package com.example.startup;

import com.example.dtos.RegisterDto;
import com.example.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    private final AuthService authService;

    @Override
    public void run(String... args) {
        RegisterDto createUserDto = new RegisterDto(adminEmail, adminPassword);
        authService.registerAdmin(createUserDto);
    }
}
