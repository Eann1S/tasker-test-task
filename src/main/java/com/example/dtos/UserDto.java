package com.example.dtos;

import com.example.entities.Role;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.entities.User}
 */
public record UserDto(
        String id,
        String email,
        List<Role> roles
) implements Serializable {
}