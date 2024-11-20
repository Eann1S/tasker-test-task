package com.example.services.impl;

import com.example.exceptions.UnauthorizedException;
import com.example.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expiration}")
    private Duration expiration;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String generateToken(String subject) {
        String token = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(getKey())
                .compact();
        redisTemplate.opsForValue().set(subject, token, expiration);
        return token;
    }

    @Override
    public String validateToken(String token) {
        try {
            String subject = extractSubject(token);
            if (doesNotExist(subject)) {
                throw new RuntimeException("Token not found");
            }
            return subject;
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new UnauthorizedException("Token is expired");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnauthorizedException("Token is invalid");
        }
    }

    private boolean doesNotExist(String token) {
        return Boolean.FALSE.equals(redisTemplate.hasKey(token));
    }

    @Override
    public void expireToken(String subject) {
        redisTemplate.opsForValue().getAndDelete(subject);
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
