package com.example.dtos;

import com.example.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.entities.User}
 */
public record UserDto(
        String id,
        String email,
        String password,
        List<Role> roles
) implements Serializable {
}