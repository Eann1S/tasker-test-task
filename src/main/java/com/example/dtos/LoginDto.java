package com.example.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record LoginDto(
        @Email(message = "Email must be a valid email.")
        @NotBlank(message = "Email must not be empty.")
        String email,
        @NotBlank(message = "Password must not be empty.")
        String password
) implements Serializable {
}