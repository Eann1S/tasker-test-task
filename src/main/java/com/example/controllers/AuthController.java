package com.example.controllers;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.dtos.UserDto;
import com.example.entities.User;
import com.example.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "JWT access token")
    public ResponseEntity<JwtDto> login(@RequestBody LoginDto loginDto) {
        JwtDto jwt = authService.login(loginDto);
        return ResponseEntity.ok().body(jwt);
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register")
    @ApiResponse(responseCode = "201", description = "Successfully registered user")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) {
        UserDto user = authService.registerUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Only authenticated user")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        authService.logout(user);
        return ResponseEntity.ok().build();
    }
}
