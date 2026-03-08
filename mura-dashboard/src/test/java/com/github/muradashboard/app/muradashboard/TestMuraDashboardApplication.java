package com.github.muradashboard.app.muradashboard;

import com.github.muradashboard.app.MuraDashboardApplication;
import org.springframework.boot.SpringApplication;

public class TestMuraDashboardApplication {

    static void main() {
        SpringApplication.from(MuraDashboardApplication::main).with(TestcontainersConfiguration.class).run(
                "--spring.security.user.name=admin",
                "--spring.security.user.password=admin"
        );
    }
}