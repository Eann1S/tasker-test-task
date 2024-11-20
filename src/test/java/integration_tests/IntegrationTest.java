package integration_tests;

import com.example.App;
import com.redis.testcontainers.RedisContainer;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {App.class}, properties = "spring.main.allow-bean-definition-overriding=false")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
@Transactional
@Testcontainers(parallel = true)
@ActiveProfiles("test")
public class IntegrationTest {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    static final RedisContainer REDIS_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:alpine").withExposedPorts(5432);
        REDIS_CONTAINER = new RedisContainer("redis:7.2-rc-alpine3.18");
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }
}
