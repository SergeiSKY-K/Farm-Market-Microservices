package telran.java57.productservice.configuration;

import io.imagekit.sdk.ImageKit;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageKitConfig {

    @Value("${imagekit.publicKey}")  private String publicKey;
    @Value("${imagekit.privateKey}") private String privateKey;
    @Value("${imagekit.urlEndpoint}") private String urlEndpoint;

    @PostConstruct
    public void init() {
        ImageKit.getInstance().setConfig(
                new io.imagekit.sdk.config.Configuration(publicKey, privateKey, urlEndpoint)
        );
    }
}