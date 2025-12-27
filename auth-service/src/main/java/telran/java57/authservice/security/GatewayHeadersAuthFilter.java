package telran.java57.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;


        if (path.startsWith("/auth/")) return true;
        if (path.equals("/login") || path.equals("/refresh") || path.equals("/logout")) return true;


        if (path.equals("/users/register") || path.equals("/auth/users/register")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String login = request.getHeader("X-User-Login");
        String rolesHeader = request.getHeader("X-User-Roles");

        if (login != null && !login.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            List<SimpleGrantedAuthority> authorities =
                    (rolesHeader == null || rolesHeader.isBlank())
                            ? List.of()
                            : Arrays.stream(rolesHeader.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            var auth = new UsernamePasswordAuthenticationToken(login, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
