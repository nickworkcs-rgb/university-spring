package ru.project.university.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.university.dto.BookCreateDto;
import ru.project.university.dto.BookDto;
import ru.project.university.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@Valid @RequestBody BookCreateDto dto) {
        return bookService.create(dto);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @Valid @RequestBody BookCreateDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assign/{studentId}")
    public BookDto assignToStudent(@PathVariable Long id, @PathVariable Long studentId) {
        return bookService.assignToStudent(id, studentId);
    }

    @PatchMapping("/{id}/unassign")
    public BookDto unassign(@PathVariable Long id) {
        return bookService.unassign(id);
    }
}