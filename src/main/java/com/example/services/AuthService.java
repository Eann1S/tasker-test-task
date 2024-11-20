package com.example.services;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.dtos.UserDto;
import com.example.entities.User;

public interface AuthService {

    JwtDto login(LoginDto loginDto);

    UserDto registerUser(RegisterDto registerDto);

    UserDto registerAdmin(RegisterDto registerDto);

    void logout(User user);
}
