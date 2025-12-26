package telran.java57.authservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth.cookie")
public class CookieProps {
    private boolean secure = true;
    private String sameSite = "None";
    private String path = "/";
    private long maxAgeMs = 604800000;
    private String domain;
}
