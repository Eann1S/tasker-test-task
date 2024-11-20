package integration_tests.utils;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.entities.User;
import com.example.services.AuthService;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

@TestComponent
@RequiredArgsConstructor
public class AuthUtils {

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    private final AuthService authService;
    private final UserService userService;

    public void registerUser(RegisterDto registerDto) {
        authService.registerUser(registerDto);
    }

    public Pair<User, JwtDto> createUser() {
        var registerDto = Instancio.create(RegisterDto.class);
        registerUser(registerDto);
        var loginDto = new LoginDto(registerDto.email(), registerDto.password());
        var jwt = authService.login(loginDto);
        var user = userService.findByEmail(loginDto.email());
        return Pair.of(user, jwt);
    }

    public Pair<User, JwtDto> loginAsAdmin() {
        var loginDto = new LoginDto(adminEmail, adminPassword);
        var user = userService.findByEmail(adminEmail);
        JwtDto jwt = authService.login(loginDto);
        return Pair.of(user, jwt);
    }
}
