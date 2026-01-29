package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.AssignmentQuestionDTO;
import app.demo.neurade.domain.dtos.requests.AssignmentCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.domain.models.assignment.QuestionType;
import app.demo.neurade.infrastructures.assignment_ocr.AssignmentOcrClient;
import app.demo.neurade.infrastructures.assignment_ocr.responses.ExtractResponse;
import app.demo.neurade.infrastructures.repositories.AssignmentRepository;
import app.demo.neurade.infrastructures.repositories.ClassRepository;
import app.demo.neurade.services.AssignmentQuestionPersistenceService;
import app.demo.neurade.services.AssignmentService;
import app.demo.neurade.services.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final ClassRepository classRepository;
    private final AssignmentRepository assignmentRepository;
    private final Mapper mapper;
    private final AssignmentOcrClient ocrClient;
    private final FileService fileService;
    private final AssignmentQuestionPersistenceService assignmentQuestionPersistenceService;

    @Override
    @Transactional
    public AssignmentDTO createAssignment(Long classId, AssignmentCreationRequest req) {
        Classroom classroom = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Assignment assignment = Assignment.builder()
                .classroom(classroom)
                .title(req.getTitle())
                .description(req.getDescription())
                .deadline(req.getDeadline())
                .build();

        assignment = assignmentRepository.save(assignment);

        return mapper.toDto(assignment);
    }

    @Override
    public List<AssignmentQuestionDTO> createAndProcessPDF(UUID assignmentId, List<MultipartFile> files) {
        MultipartFile file = files.getFirst();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        log.info("Processing file {}", file.getOriginalFilename());
        ExtractResponse response = ocrClient.callExtract(file);
        log.info("Received OCR response with {} questions", response.getQuestions().size());

        List<AssignmentQuestion> result = new ArrayList<>();

        for (var entry : response.getQuestions().entrySet()) {
            String key = entry.getKey();

            List<ExtractResponse.QuestionRegionDTO> regions = entry.getValue();
            if (regions == null || regions.isEmpty()) {
                log.warn("Question {} has no regions, skipping", key);
                continue;
            }

            ExtractResponse.QuestionRegionDTO region = regions.getFirst();

            List<Object> coor = region.getCoor();
            if (coor == null || coor.size() < 5) {
                log.warn("Invalid coor for question {}, skipping", key);
                continue;
            }

            String questionBase64 = (String) coor.get(4);

            String questionImageUrl = uploadBase64ToMinio(questionBase64);

            String correctAnswer = null;
            if (response.getCorrectOptions() != null) {
                correctAnswer = response.getCorrectOptions().get(key);
            }
            correctAnswer = normalizeAnswer(correctAnswer);

            String explainImageUrl = null;
            if (response.getExplains() != null
                    && response.getExplains().containsKey(key)) {

                List<List<Object>> explainRegions =
                        response.getExplains().get(key);

                if (explainRegions != null && !explainRegions.isEmpty()) {
                    List<Object> explainCoor = explainRegions.getFirst();
                    if (explainCoor.size() >= 5) {
                        String explainBase64 = (String) explainCoor.get(4);
                        explainImageUrl = uploadBase64ToMinio(explainBase64);
                    }
                }
            }

            AssignmentQuestion question = toEntity(
                    key,
                    region,
                    correctAnswer,
                    questionImageUrl,
                    explainImageUrl
            );

            result.add(question);
        }

        assignmentQuestionPersistenceService.saveAssignmentWithQuestions(
                assignmentId,
                result
        );

        return result.stream()
                .map(mapper::toDto)
                .toList();
    }

    private AssignmentQuestion toEntity(
            String key,
            ExtractResponse.QuestionRegionDTO region,
            String correctAnswer,
            String questionImageUrl,
            String explainImageUrl
    ) {

        QuestionType type = detectType(correctAnswer);

        return AssignmentQuestion.builder()
                .questionKey(key)
                .questionType(type)
                .questionImageUrl(questionImageUrl)
                .correctAnswer(correctAnswer)
                .explainImageUrl(explainImageUrl)
                .page(region.getPage())
                .build();
    }

    private String normalizeAnswer(String answer) {
        if (answer == null) return null;
        return answer.trim().toUpperCase(new Locale("vi", "VN"));
    }

    private QuestionType detectType(String correctAnswer) {
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return QuestionType.SHORT_ANSWER;
        }

        return switch (correctAnswer.trim()) {
            case "A", "B", "C", "D" -> QuestionType.MCQ;
            case "Đ", "S" -> QuestionType.TF;
            default -> QuestionType.SHORT_ANSWER;
        };
    }

    private String uploadBase64ToMinio(String dataUrl) {
        return fileService.uploadBase64Image(dataUrl, "questions");
    }
}
