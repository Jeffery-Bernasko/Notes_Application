package org.example.notes_application.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.notes_application.dto.AuthRequest;
import org.example.notes_application.dto.AuthResponse;
import org.example.notes_application.security.JwtProvider;
import org.example.notes_application.service.AuthService;
import org.example.notes_application.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid AuthRequest request,HttpServletResponse response) {
        return getToken(request, response);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        return getToken(request, response);
    }

    private ResponseEntity<AuthResponse> getToken(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        String token = authService.login(request).getToken();

        ResponseCookie jwtCookie = ResponseCookie.from("jwt",token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        return ResponseEntity.ok().build();
    }
}
