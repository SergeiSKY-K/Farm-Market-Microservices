package telran.java57.farmmarket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import telran.java57.farmmarket.api.SecurityErrorHandlers;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final GatewayHeadersAuthFilter gatewayHeadersAuthFilter;
    private final SecurityErrorHandlers securityErrorHandlers;

    public SecurityConfiguration(
            GatewayHeadersAuthFilter gatewayHeadersAuthFilter,
            SecurityErrorHandlers securityErrorHandlers
    ) {
        this.gatewayHeadersAuthFilter = gatewayHeadersAuthFilter;
        this.securityErrorHandlers = securityErrorHandlers;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityErrorHandlers.authEntryPoint())
                .accessDeniedHandler(securityErrorHandlers.accessDeniedHandler())
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/orders").authenticated()
                .requestMatchers(HttpMethod.POST, "/orders/*/pay").authenticated()
                .requestMatchers(HttpMethod.GET, "/orders/my").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/orders/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/orders/supplier")
                .hasAnyRole("SUPPLIER","ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/orders/moderator")
                .hasRole("MODERATOR")

                .anyRequest().denyAll()
        );


        http.addFilterBefore(
                gatewayHeadersAuthFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
