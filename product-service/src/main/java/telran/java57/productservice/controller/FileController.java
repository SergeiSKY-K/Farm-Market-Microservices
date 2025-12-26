package telran.java57.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import telran.java57.productservice.dto.FileUploadResponse;
import telran.java57.productservice.service.FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName
    ) throws Exception {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(FileUploadResponse.error("Empty file"));
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(413).body(FileUploadResponse.error("File too large"));
        }

        var result = fileService.uploadValidated(file, fileName);
        return ResponseEntity.ok(result);
    }
}