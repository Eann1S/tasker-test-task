package integration_tests.controller;

import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.entities.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration_tests.IntegrationTest;
import integration_tests.utils.AuthUtils;
import org.instancio.junit.InstancioSource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = AuthUtils.class)
public class AuthControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Register {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldRegister(RegisterDto registerDto) throws Exception {
            var res = performRegisterRequest(registerDto);

            res.andExpectAll(
                    status().isCreated(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id").exists(),
                    jsonPath("$.email").value(registerDto.email()),
                    jsonPath("$.roles[0]").value(Role.ROLE_USER.name())
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotRegister_whenUserAlreadyExist(RegisterDto registerDto) throws Exception {
            authUtils.registerUser(registerDto);

            var res = performRegisterRequest(registerDto);

            res.andExpectAll(
                    status().isConflict(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message").value("Email already in use")
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotRegister_whenProvidedDataIsInvalid() throws Exception {
            var registerDto = new RegisterDto("invalid", "");

            var res = performRegisterRequest(registerDto);

            res.andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.email").value("Email must be a valid email."),
                    jsonPath("$.password").value("Password must be between 4-16 characters long.")
            );
        }
    }

    @Nested
    class Login {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldLogin(RegisterDto registerDto) throws Exception {
            authUtils.registerUser(registerDto);
            var loginDto = new LoginDto(registerDto.email(), registerDto.password());

            var res = performLoginRequest(loginDto);

            res.andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.accessToken").isString()
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotLogin_whenEmailIsInvalid(RegisterDto registerDto) throws Exception {
            authUtils.registerUser(registerDto);
            var loginDto = new LoginDto("invalid", registerDto.password());

            var res = performLoginRequest(loginDto);

            res.andExpectAll(
                    status().isNotFound(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message").value("User not found")
            );
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotLogin_whenPasswordIsInvalid(RegisterDto registerDto) throws Exception {
            authUtils.registerUser(registerDto);
            var loginDto = new LoginDto(registerDto.email(), "invalid");

            var res = performLoginRequest(loginDto);

            res.andExpectAll(
                    status().isUnauthorized(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message").value("Invalid credentials")
            );
        }
    }

    @Nested
    class Logout {
        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldLogout() throws Exception {
            var jwt = authUtils.createUser().getValue();

            var res = performLogoutRequest(jwt.accessToken());

            res.andExpectAll(status().isOk());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotLogout_whenNotAuthenticated() throws Exception {
            var res = performLogoutRequest("invalid");

            res.andExpectAll(status().isUnauthorized());
        }

        @ParameterizedTest
        @InstancioSource(samples = 1)
        void shouldNotLogout_whenAlreadyLoggedOut() throws Exception {
            var jwt = authUtils.createUser().getValue();

            performLogoutRequest(jwt.accessToken());
            var res = performLogoutRequest(jwt.accessToken());

            res.andExpectAll(status().isUnauthorized());
        }
    }

    private @NotNull ResultActions performRegisterRequest(RegisterDto registerDto) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(registerDto))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private @NotNull ResultActions performLoginRequest(LoginDto loginDto) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private @NotNull ResultActions performLogoutRequest(String accessToken) throws Exception {
        return mockMvc.perform(post("/api/logout")
                .header("Authorization", "Bearer " + accessToken));
    }
}
