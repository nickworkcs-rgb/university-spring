package ru.project.university.dto;

import jakarta.validation.constraints.NotBlank;

public record FacultyCreateDto(@NotBlank String name) {
}