package telran.java57.farmmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class FarmMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmMarketApplication.class, args);
    }

}
