package com.example.controllers;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.dtos.UserDto;
import com.example.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody LoginDto loginDto) {
        JwtDto jwt = authService.login(loginDto);
        return ResponseEntity.ok().body(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) {
        UserDto user = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
