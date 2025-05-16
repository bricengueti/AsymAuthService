package tnbtech.example.Auth_service.dto;
public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType
) {
}
