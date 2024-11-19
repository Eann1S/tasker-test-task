package integration_tests.controller;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration_tests.IntegrationTest;
import integration_tests.utils.AuthUtils;
import integration_tests.utils.TaskUtils;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AuthUtils.class, TaskUtils.class})
public class TaskControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private TaskUtils taskUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class AddTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldAddTask(CreateTaskDto createTaskDto) throws Exception {
            var jwt = authUtils.loginAsAdmin();

            var res = performAddTaskRequest(jwt.accessToken(), createTaskDto);

            res.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id").exists(),
                    jsonPath("$.title").value(createTaskDto.title()),
                    jsonPath("$.description").value(createTaskDto.description()),
                    jsonPath("$.status").value(createTaskDto.status().name()),
                    jsonPath("$.priority").value(createTaskDto.priority().name())
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotAddTask_whenNotAdmin(CreateTaskDto createTaskDto) throws Exception {
            var jwt = authUtils.createUser().getRight();

            var res = performAddTaskRequest(jwt.accessToken(), createTaskDto);

            res.andExpectAll(status().is(403));
        }
    }

    @Nested
    class GetTasks {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldReturnTasks() throws Exception {
            var jwt = authUtils.loginAsAdmin();
            TaskDto task = taskUtils.addRandomTask();

            var res = performGetTasksRequest(jwt.accessToken(), 0, 10);

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.content[0].id").exists(),
                    jsonPath("$.content[0].title").value(task.title()),
                    jsonPath("$.content[0].description").value(task.description()),
                    jsonPath("$.content[0].status").value(task.status().name()),
                    jsonPath("$.content[0].priority").value(task.priority().name()),
                    jsonPath("$.pageNumber").value(0),
                    jsonPath("$.pageSize").value(10),
                    jsonPath("$.totalElements").value(1),
                    jsonPath("$.totalPages").value(1)
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotReturnTasks_whenNotAdmin() throws Exception {
            var jwt = authUtils.createUser().getRight();
            taskUtils.addRandomTask();

            var res = performGetTasksRequest(jwt.accessToken(), 0, 10);

            res.andExpectAll(status().isForbidden());
        }
    }

    @Nested
    class UpdateTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldUpdateTask(UpdateTaskDto updateTaskDto) throws Exception {
            var jwt = authUtils.loginAsAdmin();
            var task = taskUtils.addRandomTask();

            var res = performPutTaskRequest(jwt.accessToken(), task.id(), updateTaskDto);

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.id").value(task.id()),
                    jsonPath("$.title").value(updateTaskDto.title()),
                    jsonPath("$.description").value(updateTaskDto.description()),
                    jsonPath("$.status").value(updateTaskDto.status().name()),
                    jsonPath("$.priority").value(updateTaskDto.priority().name())
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotUpdateTask_whenNotAdmin(UpdateTaskDto updateTaskDto) throws Exception {
            var jwt = authUtils.createUser().getRight();
            var task = taskUtils.addRandomTask();

            var res = performPutTaskRequest(jwt.accessToken(), task.id(), updateTaskDto);

            res.andExpectAll(status().is(403));
        }
    }

    @Nested
    class DeleteTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldDeleteTask() throws Exception {
            var jwt = authUtils.loginAsAdmin();
            var task = taskUtils.addRandomTask();

            var res = performDeleteTasksRequest(jwt.accessToken(), task.id());

            res.andExpectAll(status().isOk());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotReturnTasks_whenNotAdmin() throws Exception {
            var jwt = authUtils.createUser().getRight();
            var task = taskUtils.addRandomTask();

            var res = performDeleteTasksRequest(jwt.accessToken(), task.id());

            res.andExpectAll(status().isForbidden());
        }
    }

    private ResultActions performAddTaskRequest(String accessToken, CreateTaskDto createTaskDto) throws Exception {
        return mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskDto)));
    }

    private ResultActions performGetTasksRequest(String accessToken, int page, int size) throws Exception {
        return mockMvc.perform(get("/api/tasks")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performPutTaskRequest(String accessToken, String taskId, UpdateTaskDto updateTaskDto) throws Exception {
        return mockMvc.perform(put("/api/tasks/{taskId}", taskId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskDto)));
    }

    private ResultActions performDeleteTasksRequest(String accessToken, String taskId) throws Exception {
        return mockMvc.perform(delete("/api/tasks/{taskId}", taskId)
                .header("Authorization", "Bearer " + accessToken));
    }
}
