package com.campusbugs.campusbugs.dto;

import com.campusbugs.campusbugs.model.Status;

import jakarta.validation.constraints.NotNull;

public record StatusRequest (
    @NotNull(message = "Status is required")
    Status status)
{ }

