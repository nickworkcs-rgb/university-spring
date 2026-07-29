package ru.project.university.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.university.dto.StudentCreateDto;
import ru.project.university.dto.StudentDto;
import ru.project.university.services.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "CRUD operations for students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students")
    public List<StudentDto> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by id")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public StudentDto findById(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new student")
    @ApiResponse(responseCode = "404", description = "Faculty not found")
    public StudentDto create(@Valid @RequestBody StudentCreateDto dto) {
        return studentService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student, optionally moving them to another faculty")
    @ApiResponse(responseCode = "404", description = "Student or faculty not found")
    public StudentDto update(@PathVariable Long id, @Valid @RequestBody StudentCreateDto dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student by id")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}