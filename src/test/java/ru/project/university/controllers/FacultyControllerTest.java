package ru.project.university.controllers;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.project.university.dto.FacultyCreateDto;
import ru.project.university.dto.FacultyDto;
import ru.project.university.exceptions.DuplicateResourceException;

import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.services.FacultyService;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    @Test
    void findAll_returnsOkAndList() throws Exception {
        FacultyDto dto = new FacultyDto(1L, "Computer Science");
        when(facultyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Computer Science"));
    }

    @Test
    void findById_whenExists_returnsOk() throws Exception {
        FacultyDto dto = new FacultyDto(1L, "Computer Science");
        when(facultyService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/faculties/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Computer Science"));
    }

    @Test
    void findById_whenMissing_returnsNotFound() throws Exception {
        when(facultyService.findById(99L)).thenThrow(new ResourceNotFoundException("Faculty with id 99 not found"));

        mockMvc.perform(get("/api/faculties/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Faculty with id 99 not found"));
    }

    @Test
    void create_withValidBody_returnsCreated() throws Exception {
        FacultyCreateDto createDto = new FacultyCreateDto("Computer Science");
        FacultyDto responseDto = new FacultyDto(1L, "Computer Science");
        when(facultyService.create(any(FacultyCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_withBlankName_returnsBadRequest() throws Exception {
        FacultyCreateDto invalidDto = new FacultyCreateDto("");

        mockMvc.perform(post("/api/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verify(facultyService, never()).create(any());
    }

    @Test
    void create_withDuplicateName_returnsConflict() throws Exception {
        FacultyCreateDto createDto = new FacultyCreateDto("Computer Science");
        when(facultyService.create(any(FacultyCreateDto.class)))
                .thenThrow(new DuplicateResourceException("Faculty with name 'Computer Science' already exists"));

        mockMvc.perform(post("/api/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_withValidBody_returnsOk() throws Exception {
        FacultyCreateDto updateDto = new FacultyCreateDto("Physics");
        FacultyDto responseDto = new FacultyDto(1L, "Physics");
        when(facultyService.update(eq(1L), any(FacultyCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/faculties/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Physics"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/faculties/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(facultyService).delete(1L);
    }
}