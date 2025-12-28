package telran.java57.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class GatewayHeadersAuthFilter extends OncePerRequestFilter {

    @Value("${gateway.secret}")
    private String gatewaySecret;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        if ("POST".equalsIgnoreCase(method)) {
            return path.equals("/login")
                    || path.equals("/refresh")
                    || path.equals("/logout")
                    || path.equals("/users/register");
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        if (gatewaySecret == null || gatewaySecret.isBlank()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }


        String key = request.getHeader("X-Gateway-Key");
        if (!gatewaySecret.equals(key)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String login = request.getHeader("X-User-Login");
        String rolesHeader = request.getHeader("X-User-Roles");


        if (login == null || login.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }


        List<SimpleGrantedAuthority> authorities =
                (rolesHeader == null || rolesHeader.isBlank())
                        ? List.<SimpleGrantedAuthority>of()
                        : Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        var auth = new UsernamePasswordAuthenticationToken(login, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
