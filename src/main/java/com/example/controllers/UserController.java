package com.example.controllers;

import com.example.dtos.UserDto;
import com.example.entities.User;
import com.example.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user profile")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved authenticated user profile")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User user) {
        UserDto profile = userService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }
}
