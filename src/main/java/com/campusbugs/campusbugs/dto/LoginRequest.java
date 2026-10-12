package com.campusbugs.campusbugs.dto;

import jakarta.validation.constraints.NotBlank;



    public record LoginRequest(
            @NotBlank(message = "Roll no is required")
            String rollNumber,

            @NotBlank(message = "Password is required")
            String password
    )
{}

