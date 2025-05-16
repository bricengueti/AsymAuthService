package tnbtech.example.Auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tnbtech.example.Auth_service.entities.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user.id = :userId AND rt.isRevoked = false")
    int revokeAllUserTokens(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    int revokeRefreshToken(@Param("token") String token);
    // Optional: Find by token value
    Optional<RefreshToken> findByToken(String token);

    // Optional: Find all valid refresh tokens for a user
    List<RefreshToken> findAllByUser_IdAndIsRevokedFalseAndExpiryDateAfter(Long userId, Instant now);
}