package integration_tests.utils;

import com.example.dtos.RegisterDto;
import com.example.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class AuthUtils {

    private final AuthService authService;

    public void registerUser(RegisterDto registerDto) {
        authService.register(registerDto);
    }
}
