package ru.project.university.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.university.dto.FacultyCreateDto;
import ru.project.university.dto.FacultyDto;
import ru.project.university.services.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
@Tag(name = "Faculties", description = "CRUD operations for university faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    @Operation(summary = "Get all faculties")
    public List<FacultyDto> findAll() {
        return facultyService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a faculty by id")
    @ApiResponse(responseCode = "404", description = "Faculty not found")
    public FacultyDto findById(@PathVariable Long id) {
        return facultyService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new faculty")
    @ApiResponse(responseCode = "409", description = "Faculty with this name already exists")
    public FacultyDto create(@Valid @RequestBody FacultyCreateDto dto) {
        return facultyService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing faculty")
    @ApiResponse(responseCode = "404", description = "Faculty not found")
    @ApiResponse(responseCode = "409", description = "Faculty with this name already exists")
    public FacultyDto update(@PathVariable Long id, @Valid @RequestBody FacultyCreateDto dto) {
        return facultyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a faculty by id")
    @ApiResponse(responseCode = "404", description = "Faculty not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}