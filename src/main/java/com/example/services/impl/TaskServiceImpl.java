package com.example.services.impl;

import com.example.dtos.*;
import com.example.entities.*;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.NotFoundException;
import com.example.mapper.TaskMapper;
import com.example.repositories.TaskRepository;
import com.example.services.CommentService;
import com.example.services.TaskService;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final CommentService commentService;

    @Override
    public TaskDto addTask(CreateTaskDto dto, User author) {
        Task task = taskMapper.fromDto(dto, author);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public PageableDto<TaskDto> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        return taskMapper.toPageableDto(tasks);
    }

    @Override
    public PageableDto<TaskDto> getTasksForAuthor(String authorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthor_Id(authorId, pageable);
        return taskMapper.toPageableDto(tasks);
    }

    @Override
    public PageableDto<TaskDto> getTasksForAssignee(String assigneeId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAssignee_Id(assigneeId, pageable);
        return taskMapper.toPageableDto(tasks);
    }

    @Override
    public TaskDto updateTask(String taskId, UpdateTaskDto dto) {
        Task task = findById(taskId);
        return updateTask(task, dto);
    }

    @Override
    public TaskDto updateTaskStatus(String taskId, String status, User user) {
        Task task = findById(taskId);
        if (notAssignee(task, user) && notAdmin(user)) {
            throw new ForbiddenException("You are not allowed to update this task's status");
        }
        UpdateTaskDto updateTaskDto = new UpdateTaskDto(null, null, Status.from(status), null);
        return updateTask(task, updateTaskDto);
    }

    @Override
    public void deleteTask(String taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public void assignTask(String taskId, String assigneeId) {
        User assignee = userService.findById(assigneeId);
        Task task = findById(taskId);
        task.setAssignee(assignee);
        taskRepository.saveAndFlush(task);
    }

    @Override
    public TaskDto commentTask(String taskId, CreateCommentDto createCommentDto, User author) {
        Task task = findById(taskId);
        if (notAssignee(task, author) && notAdmin(author)) {
            throw new ForbiddenException("You are not allowed to comment this task");
        }
        Comment comment = commentService.createComment(createCommentDto, author, task);
        task.addComment(comment);
        task = taskRepository.saveAndFlush(task);
        return taskMapper.toDto(task);
    }

    @Override
    public Task findById(String id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    private TaskDto updateTask(Task task, UpdateTaskDto dto) {
        Task updated = taskMapper.partialUpdate(dto, task);
        taskRepository.save(updated);
        return taskMapper.toDto(updated);
    }

    private boolean notAdmin(User user) {
        return !user.getRoles().contains(Role.ROLE_ADMIN);
    }

    private boolean notAssignee(Task task, User user) {
        return !user.equals(task.getAssignee());
    }
}
