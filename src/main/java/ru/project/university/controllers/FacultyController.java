package ru.project.university.controllers;

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
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    public List<FacultyDto> findAll() {
        return facultyService.findAll();
    }

    @GetMapping("/{id}")
    public FacultyDto findById(@PathVariable Long id) {
        return facultyService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyDto create(@Valid @RequestBody FacultyCreateDto dto) {
        return facultyService.create(dto);
    }

    @PutMapping("/{id}")
    public FacultyDto update(@PathVariable Long id, @Valid @RequestBody FacultyCreateDto dto) {
        return facultyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}