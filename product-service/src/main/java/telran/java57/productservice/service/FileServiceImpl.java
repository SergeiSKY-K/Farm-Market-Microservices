package telran.java57.productservice.service;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import telran.java57.productservice.dto.FileUploadResponse;

import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    @Override
    public FileUploadResponse uploadValidated(MultipartFile file, String fileName) throws Exception {

        if (file == null || file.isEmpty()) {
            return FileUploadResponse.error("File is empty");
        }

        String mime = file.getContentType();
        if (mime == null || !ALLOWED_MIME.contains(mime.toLowerCase())) {
            return FileUploadResponse.error("Only JPEG / PNG / WEBP allowed");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            return FileUploadResponse.error("File size exceeds 10MB");
        }

        String safeName = sanitize(
                (fileName == null || fileName.isBlank())
                        ? file.getOriginalFilename()
                        : fileName
        );

        FileCreateRequest request = new FileCreateRequest(file.getBytes(), safeName);
        request.setUseUniqueFileName(true);

        Result result = ImageKit.getInstance().upload(request);

        return FileUploadResponse.ok(result.getUrl(), result.getFileId());
    }

    private String sanitize(String name) {
        if (name == null) return "image";

        String base = name.replace("\\", "/");
        base = base.substring(base.lastIndexOf('/') + 1);
        base = base.replaceAll("[\\r\\n\\t]", "_")
                .replaceAll("[^A-Za-z0-9._-]", "_");

        return base.isBlank() ? "image" : base;
    }
}
