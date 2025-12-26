package telran.java57.apigateway.security;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("http://auth-service:8081")
                )

                .route("product-service", r -> r
                        .path("/products/**")
                        .uri("http://product-service:8080")
                )

                .route("farm-market", r -> r
                        .path("/farm/**", "/orders/**")
                        .uri("http://farm-market:8080")
                )

                .route("auth-users", r -> r
                        .path("/users/**")
                        .uri("http://auth-service:8081")
                )
                .route("files", r -> r
                        .path("/files/**")
                        .uri("http://product-service:8080")
                )


                .build();
    }
}