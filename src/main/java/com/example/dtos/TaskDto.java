package com.example.dtos;

import com.example.entities.Priority;
import com.example.entities.Status;
import com.example.entities.Task;

import java.io.Serializable;

/**
 * DTO for {@link Task}
 */
public record TaskDto(
        String id,
        String title,
        String description,
        Status status,
        Priority priority
) implements Serializable {
}