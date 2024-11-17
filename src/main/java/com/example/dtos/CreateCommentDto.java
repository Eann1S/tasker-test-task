package com.example.dtos;

import com.example.entities.Comment;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link Comment}
 */
public record CreateCommentDto(
        @NotBlank(message = "Content must not be empty.")
        String content
) implements Serializable {
}