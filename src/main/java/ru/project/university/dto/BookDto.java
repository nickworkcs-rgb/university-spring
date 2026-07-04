package ru.project.university.dto;

public record BookDto(
        Long id,
        String author,
        String title,
        Long studentId
) {
}
