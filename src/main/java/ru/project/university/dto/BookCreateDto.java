package ru.project.university.dto;

import jakarta.validation.constraints.NotBlank;

public record BookCreateDto(
        @NotBlank String author,
        @NotBlank String title
) {
}