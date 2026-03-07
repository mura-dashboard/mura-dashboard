package com.github.muradashboard.app.muradashboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MuraDashboardApplicationTests {

    @Test
    void contextLoads() {
    }

}
