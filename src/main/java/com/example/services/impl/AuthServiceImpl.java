package com.example.services.impl;

import com.example.dtos.*;
import com.example.entities.Role;
import com.example.entities.User;
import com.example.exceptions.ConflictException;
import com.example.exceptions.UnauthorizedException;
import com.example.mapper.UserMapper;
import com.example.services.AuthService;
import com.example.services.JwtService;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

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
        setAuthentication(user);
        String token = jwtService.generateToken(user.getEmail());
        return new JwtDto(token);
    }

    @Override
    public UserDto registerUser(RegisterDto registerDto) {
        validateExistingUserByEmail(registerDto.email());
        return register(registerDto, Set.of(Role.ROLE_USER));
    }

    @Override
    public UserDto registerAdmin(RegisterDto registerDto) {
        return register(registerDto, Set.of(Role.ROLE_ADMIN));
    }

    private UserDto register(RegisterDto registerDto, Set<Role> roles) {
        String encodedPassword = passwordEncoder.encode(registerDto.password());
        CreateUserDto createUserDto = new CreateUserDto(registerDto.email(), encodedPassword);
        User user = userService.createUser(createUserDto, roles);
        return userMapper.toDto(user);
    }

    private boolean isPasswordValid(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    private void validateExistingUserByEmail(String email) {
        boolean exists = userService.existsByEmail(email);
        if (exists) {
            throw new ConflictException("Email already in use");
        }
    }

    private void setAuthentication(User user) {
        var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
