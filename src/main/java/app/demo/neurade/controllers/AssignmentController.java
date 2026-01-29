package app.demo.neurade.controllers;

import app.demo.neurade.services.AssignmentJudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assignment")
public class AssignmentController {

    private final AssignmentJudgeService assignmentJudgeService;

    @PostMapping("/judge")
    public ResponseEntity<?> judgeAssignment(
            @RequestParam Map<String, MultipartFile> answers
    ) {
        Map<String, String> res = assignmentJudgeService.checkAnswers(answers);
        return ResponseEntity.ok(res);
    }
}
