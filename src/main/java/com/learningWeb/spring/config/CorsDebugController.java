package com.learningWeb.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsDebugController {

    @Value("${cors.allowed.origin}")
    private String corsAllowedOrigin;

    @GetMapping("/cors-debug")
    public String corsDebug() {
        return "CORS Allowed Origin: " + corsAllowedOrigin;
    }
}
