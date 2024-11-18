package com.example.services;

import com.example.dtos.CreateUserDto;
import com.example.dtos.UserDto;
import com.example.entities.User;

public interface UserService {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findById(String id);

    UserDto getUserProfile(String id);

    User createUser(CreateUserDto createUserDto);
}
