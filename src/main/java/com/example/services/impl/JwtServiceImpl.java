package com.example.services.impl;

import com.example.exceptions.UnauthorizedException;
import com.example.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
@Slf4j
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
            log.error(e.getMessage());
            throw new UnauthorizedException("Token is expired");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnauthorizedException("Token is invalid");
        }
    }

    private String extractSubject(String token) {
        DefaultClaims payload = getPayload(token);
        return payload.getSubject();
    }

    private DefaultClaims getPayload(String token) {
        var parser = getParser();
        return (DefaultClaims) parser
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
