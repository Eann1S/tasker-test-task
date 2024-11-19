package integration_tests.utils;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.TaskDto;
import com.example.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TaskUtils {

    private final TaskService taskService;

    public TaskDto addRandomTask() {
        CreateTaskDto dto = Instancio.create(CreateTaskDto.class);
        return taskService.addTask(dto);
    }
}
