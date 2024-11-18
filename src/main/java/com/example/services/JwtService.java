package com.example.services;

public interface JwtService {
    String generateToken(String subject);

    String validateToken(String token);
}
