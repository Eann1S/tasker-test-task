package com.example.dtos;

import com.example.entities.Comment;

import java.io.Serializable;

/**
 * DTO for {@link Comment}
 */
public record UpdateCommentDto(
        String content
) implements Serializable {
}