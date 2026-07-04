package ru.project.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentCreateDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Long facultyId
) {
}

