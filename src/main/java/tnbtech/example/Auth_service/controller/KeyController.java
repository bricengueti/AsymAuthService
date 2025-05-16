package tnbtech.example.Auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tnbtech.example.Auth_service.service.JwtService;

@RestController
public class KeyController {

    private final JwtService jwtService;

    public KeyController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/.well-known/jwks")
    public String getPublicKey() {
        return jwtService.getPublicKeyAsPEM();
    }
}
