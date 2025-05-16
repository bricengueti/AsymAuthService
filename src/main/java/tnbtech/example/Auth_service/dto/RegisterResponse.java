package tnbtech.example.Auth_service.dto;

import tnbtech.example.Auth_service.entities.User;

import java.time.LocalDateTime;
import java.util.Date;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Date createdAt
) {
}