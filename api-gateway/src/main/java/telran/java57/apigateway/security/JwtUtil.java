package telran.java57.apigateway.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("Missing jwt.secret");
        }
        algorithm = Algorithm.HMAC256(secret);
    }

    public DecodedJWT validate(String token) {
        return JWT.require(algorithm).build().verify(token);
    }
}