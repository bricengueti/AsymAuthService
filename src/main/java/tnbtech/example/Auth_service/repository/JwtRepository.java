package tnbtech.example.Auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tnbtech.example.Auth_service.entities.Jwt;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {

    @Modifying
    @Query("UPDATE Jwt j SET j.isRevoked = true WHERE j.user.id = :userId AND j.isRevoked = false")
    int revokeAllUserTokens(@Param("userId") Long userId);

    // Optional: Find all valid tokens for a user
    List<Jwt> findAllByUser_IdAndIsRevokedFalseAndIsExpiredFalse(Long userId);

    Jwt findByToken(String token);
}