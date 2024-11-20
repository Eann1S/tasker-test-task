package com.example.services;

import com.example.dtos.*;
import com.example.entities.Task;
import com.example.entities.User;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto addTask(CreateTaskDto dto, User author);

    PageableDto<TaskDto> getAllTasks(Pageable pageable);

    PageableDto<TaskDto> getTasksForAuthor(String authorId, Pageable pageable);

    PageableDto<TaskDto> getTasksForAssignee(String assigneeId, Pageable pageable);

    TaskDto updateTask(String taskId, UpdateTaskDto dto);

    TaskDto updateTaskStatus(String taskId, String status, User user);

    void deleteTask(String taskId);

    void assignTask(String taskId, String assigneeId);

    TaskDto commentTask(String taskId, CreateCommentDto createCommentDto, User author);

    Task findById(String id);
}
