package com.example.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record RegisterDto(
        @Email(message = "Email must be a valid email.")
        @NotBlank(message = "Email must not be empty.")
        String email,
        @Length(message = "Password must be between 4-16 characters long.", min = 4, max = 16)
        String password
) implements Serializable {
}