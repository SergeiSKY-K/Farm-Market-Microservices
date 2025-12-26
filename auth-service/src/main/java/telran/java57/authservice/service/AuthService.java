package telran.java57.authservice.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telran.java57.authservice.dto.exceptions.UserNotFoundException;
import telran.java57.authservice.utils.JwtUtil;
import telran.java57.authservice.dao.RefreshTokenRepository;
import telran.java57.authservice.dao.UserRepository;
import telran.java57.authservice.dto.LoginDto;
import telran.java57.authservice.dto.TokenResponseDto;
import telran.java57.authservice.dto.UserDto;
import telran.java57.authservice.model.CookieProps;
import telran.java57.authservice.entity.RefreshTokenEntity;
import telran.java57.authservice.model.UserAccount;

import java.time.Duration;

import static telran.java57.authservice.utils.TokenHashUtil.hash;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CookieProps cookieProps;

    private ResponseCookie buildRefreshCookie(String value) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie
                .from("refreshToken", value)
                .httpOnly(true)
                .path(cookieProps.getPath())
                .secure(cookieProps.isSecure())
                .sameSite(cookieProps.getSameSite())
                .maxAge(Duration.ofMillis(jwtUtil.getRefreshExpiration()));

        if (cookieProps.getDomain() != null && !cookieProps.getDomain().isBlank()) {
            b.domain(cookieProps.getDomain());
        }
        return b.build();
    }

    private ResponseCookie expireRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }


    public ResponseEntity<UserDto> login(LoginDto loginDto, HttpServletResponse response) {

        UserAccount userAccount = userRepository.findById(loginDto.getLogin())
                .orElseThrow(() -> new UserNotFoundException(loginDto.getLogin()));

        if (!passwordEncoder.matches(loginDto.getPassword(), userAccount.getPassword())) {
            throw new BadCredentialsException("Incorrect login or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userAccount.getLogin());

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        refreshTokenRepository.deleteById(userAccount.getLogin());
        refreshTokenRepository.save(new RefreshTokenEntity(userAccount.getLogin(), hash(refreshToken)));

        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(refreshToken).toString());

        UserDto dto = modelMapper.map(userAccount, UserDto.class);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(dto);
    }


    public ResponseEntity<Void> logout(String login, HttpServletResponse response) {
        refreshTokenRepository.deleteById(login);
        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshCookie().toString());
        return ResponseEntity.noContent().build();
    }



    public ResponseEntity<TokenResponseDto> refresh(String oldRefreshToken, HttpServletResponse response) {

        String username = jwtUtil.extractUsername(oldRefreshToken);

        RefreshTokenEntity stored = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        String hashed = hash(oldRefreshToken);

        if (!hashed.equals(stored.getHashedRefreshToken()) ||
                !jwtUtil.validateRefreshToken(oldRefreshToken)) {

            throw new RuntimeException("Invalid refresh token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        refreshTokenRepository.save(new RefreshTokenEntity(username, hash(newRefreshToken)));

        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(newRefreshToken).toString());

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
    }

}
