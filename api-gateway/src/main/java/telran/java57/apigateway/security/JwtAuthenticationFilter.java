package telran.java57.apigateway.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;


import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Value("${gateway.secret}")
    private String gatewaySecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        if (method == HttpMethod.OPTIONS
                || path.startsWith("/auth/")
                || path.equals("/users/register")
                || path.startsWith("/files/")) {
            return chain.filter(exchange);
        }


        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            DecodedJWT jwt = jwtUtil.validate(token);

            String username = jwt.getSubject();

            List<String> roles = Optional
                    .ofNullable(jwt.getClaim("roles").asList(String.class))
                    .orElse(List.of());


            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .headers(h -> {
                        h.remove("X-User-Login");
                        h.remove("X-User-Roles");
                        h.remove("X-Gateway-Key");

                        h.add("X-User-Login", username);
                        h.add("X-User-Roles", String.join(",", roles));
                        h.add("X-Gateway-Key", gatewaySecret);
                    })
                    .build();

            var authorities = roles.stream()
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
