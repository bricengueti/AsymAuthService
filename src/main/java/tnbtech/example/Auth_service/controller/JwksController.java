package tnbtech.example.Auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/keys")
public class JwksController {

    private final KeyPair keyPair;

    // Injection par constructeur (recommandé)
    @Autowired
    public JwksController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        return Map.of(
                "keys", List.of(Map.of(
                        "kty", "RSA",
                        "kid", "current-key-1",
                        "n", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray()),
                        "e", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray()),
                        "alg", "RS256",
                        "use", "sig"
                ))
        );
    }
}