package integration_tests.utils;

import com.example.dtos.CreateCommentDto;
import com.example.dtos.CreateTaskDto;
import com.example.dtos.TaskDto;
import com.example.entities.Task;
import com.example.entities.User;
import com.example.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TaskUtils {

    private final TaskService taskService;

    public TaskDto addRandomTask(User author) {
        CreateTaskDto dto = Instancio.create(CreateTaskDto.class);
        return taskService.addTask(dto, author);
    }

    public TaskDto commentTask(String taskId, User author) {
        var createCommentDto = Instancio.create(CreateCommentDto.class);
        return taskService.commentTask(taskId, createCommentDto, author);
    }

    public void assignTask(String taskId, String assigneeId) {
        taskService.assignTask(taskId, assigneeId);
    }
}
