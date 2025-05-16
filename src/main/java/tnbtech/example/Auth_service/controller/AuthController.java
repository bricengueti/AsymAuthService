package tnbtech.example.Auth_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tnbtech.example.Auth_service.dto.LoginRequest;
import tnbtech.example.Auth_service.dto.LoginResponse;
import tnbtech.example.Auth_service.dto.RegisterRequest;
import tnbtech.example.Auth_service.dto.RegisterResponse;
import tnbtech.example.Auth_service.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@Valid @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, String> newTokens = authService.generateJwtByRefreshToken(refreshToken);
        return ResponseEntity.ok(newTokens);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = authService.isValide(token);
        return ResponseEntity.ok(isValid);
    }
}
