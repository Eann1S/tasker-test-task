package com.example.services.impl;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.PageableDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.example.entities.Task;
import com.example.exceptions.NotFoundException;
import com.example.mapper.TaskMapper;
import com.example.repositories.TaskRepository;
import com.example.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto addTask(CreateTaskDto dto) {
        Task task = taskMapper.fromDto(dto);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public PageableDto<TaskDto> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        return taskMapper.toPageableDto(tasks);
    }

    @Override
    public TaskDto updateTask(String taskId, UpdateTaskDto dto) {
        Task task = findById(taskId);
        Task updated = taskMapper.partialUpdate(dto, task);
        taskRepository.save(updated);
        return taskMapper.toDto(updated);
    }

    @Override
    public void deleteTask(String taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public Task findById(String id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }
}
