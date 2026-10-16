package com.campusbugs.campusbugs.dto;


import com.campusbugs.campusbugs.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100,message = "Tile must be at most 100 characters")
        String title,


        @NotBlank(message = "Description cannot be blank")
        @Size(min = 3,max=100 ,message = "Desc can be at most of 1000 character")
        String description,


        @NotNull(message = "Category can not be blank")

        Category category
) { }
