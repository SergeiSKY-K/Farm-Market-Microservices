package telran.java57.authservice.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class ApiError {
    Instant timestamp;
    int status;
    String error;
    String code;
    String message;
    String path;
    String traceId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<FieldError> details;

    @Value @Builder
    public static class FieldError {
        String field;
        String message;
        Object rejectedValue;
    }
}