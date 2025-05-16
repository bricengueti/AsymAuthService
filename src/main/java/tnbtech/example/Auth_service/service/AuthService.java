package tnbtech.example.Auth_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tnbtech.example.Auth_service.dto.LoginRequest;
import tnbtech.example.Auth_service.dto.LoginResponse;
import tnbtech.example.Auth_service.dto.RegisterRequest;
import tnbtech.example.Auth_service.dto.RegisterResponse;
import tnbtech.example.Auth_service.entities.Jwt;
import tnbtech.example.Auth_service.entities.RefreshToken;
import tnbtech.example.Auth_service.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tnbtech.example.Auth_service.repository.JwtRepository;
import tnbtech.example.Auth_service.repository.RefreshTokenRepository;
import tnbtech.example.Auth_service.repository.UserRepository;

import java.util.Date;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtRepository jwtRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtRepository jwtRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtRepository = jwtRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Vérification des doublons
        if (userRepository.existsByEmail(request.email())) {
            throw  new RuntimeException("cette utilisateur avec cette email existe deja");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("cette utilisateur avec ce username existe deja");
        }

        // Création du user
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(true);
        user.setLocked(false);

        User savedUser = userRepository.save(user);
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getCreatedAt()
        );
    }



    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // 2. Get the authenticated user
        User user = (User) authentication.getPrincipal();

        // 3. Check account status (optional, can also be done in CustomAuthProvider)
        if (!user.isActive()) {
            throw new RuntimeException("Account is inactive");
        }
        if (user.isLocked()) {
            throw new RuntimeException("Account is locked");
        }

        // 4. Revoke all existing tokens
        jwtRepository.revokeAllUserTokens(user.getId());
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        // 5. Generate new tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 6. Save tokens
        saveJwtToken(user, accessToken);
        saveRefreshToken(user, refreshToken);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationInSeconds(),
                "Bearer"
        );
    }

    public Map generateJwtByRefreshToken(String refreshToken) {
       return this.jwtService.generateTokenByRefreshToken(refreshToken);
    }

    public boolean isValide(String token) {
        Jwt jwt = jwtRepository.findByToken(token);
        return jwt != null && !jwt.isExpired() && !jwt.isRevoked();
    }

    private void saveJwtToken(User user, String token) {
        Jwt jwt = new Jwt();
        jwt.setToken(token);
        jwt.setTokenType("Bearer");
        jwt.setIssuedAt(new Date());
        jwt.setExpiresAt(jwtService.getAccessTokenExpiryDate());
        jwt.setUser(user);
        jwt.setRevoked(false);
        jwt.setExpired(false);
        jwtRepository.save(jwt);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(jwtService.getRefreshTokenExpiryDate().toInstant());
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }
}