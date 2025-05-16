package tnbtech.example.Auth_service.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "jwt_tokens")
public class Jwt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2000)
    private String token;

    @Column(name = "token_type", nullable = false)
    private String tokenType = "Bearer";

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired = false;

    @Column(name = "issued_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date issuedAt;

    @Column(name = "expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructeurs
    public Jwt() {
    }

    public Jwt(String token, Date issuedAt, Date expiresAt, String keyId, User user) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Jwt{" +
                "id=" + id +
                ", tokenType='" + tokenType + '\'' +
                ", isRevoked=" + isRevoked +
                ", isExpired=" + isExpired +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}