package com.campusbugs.campusbugs.controller;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController
{
    @GetMapping("/api/me")
    public Map<String, String> me(Authentication authentication){
        return Map.of(
                "rollNumber", authentication.getName(),
                "authorities", authentication.getAuthorities().toString());


    }

    @GetMapping("/api/admin/ping")
    public Map<String, String> adminPing() {
        return Map.of("message","Hello admin");
    }
}
