package integration_tests.controller;

import com.example.dtos.CommentDto;
import com.example.dtos.TaskDto;
import integration_tests.IntegrationTest;
import integration_tests.utils.AuthUtils;
import integration_tests.utils.TaskUtils;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AuthUtils.class, TaskUtils.class})
public class CommentControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private TaskUtils taskUtils;

    @Nested
    class DeleteComment {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldDeleteComment() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            TaskDto task = taskUtils.addRandomTask(admin);
            var pair = authUtils.createUser();
            var user = pair.getKey();
            var jwt = pair.getValue();
            taskUtils.assignTask(task.id(), user.getId());
            task = taskUtils.commentTask(task.id(), user);
            CommentDto comment = task.comments().get(0);

            var res = performDeleteComment(comment.id(), jwt.accessToken());

            res.andExpectAll(status().isOk());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotDeleteComment_whenNotAuthorAndNotAdmin() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            TaskDto task = taskUtils.addRandomTask(admin);
            task = taskUtils.commentTask(task.id(), admin);
            CommentDto comment = task.comments().get(0);
            var jwt = authUtils.createUser().getValue();

            var res = performDeleteComment(comment.id(), jwt.accessToken());

            res.andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.message").value("You are not allowed to delete this comment")
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotDeleteComment_whenCommentNotFound() throws Exception {
            var admin = authUtils.loginAsAdmin().getKey();
            TaskDto task = taskUtils.addRandomTask(admin);
            taskUtils.commentTask(task.id(), admin);
            var jwt = authUtils.createUser().getValue();

            var res = performDeleteComment("id", jwt.accessToken());

            res.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.message").value("Comment not found")
            );
        }
    }

    private ResultActions performDeleteComment(String commentId, String accessToken) throws Exception {
        return mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .header("Authorization", "Bearer " + accessToken));
    }
}
