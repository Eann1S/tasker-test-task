package com.example.controllers;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.PageableDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.example.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> addTask(@RequestBody CreateTaskDto dto) {
        var task = taskService.addTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto<TaskDto>> getAllTasks(Pageable pageable) {
        var tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("taskId") String taskId, @RequestBody UpdateTaskDto dto) {
        var task = taskService.updateTask(taskId, dto);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }
}
