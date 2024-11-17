package com.example.dtos;

import com.example.entities.Priority;
import com.example.entities.Status;
import com.example.entities.Task;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link Task}
 */
public record CreateTaskDto(
        @NotBlank(message = "Title must not be empty.")
        String title,
        String description,
        Status status,
        Priority priority
) implements Serializable {
}