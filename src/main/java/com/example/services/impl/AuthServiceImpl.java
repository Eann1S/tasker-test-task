package com.example.services.impl;

import com.example.dtos.*;
import com.example.entities.User;
import com.example.exceptions.ConflictException;
import com.example.exceptions.UnauthorizedException;
import com.example.mapper.UserMapper;
import com.example.services.AuthService;
import com.example.services.JwtService;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public JwtDto login(LoginDto loginDto) {
        User user = userService.findByEmail(loginDto.email());
        boolean isPasswordValid = isPasswordValid(loginDto.password(), user.getPassword());
        if (!isPasswordValid) {
            throw new UnauthorizedException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new JwtDto(token);
    }

    @Override
    public UserDto register(RegisterDto registerDto) {
        boolean exists = userService.existsByEmail(registerDto.email());
        if (exists) {
            throw new ConflictException("Email already in use");
        }
        String encodedPassword = passwordEncoder.encode(registerDto.password());
        CreateUserDto createUserDto = new CreateUserDto(registerDto.email(), encodedPassword);
        User user = userService.createUser(createUserDto);
        return userMapper.toDto(user);
    }

    private boolean isPasswordValid(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
