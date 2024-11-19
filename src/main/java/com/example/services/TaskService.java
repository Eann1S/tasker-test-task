package com.example.services;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.PageableDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.example.entities.Task;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto addTask(CreateTaskDto dto);

    PageableDto<TaskDto> getAllTasks(Pageable pageable);

    TaskDto updateTask(String taskId, UpdateTaskDto dto);

    void deleteTask(String taskId);

    Task findById(String id);
}
