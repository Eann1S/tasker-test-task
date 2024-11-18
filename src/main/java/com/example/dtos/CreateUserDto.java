package com.example.dtos;

import com.example.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record CreateUserDto(
        String email,
        String password
) implements Serializable {
}
