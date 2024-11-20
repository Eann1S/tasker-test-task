package com.example.mapper;

import com.example.dtos.CreateUserDto;
import com.example.dtos.UserDto;
import com.example.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(CreateUserDto createUserDto);
}
