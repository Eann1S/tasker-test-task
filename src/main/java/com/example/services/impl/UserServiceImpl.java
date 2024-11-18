package com.example.services.impl;

import com.example.dtos.CreateUserDto;
import com.example.dtos.UserDto;
import com.example.entities.Role;
import com.example.entities.User;
import com.example.exceptions.NotFoundException;
import com.example.mapper.UserMapper;
import com.example.repositories.UserRepository;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    @Override
    public UserDto getUserProfile(String id) {
        User user = findById(id);
        return mapper.toDto(user);
    }

    @Override
    public User createUser(CreateUserDto createUserDto) {
        User user = mapper.toEntity(createUserDto);
        user.setRoles(Collections.singletonList(Role.USER));
        return userRepository.save(user);
    }
}
