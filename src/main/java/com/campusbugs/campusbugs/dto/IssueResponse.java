package com.campusbugs.campusbugs.dto;

import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.model.Status;

import java.time.LocalDateTime;

public record IssueResponse(

        Long id,
        String title,
        String description,
        Category category,
        Status status,
        int upvoteCount,
        LocalDateTime createdAt
) { }
