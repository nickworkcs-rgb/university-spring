package ru.project.university.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import ru.project.university.dto.StudentCreateDto;
import ru.project.university.dto.StudentDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.services.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @Test
    void findAll_returnsOkAndList() throws Exception {
        StudentDto dto = new StudentDto(1L, "John", "Doe", 1L, "Computer Science");
        when(studentService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].facultyName").value("Computer Science"));
    }

    @Test
    void findById_whenExists_returnsOk() throws Exception {
        StudentDto dto = new StudentDto(1L, "John", "Doe", 1L, "Computer Science");
        when(studentService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void findById_whenMissing_returnsNotFound() throws Exception {
        when(studentService.findById(99L)).thenThrow(new ResourceNotFoundException("Student with id 99 not found"));

        mockMvc.perform(get("/api/students/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withValidBody_returnsCreated() throws Exception {
        StudentCreateDto createDto = new StudentCreateDto("John", "Doe", 1L);
        StudentDto responseDto = new StudentDto(1L, "John", "Doe", 1L, "Computer Science");
        when(studentService.create(any(StudentCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_withMissingFacultyId_returnsBadRequest() throws Exception {
        String invalidJson = """
                {"firstName": "John", "lastName": "Doe"}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(studentService, never()).create(any());
    }

    @Test
    void create_whenFacultyMissing_returnsNotFound() throws Exception {
        StudentCreateDto createDto = new StudentCreateDto("John", "Doe", 99L);
        when(studentService.create(any(StudentCreateDto.class)))
                .thenThrow(new ResourceNotFoundException("Faculty with id 99 not found"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_withValidBody_returnsOk() throws Exception {
        StudentCreateDto updateDto = new StudentCreateDto("Jane", "Doe", 1L);
        StudentDto responseDto = new StudentDto(1L, "Jane", "Doe", 1L, "Computer Science");
        when(studentService.update(eq(1L), any(StudentCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(studentService).delete(1L);
    }
}