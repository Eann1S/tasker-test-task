package integration_tests.controller;

import com.example.entities.Role;
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
                jsonPath("$.id").value(user.getId()),
                jsonPath("$.email").value(user.getEmail()),
                jsonPath("$.roles[0]").value(Role.ROLE_USER.name())
        );
    }

    @ParameterizedTest
    @InstancioSource(samples = 1)
    void shouldNotReturnProfile_whenTokenIsInvalid() throws Exception {
        var res = performGetProfileRequest("invalid");

        res.andExpectAll(
                status().isUnauthorized(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.message").value("Token is invalid")
        );
    }

    @ParameterizedTest
    @InstancioSource(samples = 1)
    void shouldNotReturnProfile_whenTokenIsMissing() throws Exception {
        var res = mockMvc.perform(get("/api/users/me"));

        res.andExpectAll(status().isForbidden());
    }

    private ResultActions performGetProfileRequest(String accessToken) throws Exception {
        return mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + accessToken));
    }
}
