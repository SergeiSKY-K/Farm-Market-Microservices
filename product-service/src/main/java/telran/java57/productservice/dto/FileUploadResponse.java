package telran.java57.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String url;
    private String fileId;
    private String error;

    public static FileUploadResponse ok(String url, String fileId) {
        return new FileUploadResponse(url, fileId, null);
    }

    public static FileUploadResponse error(String message) {
        return new FileUploadResponse(null, null, message);
    }
}