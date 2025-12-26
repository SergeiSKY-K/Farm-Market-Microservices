package telran.java57.authservice.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    private String secret;
    private long accessExpiration;
    @Getter
    private long refreshExpiration;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        try {
            Dotenv dotenv = Dotenv.load();
            secret = dotenv.get("JWT_SECRET", System.getenv("JWT_SECRET"));
            accessExpiration = Long.parseLong(dotenv.get("JWT_ACCESS_EXPIRATION", System.getenv("JWT_ACCESS_EXPIRATION")));
            refreshExpiration = Long.parseLong(dotenv.get("JWT_REFRESH_EXPIRATION", System.getenv("JWT_REFRESH_EXPIRATION")));
        } catch (Exception e) {
            secret = System.getenv("JWT_SECRET");
            accessExpiration = Long.parseLong(System.getenv("JWT_ACCESS_EXPIRATION"));
            refreshExpiration = Long.parseLong(System.getenv("JWT_REFRESH_EXPIRATION"));
        }

        algorithm = Algorithm.HMAC256(secret);
    }


    public String generateAccessToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("roles", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiration))
                .sign(algorithm);
    }


    public String generateRefreshToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .sign(algorithm);
    }


    public String extractUsername(String token) {
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

    public boolean validateRefreshToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            return jwt.getExpiresAt().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
