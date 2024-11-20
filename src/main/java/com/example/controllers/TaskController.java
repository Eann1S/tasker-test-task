package com.example.controllers;

import com.example.dtos.*;
import com.example.entities.User;
import com.example.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> addTask(@RequestBody CreateTaskDto dto, @AuthenticationPrincipal User user) {
        var task = taskService.addTask(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto<TaskDto>> getAllTasks(Pageable pageable) {
        var tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<PageableDto<TaskDto>> getTasksForAuthor(@PathVariable String authorId, Pageable pageable) {
        var tasks = taskService.getTasksForAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<PageableDto<TaskDto>> getTasksForAssignee(@PathVariable String assigneeId, Pageable pageable) {
        var tasks = taskService.getTasksForAssignee(assigneeId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateTaskDto dto
    ) {
        var task = taskService.updateTask(taskId, dto);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable("taskId") String taskId,
            @RequestParam String status,
            @AuthenticationPrincipal User user
    ) {
        var task = taskService.updateTaskStatus(taskId, status, user);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignTask(@PathVariable("taskId") String taskId, @PathVariable("userId") String userId) {
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/comment")
    public ResponseEntity<TaskDto> commentTask(
            @PathVariable("taskId") String taskId,
            @RequestBody CreateCommentDto dto,
            @AuthenticationPrincipal User user
    ) {
        TaskDto task = taskService.commentTask(taskId, dto, user);
        return ResponseEntity.ok(task);
    }
}
