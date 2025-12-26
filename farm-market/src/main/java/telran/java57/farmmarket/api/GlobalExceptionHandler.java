package telran.java57.farmmarket.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError.ApiErrorBuilder base(HttpServletRequest req, HttpStatus status, String code) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .path(req.getRequestURI())
                .traceId(UUID.randomUUID().toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        var st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(
                base(req, st, "BAD_REQUEST").message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> ApiError.FieldError.builder()
                        .field(f.getField())
                        .message(f.getDefaultMessage())
                        .rejectedValue(f.getRejectedValue())
                        .build()
                )
                .toList();

        var st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(
                base(req, st, "VALIDATION_ERROR")
                        .message("Validation failed")
                        .details(errors)
                        .build()
        );
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest req
    ) {
        var st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(
                base(req, st, "MALFORMED_JSON")
                        .message("Malformed JSON or wrong payload")
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.toString(), ex);

        var st = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(st).body(
                base(req, st, "INTERNAL_ERROR")
                        .message("Unexpected server error")
                        .build()
        );
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", ex.getMessage()
                ));
    }
}