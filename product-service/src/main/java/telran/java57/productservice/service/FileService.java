package telran.java57.productservice.service;

import org.springframework.web.multipart.MultipartFile;
import telran.java57.productservice.dto.FileUploadResponse;

public interface FileService {
    FileUploadResponse uploadValidated(MultipartFile file, String fileName) throws Exception;
}