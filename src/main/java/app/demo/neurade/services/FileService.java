package app.demo.neurade.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadPfp(MultipartFile file);
}
