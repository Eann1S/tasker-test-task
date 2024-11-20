package com.example.mapper;

import com.example.dtos.CommentDto;
import com.example.dtos.CreateCommentDto;
import com.example.entities.Comment;
import com.example.entities.Task;
import com.example.entities.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "author", expression = "java(author)")
    @Mapping(target = "task", expression = "java(task)")
    Comment toEntity(CreateCommentDto createCommentDto, @Context User author, @Context Task task);

    CommentDto toDto(Comment comment);
}
