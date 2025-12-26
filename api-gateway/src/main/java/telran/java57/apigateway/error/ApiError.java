package telran.java57.apigateway.error;


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
    String message;
    String path;
    String traceId;
    List<FieldError> details;

    @Value
    @Builder
    public static class FieldError {
        String field;
        String message;
        Object rejectedValue;
    }
}