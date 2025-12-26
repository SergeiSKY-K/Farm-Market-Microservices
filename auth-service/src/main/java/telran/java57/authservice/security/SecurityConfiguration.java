package telran.java57.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final GatewayHeadersAuthFilter gatewayHeadersAuthFilter;

    public SecurityConfiguration(GatewayHeadersAuthFilter gatewayHeadersAuthFilter) {
        this.gatewayHeadersAuthFilter = gatewayHeadersAuthFilter;
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // public auth
                .requestMatchers("/auth/login", "/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()

                // self-service
                .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/users/password").authenticated()
                .requestMatchers(HttpMethod.GET,  "/users/user/*").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/users/user/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/users/user/*").authenticated()

                // admin / moderator
                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/users/suppliers").hasAnyRole("MODERATOR","ADMINISTRATOR")

                // role management
                .requestMatchers(HttpMethod.PUT, "/users/user/*/role/SUPPLIER").hasAnyRole("MODERATOR","ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/users/user/*/role/SUPPLIER").hasAnyRole("MODERATOR","ADMINISTRATOR")

                .requestMatchers(HttpMethod.PUT, "/users/user/*/role/*").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/users/user/*/role/*").hasRole("ADMINISTRATOR")

                .anyRequest().denyAll()
        );

        http.addFilterBefore(gatewayHeadersAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}