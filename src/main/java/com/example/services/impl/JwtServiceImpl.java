package com.example.services.impl;

import com.example.exceptions.UnauthorizedException;
import com.example.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expiration}")
    private Duration expiration;

    @Override
    public String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(getKey())
                .compact();
    }

    @Override
    public String validateToken(String token) {
        try {
            return extractSubject(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token is expired");
        } catch (Exception e) {
            throw new UnauthorizedException("Token is invalid");
        }
    }

    private String extractSubject(String token) {
        var parser = getParser();
        return (String) parser
                .parse(token)
                .getPayload();
    }

    private JwtParser getParser() {
        return Jwts.parser().verifyWith(getKey()).build();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
