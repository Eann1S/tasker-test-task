package integration_tests.utils;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.dtos.UserDto;
import com.example.services.AuthService;
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

    public UserDto registerUser(RegisterDto registerDto) {
        return authService.registerUser(registerDto);
    }

    public Pair<UserDto, JwtDto> createUser() {
        var registerDto = Instancio.create(RegisterDto.class);
        var userDto = registerUser(registerDto);
        var loginDto = new LoginDto(registerDto.email(), registerDto.password());
        JwtDto jwt = authService.login(loginDto);
        return Pair.of(userDto, jwt);
    }

    public JwtDto loginAsAdmin() {
        var loginDto = new LoginDto(adminEmail, adminPassword);
        return authService.login(loginDto);
    }
}
