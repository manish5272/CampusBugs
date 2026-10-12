package com.campusbugs.campusbugs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record RegisterRequest(
        @NotBlank(message = "Roll number is required")
        String rollNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}
