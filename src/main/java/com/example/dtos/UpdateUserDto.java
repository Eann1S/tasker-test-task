package com.example.dtos;

import com.example.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UpdateUserDto(
        @Email(message = "Email must be a valid email.")
        String email
) implements Serializable {
}