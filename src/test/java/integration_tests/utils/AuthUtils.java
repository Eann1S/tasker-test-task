package integration_tests.utils;

import com.example.dtos.JwtDto;
import com.example.dtos.LoginDto;
import com.example.dtos.RegisterDto;
import com.example.dtos.UserDto;
import com.example.entities.User;
import com.example.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.boot.test.context.TestComponent;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

@TestComponent
@RequiredArgsConstructor
public class AuthUtils {

    private final AuthService authService;

    public UserDto registerUser(RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    public Pair<UserDto, JwtDto> createUser() {
        var registerDto = Instancio.create(RegisterDto.class);
        UserDto userDto = registerUser(registerDto);
        var loginDto = new LoginDto(registerDto.email(), registerDto.password());
        JwtDto jwt = authService.login(loginDto);
        return Pair.of(userDto, jwt);
    }
}
