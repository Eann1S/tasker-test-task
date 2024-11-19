package com.example.services;

import com.example.dtos.CreateUserDto;
import com.example.dtos.UserDto;
import com.example.entities.Role;
import com.example.entities.User;

import java.util.Set;

public interface UserService {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findById(String id);

    UserDto getUserProfileByEmail(String email);

    User createUser(CreateUserDto createUserDto, Set<Role> roles);
}
