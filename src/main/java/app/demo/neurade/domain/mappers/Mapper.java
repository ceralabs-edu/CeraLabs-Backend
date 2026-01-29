package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.*;
import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.chatbot_llm.responses.VerifyKeyResponse;
import app.demo.neurade.domain.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .verified(user.getVerified())
                .build();
    }

    public UserInfoDTO toDto(UserInformation userInformation) {
        return UserInfoDTO.builder()
                .firstName(userInformation.getFirstName())
                .lastName(userInformation.getLastName())
                .cityCode(userInformation.getCity().getCode())
                .subDistrictCode(userInformation.getSubDistrict().getCode())
                .addressDetail(userInformation.getAddressDetail())
                .school(userInformation.getSchool())
                .grade(userInformation.getGrade())
                .bio(userInformation.getBio())
                .favoriteSubjects(userInformation.getFavoriteSubjects())
                .dateOfBirth(userInformation.getDateOfBirth())
                .avatarUrl(userInformation.getAvatarImage())
                .build();
    }

    public ClassDTO toDto(Classroom classroom) {
        return ClassDTO.builder()
                .classId(classroom.getId())
                .name(classroom.getName())
                .creatorId(classroom.getCreator().getId())
                .description(classroom.getDescription())
                .build();
    }

    public ValidateKeyDTO toDto(VerifyKeyResponse response) {
        return ValidateKeyDTO.builder()
                .isValid(response.isValid())
                .models(response.getModels())
                .errorMessage(response.getErrorMessage())
                .build();
    }

    public AIPackageInstanceDTO toDto(AIPackageInstance aiPackageInstance, AIPackage aiPackage) {
        var builder = AIPackageInstanceDTO.builder()
                .instanceId(aiPackageInstance.getId())
                .aiPackageId(aiPackageInstance.getAiPackage().getId())
                .purchaserId(aiPackageInstance.getBuyer().getId())
                .remainingToken(aiPackageInstance.getRemainingToken())
                .totalToken(aiPackage.getTotalToken())
                .purchaseDate(aiPackageInstance.getPurchaseDate())
                .expiryDate(aiPackageInstance.getExpiryDate());

        if (aiPackageInstance.getClassRoom() != null) {
            builder.classId(aiPackageInstance.getClassRoom().getId());
        }

        return builder.build();
    }

    public AIPackageDTO toDto(AIPackage aiPackage) {
        return AIPackageDTO.builder()
                .id(aiPackage.getId())
                .name(aiPackage.getName())
                .price(aiPackage.getPrice())
                .totalToken(aiPackage.getTotalToken())
                .tokenRateLimit(aiPackage.getTokenRateLimit())
                .model(aiPackage.getModel())
                .durationInDays(aiPackage.getDurationInDays())
                .description(aiPackage.getDescription())
                .build();
    }

    public AssignmentDTO toDto(Assignment assignment) {
        return AssignmentDTO.builder()
                .id(assignment.getId())
                .classId(assignment.getClassroom().getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .build();
    }

    public AssignmentDTO toDto(Assignment assignment, List<AssignmentQuestion> questions, boolean showCorrectAnswers) {
        var builder = AssignmentDTO.builder()
                .id(assignment.getId())
                .classId(assignment.getClassroom().getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .questions(
                        questions.stream()
                                .map(q -> toDto(q, showCorrectAnswers))
                                .toList()
                );

        return builder.build();
    }

    public AssignmentQuestionDTO toDto(AssignmentQuestion assignmentQuestion, boolean showCorrectAnswer) {
        var builder = AssignmentQuestionDTO.builder()
                .id(assignmentQuestion.getId())
                .questionKey(assignmentQuestion.getQuestionKey())
                .questionType(assignmentQuestion.getQuestionType())
                .questionUrl(assignmentQuestion.getQuestionImageUrl())
                .optionUrls(assignmentQuestion.getAnswerImageUrls())
                .explainUrl(assignmentQuestion.getExplainImageUrl());

        if (showCorrectAnswer) {
            builder.answer(assignmentQuestion.getCorrectAnswer());
        }

        return builder.build();
    }
}
