package telran.java57.productservice.security;

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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final GatewayHeadersAuthFilter gatewayHeadersAuthFilter;

    public SecurityConfiguration(GatewayHeadersAuthFilter gatewayHeadersAuthFilter) {
        this.gatewayHeadersAuthFilter = gatewayHeadersAuthFilter;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // SUPPLIER
                .requestMatchers(HttpMethod.GET, "/products/my-products").hasAnyRole("SUPPLIER","ADMINISTRATOR")
                .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("SUPPLIER","ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/products/*").hasAnyRole("SUPPLIER","ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/products/*").hasAnyRole("SUPPLIER","ADMINISTRATOR")

                // MODERATOR
                .requestMatchers(HttpMethod.PUT, "/products/*/status").hasAnyRole("MODERATOR","ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/products/blocked").hasAnyRole("MODERATOR","ADMINISTRATOR")

                // PUBLIC
                .requestMatchers(HttpMethod.GET, "/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/category/**").permitAll()


                .requestMatchers(HttpMethod.GET, "/files/**").permitAll()
                // UPLOAD
                .requestMatchers(HttpMethod.POST, "/files").hasAnyRole("SUPPLIER","ADMINISTRATOR")

                .anyRequest().authenticated()
        );


        http.addFilterBefore(gatewayHeadersAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
