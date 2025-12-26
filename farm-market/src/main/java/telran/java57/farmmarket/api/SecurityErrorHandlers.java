package telran.java57.farmmarket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint authEntryPoint() {
        return (req, res, ex) -> {
            var error = ApiError.builder()
                    .timestamp(Instant.now())
                    .status(401)
                    .error("Unauthorized")
                    .code("UNAUTHORIZED")
                    .message(ex.getMessage())
                    .path(req.getRequestURI())
                    .traceId(UUID.randomUUID().toString())
                    .build();

            res.setStatus(401);
            res.setContentType("application/json");
            objectMapper.writeValue(res.getWriter(), error);
        };
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (req, res, ex) -> {
            var error = ApiError.builder()
                    .timestamp(Instant.now())
                    .status(403)
                    .error("Forbidden")
                    .code("FORBIDDEN")
                    .message("Access denied")
                    .path(req.getRequestURI())
                    .traceId(UUID.randomUUID().toString())
                    .build();

            res.setStatus(403);
            res.setContentType("application/json");
            objectMapper.writeValue(res.getWriter(), error);
        };
    }
}