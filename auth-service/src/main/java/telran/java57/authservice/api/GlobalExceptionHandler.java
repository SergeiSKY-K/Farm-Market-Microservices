package telran.java57.authservice.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import telran.java57.authservice.api.ApiError;
import telran.java57.authservice.dto.exceptions.UserExistsException;
import telran.java57.authservice.dto.exceptions.UserNotFoundException;

import java.time.Instant;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(st)
                .body(base(req, st, "USER_NOT_FOUND")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ApiError> handleUserExists(UserExistsException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.CONFLICT;
        return ResponseEntity.status(st)
                .body(base(req, st, "USER_EXISTS")
                        .message("User already exists")
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st)
                .body(base(req, st, "BAD_REQUEST")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;

        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .rejectedValue(fe.getRejectedValue())
                        .build())
                .toList();

        return ResponseEntity.status(st)
                .body(base(req, st, "VALIDATION_ERROR")
                        .message("Validation failed")
                        .details(details)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(st)
                .body(base(req, st, "INTERNAL_ERROR")
                        .message(ex.getMessage())
                        .build());
    }
}
