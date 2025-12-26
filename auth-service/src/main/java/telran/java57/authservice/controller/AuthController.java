package telran.java57.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.java57.authservice.dto.LoginDto;
import telran.java57.authservice.dto.TokenResponseDto;
import telran.java57.authservice.dto.UserDto;
import telran.java57.authservice.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        return authService.login(loginDto, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("X-User-Login") String login,
            HttpServletResponse response
    ) {
        return authService.logout(login, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@CookieValue("refreshToken") String refreshToken,
                                                    HttpServletResponse response) {
        return authService.refresh(refreshToken, response);
    }
}