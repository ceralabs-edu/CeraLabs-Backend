package app.demo.neurade.controllers;

import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.AssignmentJudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assignment")
public class AssignmentController {

    private final AssignmentJudgeService assignmentJudgeService;
    private final Mapper mapper;

    @PostMapping("/judge")
    public ResponseEntity<?> judgeAssignment(
            @RequestParam Map<String, MultipartFile> answers
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        Map<String, String> res = assignmentJudgeService.checkAnswers(
                userDetails.getUser(),
                answers
        );
        return ResponseEntity.ok(
                Map.of(
                        "message", "Assignment answers enqueued for judgement",
                        "jobIds", res
                )
        );
    }

    @GetMapping("/judge/job-status/{jobId}")
    public ResponseEntity<?> getAssignmentJudgeJobStatus(
            @PathVariable UUID jobId
    ) {
        var result = assignmentJudgeService.getAssignmentJob(jobId);
        return ResponseEntity.ok(mapper.toDto(result));
    }

    @GetMapping("/{assignmentId}/judgement")
    public ResponseEntity<?> getAssignmentJudgement(
            @PathVariable String assignmentId
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        var result = assignmentJudgeService.getJudgementResults(
                userDetails.getUser(),
                UUID.fromString(assignmentId)
        );
        return ResponseEntity.ok(result);
    }
}
