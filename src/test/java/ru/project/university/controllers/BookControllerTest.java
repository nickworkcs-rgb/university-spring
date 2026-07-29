package ru.project.university.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import ru.project.university.dto.BookCreateDto;
import ru.project.university.dto.BookDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    void findAll_returnsOkAndList() throws Exception {
        BookDto dto = new BookDto(1L, "Author Name", "Book Title", null);
        when(bookService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book Title"))
                .andExpect(jsonPath("$[0].studentId").doesNotExist());
    }

    @Test
    void findById_whenExists_returnsOk() throws Exception {
        BookDto dto = new BookDto(1L, "Author Name", "Book Title", null);
        when(bookService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Author Name"));
    }

    @Test
    void findById_whenMissing_returnsNotFound() throws Exception {
        when(bookService.findById(99L)).thenThrow(new ResourceNotFoundException("Book with id 99 not found"));

        mockMvc.perform(get("/api/books/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withValidBody_returnsCreated() throws Exception {
        BookCreateDto createDto = new BookCreateDto("Author Name", "Book Title");
        BookDto responseDto = new BookDto(1L, "Author Name", "Book Title", null);
        when(bookService.create(any(BookCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_withBlankAuthor_returnsBadRequest() throws Exception {
        BookCreateDto invalidDto = new BookCreateDto("", "Book Title");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any());
    }

    @Test
    void update_withValidBody_returnsOk() throws Exception {
        BookCreateDto updateDto = new BookCreateDto("New Author", "New Title");
        BookDto responseDto = new BookDto(1L, "New Author", "New Title", null);
        when(bookService.update(eq(1L), any(BookCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("New Author"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService).delete(1L);
    }

    @Test
    void assignToStudent_whenBothExist_returnsOk() throws Exception {
        BookDto dto = new BookDto(1L, "Author Name", "Book Title", 10L);
        when(bookService.assignToStudent(1L, 10L)).thenReturn(dto);

        mockMvc.perform(patch("/api/books/{id}/assign/{studentId}", 1L, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(10));
    }

    @Test
    void assignToStudent_whenStudentMissing_returnsNotFound() throws Exception {
        when(bookService.assignToStudent(1L, 99L))
                .thenThrow(new ResourceNotFoundException("Student with id 99 not found"));

        mockMvc.perform(patch("/api/books/{id}/assign/{studentId}", 1L, 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void unassign_whenExists_returnsOk() throws Exception {
        BookDto dto = new BookDto(1L, "Author Name", "Book Title", null);
        when(bookService.unassign(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/books/{id}/unassign", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").doesNotExist());
    }

    @Test
    void unassign_whenBookMissing_returnsNotFound() throws Exception {
        when(bookService.unassign(99L)).thenThrow(new ResourceNotFoundException("Book with id 99 not found"));

        mockMvc.perform(patch("/api/books/{id}/unassign", 99L))
                .andExpect(status().isNotFound());
    }
}