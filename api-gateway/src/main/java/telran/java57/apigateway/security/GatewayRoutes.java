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

                .route("auth-service-auth", r -> r
                        .path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("https://auth-service-f376.onrender.com")
                )

                .route("auth-service-users", r -> r
                        .path("/users/**")
                        .uri("https://auth-service-f376.onrender.com")
                )

                .route("product-service", r -> r
                        .path("/products/**", "/files/**")
                        .uri("https://product-service-rvbw.onrender.com")
                )

                .route("farm-market", r -> r
                        .path("/farm/**", "/orders/**")
                        .uri("https://farm-market-p5da.onrender.com")
                )

                .build();
    }
}
