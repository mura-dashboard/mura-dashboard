package com.github.muradashboard.app.muradashboard;

import com.github.muradashboard.app.MuraDashboardApplication;
import org.springframework.boot.SpringApplication;

public class TestMuraDashboardApplication {

    static void main(String[] args) {
        SpringApplication.from(MuraDashboardApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
