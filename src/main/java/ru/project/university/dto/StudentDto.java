package ru.project.university.dto;

public record StudentDto(
        Long id,
        String firstName,
        String lastName,
        Long facultyId,
        String facultyName
) {
}