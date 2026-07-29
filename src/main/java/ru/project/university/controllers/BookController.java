package ru.project.university.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Books", description = "CRUD operations for books and their ownership")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public List<BookDto> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book without an owner")
    public BookDto create(@Valid @RequestBody BookCreateDto dto) {
        return bookService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book's author and title")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookDto update(@PathVariable Long id, @Valid @RequestBody BookCreateDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by id")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assign/{studentId}")
    @Operation(summary = "Assign a book to a student")
    @ApiResponse(responseCode = "404", description = "Book or student not found")
    public BookDto assignToStudent(
            @PathVariable Long id,
            @Parameter(description = "Id of the student to assign the book to") @PathVariable Long studentId) {
        return bookService.assignToStudent(id, studentId);
    }

    @PatchMapping("/{id}/unassign")
    @Operation(summary = "Remove the current owner of a book")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookDto unassign(@PathVariable Long id) {
        return bookService.unassign(id);
    }
}