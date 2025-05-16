package tnbtech.example.Auth_service.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import tnbtech.example.Auth_service.entities.RefreshToken;
import tnbtech.example.Auth_service.entities.User;
import tnbtech.example.Auth_service.repository.RefreshTokenRepository;
import tnbtech.example.Auth_service.repository.UserRepository;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final KeyPair keyPair;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtService(
            KeyPair keyPair, // Injecté depuis RsaKeyConfig
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {
        this.keyPair = keyPair;
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("user_id", user.getId());
        return buildToken(claims, user, jwtExpirationMs);
    }
  public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setHeaderParam("kid", "rsa-key-1") // optionnel mais recommandé
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(keyPair.getPublic())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        Collection<GrantedAuthority> authorities = roles != null
                ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public String getPublicKeyAsPEM() {
        RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();
        return "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pub.getEncoded()) +
                "\n-----END PUBLIC KEY-----";
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }


    // Updated methods to use the correct field names:
    public long getAccessTokenExpirationInSeconds() {
        return jwtExpirationMs / 1000;
    }

    public Date getAccessTokenExpiryDate() {
        return new Date(System.currentTimeMillis() + jwtExpirationMs);
    }

    public Date getRefreshTokenExpiryDate() {
        return new Date(System.currentTimeMillis() + refreshExpirationMs);
    }


    public Map<String, String> generateTokenByRefreshToken(String refreshToken) {
        try {
            // 1. Validate token structure and signature
            if (!validateToken(refreshToken)) {
                throw new JwtException("Invalid refresh token");
            }

            // 2. Parse claims
            Claims claims = Jwts.parser()
                    .setSigningKey(keyPair.getPublic())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // 3. Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Refresh token expired");
            }

            // 4. Verify token exists in database and isn't revoked
            Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
            if (storedToken.isEmpty() || storedToken.get().isRevoked()) {
                throw new JwtException("Refresh token revoked or not found");
            }

            // 5. Get user from database
            User user = userRepository.findById(claims.get("user_id", Long.class))
                    .orElseThrow(() -> new JwtException("User not found"));

            // 6. Generate new tokens
            String newAccessToken = generateAccessToken(user);
            String newRefreshToken = generateRefreshToken(user);

            // 7. Update refresh token in database (revoke old, save new)
            refreshTokenRepository.revokeRefreshToken(refreshToken);
            saveRefreshToken(user, newRefreshToken);

            // 8. Return both tokens
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            return tokens;

        } catch (JwtException e) {
            throw new JwtException("Refresh token processing failed: " + e.getMessage());
        }
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(getRefreshTokenExpiryDate().toInstant());
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }
}
