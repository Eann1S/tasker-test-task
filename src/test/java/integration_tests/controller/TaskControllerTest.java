package integration_tests.controller;

import com.example.dtos.CreateCommentDto;
import com.example.dtos.CreateTaskDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.example.entities.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration_tests.IntegrationTest;
import integration_tests.utils.AuthUtils;
import integration_tests.utils.TaskUtils;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            var pair = authUtils.loginAsAdmin();
            var jwt = pair.getValue();

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
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            var task = taskUtils.addRandomTask(admin);

            var res = performGetTasksRequest(jwt.accessToken(), PageRequest.of(0, 10));

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.content[0].id").value(task.id()),
                    jsonPath("$.pageNumber").value(0),
                    jsonPath("$.pageSize").value(10),
                    jsonPath("$.totalElements").value(1),
                    jsonPath("$.totalPages").value(1)
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotReturnTasks_whenNotAdmin() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            taskUtils.addRandomTask(admin);
            var jwt = authUtils.createUser().getValue();

            var res = performGetTasksRequest(jwt.accessToken(), PageRequest.of(0, 10));

            res.andExpectAll(status().isForbidden());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldReturnTasksByAuthor() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var jwt = authUtils.createUser().getValue();

            var res = performGetTasksByAuthorRequest(jwt.accessToken(), admin.getId(), PageRequest.of(0, 10));

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.content[0].id").value(task.id()),
                    jsonPath("$.pageNumber").value(0),
                    jsonPath("$.pageSize").value(10),
                    jsonPath("$.totalElements").value(1),
                    jsonPath("$.totalPages").value(1)
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldReturnTasksByAssignee() throws Exception {
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            var task = taskUtils.addRandomTask(admin);
            var user = authUtils.createUser().getKey();
            taskUtils.assignTask(task.id(), user.getId());

            var res = performGetTasksByAssigneeRequest(jwt.accessToken(), user.getId(), PageRequest.of(0, 10));

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.content[0].id").value(task.id()),
                    jsonPath("$.pageNumber").value(0),
                    jsonPath("$.pageSize").value(10),
                    jsonPath("$.totalElements").value(1),
                    jsonPath("$.totalPages").value(1)
            );
        }
    }

    @Nested
    class UpdateTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldUpdateTask(UpdateTaskDto updateTaskDto) throws Exception {
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            var task = taskUtils.addRandomTask(admin);

            var res = performUpdateTaskRequest(jwt.accessToken(), task.id(), updateTaskDto);

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
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var jwt = authUtils.createUser().getValue();

            var res = performUpdateTaskRequest(jwt.accessToken(), task.id(), updateTaskDto);

            res.andExpectAll(status().isForbidden());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotUpdateTask_whenTaskNotFound(UpdateTaskDto updateTaskDto) throws Exception {
            var jwt = authUtils.loginAsAdmin().getValue();

            var res = performUpdateTaskRequest(jwt.accessToken(),"id", updateTaskDto);

            res.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.message").value("Task not found")
            );
        }
    }

    @Nested
    class UpdateTaskStatus {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldUpdateTaskStatus(Status status) throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var pair = authUtils.createUser();
            var user = pair.getKey();
            var jwt = pair.getValue();
            taskUtils.assignTask(task.id(), user.getId());

            var res = performUpdateTaskStatusRequest(jwt.accessToken(), task.id(), status);

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.id").value(task.id()),
                    jsonPath("$.status").value(status.name())
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotUpdateTaskStatus_whenNotAssigneeAndNotAdmin(Status status) throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var jwt = authUtils.createUser().getValue();

            var res = performUpdateTaskStatusRequest(jwt.accessToken(), task.id(), status);

            res.andExpectAll(status().isForbidden());
        }
    }

    @Nested
    class DeleteTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldDeleteTask() throws Exception {
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            var task = taskUtils.addRandomTask(admin);

            var res = performDeleteTaskRequest(jwt.accessToken(), task.id());

            res.andExpectAll(status().isOk());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotDeleteTask_whenNotAdmin() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var pair = authUtils.createUser();
            var jwt = pair.getValue();

            var res = performDeleteTaskRequest(jwt.accessToken(), task.id());

            res.andExpectAll(status().isForbidden());
        }
    }

    @Nested
    class AssignTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldAssignTask() throws Exception {
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            var task = taskUtils.addRandomTask(admin);
            var user = authUtils.createUser().getKey();

            var res = performAssignTaskRequest(jwt.accessToken(), task.id(), user.getId());

            res.andExpectAll(status().isOk());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotAssignTask_whenNotAdmin() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            var task = taskUtils.addRandomTask(admin);
            var pair = authUtils.createUser();
            var user = pair.getKey();
            var jwt = pair.getValue();

            var res = performAssignTaskRequest(jwt.accessToken(), task.id(), user.getId());

            res.andExpectAll(status().isForbidden());
        }
    }

    @Nested
    class CommentTask {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldCommentTask(CreateCommentDto dto) throws Exception {
            var pair = authUtils.loginAsAdmin();
            var admin = pair.getKey();
            var jwt = pair.getValue();
            TaskDto task = taskUtils.addRandomTask(admin);

            var res = performCommentTaskRequest(jwt.accessToken(), task.id(), dto);

            res.andExpectAll(
                    status().isOk(),
                    jsonPath("$.id").value(task.id()),
                    jsonPath("$.comments").isArray(),
                    jsonPath("$.comments[0].content").value(dto.content())
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotCommentTask_whenNotAssigneeAndNotAdmin(CreateCommentDto dto) throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            TaskDto task = taskUtils.addRandomTask(admin);
            var jwt = authUtils.createUser().getValue();

            var res = performCommentTaskRequest(jwt.accessToken(), task.id(), dto);

            res.andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.message").value("You are not allowed to comment this task")
            );
        }
    }

    private ResultActions performAddTaskRequest(String accessToken, CreateTaskDto createTaskDto) throws Exception {
        return mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskDto)));
    }

    private ResultActions performGetTasksRequest(String accessToken, Pageable pageable) throws Exception {
        return mockMvc.perform(get("/api/tasks")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performGetTasksByAuthorRequest(String accessToken, String authorId, Pageable pageable) throws Exception {
        return mockMvc.perform(get("/api/tasks/author/{authorId}", authorId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performGetTasksByAssigneeRequest(String accessToken, String assigneeId, Pageable pageable) throws Exception {
        return mockMvc.perform(get("/api/tasks/assignee/{assigneeId}", assigneeId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performUpdateTaskRequest(String accessToken, String taskId, UpdateTaskDto updateTaskDto) throws Exception {
        return mockMvc.perform(put("/api/tasks/{taskId}", taskId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskDto)));
    }

    private ResultActions performUpdateTaskStatusRequest(String accessToken, String taskId, Status status) throws Exception {
        return mockMvc.perform(put("/api/tasks/{taskId}/status", taskId)
                .header("Authorization", "Bearer " + accessToken)
                .param("status", status.name()));
    }

    private ResultActions performDeleteTaskRequest(String accessToken, String taskId) throws Exception {
        return mockMvc.perform(delete("/api/tasks/{taskId}", taskId)
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performAssignTaskRequest(String accessToken, String taskId, String userId) throws Exception {
        return mockMvc.perform(post("/api/tasks/{taskId}/assign/{userId}", taskId, userId)
                .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performCommentTaskRequest(String accessToken, String taskId, CreateCommentDto dto) throws Exception {
        return mockMvc.perform(post("/api/tasks/{taskId}/comment", taskId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }
}
