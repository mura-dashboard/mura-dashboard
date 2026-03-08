package com.github.muradashboard.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards SPA routes to index.html so that client-side routing works.
 * Only matches paths without a file extension (no dot) to avoid intercepting
 * static resources like index.html, which would cause an infinite forward loop.
 */
@Controller
public class SpaForwardingController {

    @GetMapping("/")
    public String root() {
        return "forward:/index.html";
    }

    @GetMapping("/{path:^(?!rapi|api|swagger|v3|actuator|assets)[^.]*$}/**")
    public String forward() {
        return "forward:/index.html";
    }
}
