package integration_tests.controller;

import integration_tests.IntegrationTest;
import integration_tests.utils.AuthUtils;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = AuthUtils.class)
public class UserControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUtils authUtils;

    @ParameterizedTest
    @InstancioSource(samples = 1)
    void shouldReturnProfile_whenUserAuthenticated() throws Exception {
        var pair = authUtils.createUser();
        var user = pair.getLeft();
        var jwt = pair.getRight();

        var res = performGetProfileRequest(jwt.accessToken());

        res.andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(user.id()),
                jsonPath("$.email").value(user.email()),
                jsonPath("$.roles[0]").value(user.roles().get(0).name())
        );
    }

    @ParameterizedTest
    @InstancioSource(samples = 1)
    void shouldNotReturnProfile_whenUserNotAuthenticated() throws Exception {
        var res = performGetProfileRequest("invalid");

        res.andExpectAll(
                status().isUnauthorized(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.message").value("Token is invalid")
        );
    }

    private ResultActions performGetProfileRequest(String accessToken) throws Exception {
        return mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + accessToken));
    }
}