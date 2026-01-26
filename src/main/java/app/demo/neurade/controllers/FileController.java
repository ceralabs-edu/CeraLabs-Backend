package app.demo.neurade.controllers;

import app.demo.neurade.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload-pfp")
    public ResponseEntity<?> uploadPfp(MultipartFile file) {
        String fileUrl = fileService.uploadPfp(file);
        return ResponseEntity.ok(
                Map.of(
                        "message", "File uploaded successfully",
                        "url", fileUrl
                )
        );
    }
}
