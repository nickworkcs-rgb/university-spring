package ru.project.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.project.university.dto.FacultyCreateDto;
import ru.project.university.dto.FacultyDto;
import ru.project.university.exceptions.DuplicateResourceException;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.FacultyMapper;
import ru.project.university.models.Faculty;
import ru.project.university.repositories.FacultyRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyService facultyService;

    private Faculty faculty;
    private FacultyDto facultyDto;

    @BeforeEach
    void setUp() {
        faculty = new Faculty("Computer Science");
        faculty.setId(1L);
        facultyDto = new FacultyDto(1L, "Computer Science");
    }

    @Test
    void findAll_returnsMappedList() {
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));
        when(facultyMapper.toDto(faculty)).thenReturn(facultyDto);

        List<FacultyDto> result = facultyService.findAll();

        assertThat(result).containsExactly(facultyDto);
    }

    @Test
    void findById_whenExists_returnsDto() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyMapper.toDto(faculty)).thenReturn(facultyDto);

        FacultyDto result = facultyService.findById(1L);

        assertThat(result).isEqualTo(facultyDto);
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_whenNameFree_savesAndReturnsDto() {
        FacultyCreateDto createDto = new FacultyCreateDto("Computer Science");
        when(facultyRepository.findByName("Computer Science")).thenReturn(Optional.empty());
        when(facultyMapper.toEntity(createDto)).thenReturn(faculty);
        when(facultyRepository.save(faculty)).thenReturn(faculty);
        when(facultyMapper.toDto(faculty)).thenReturn(facultyDto);

        FacultyDto result = facultyService.create(createDto);

        assertThat(result).isEqualTo(facultyDto);
        verify(facultyRepository).save(faculty);
    }

    @Test
    void create_whenNameTaken_throwsDuplicate() {
        FacultyCreateDto createDto = new FacultyCreateDto("Computer Science");
        when(facultyRepository.findByName("Computer Science")).thenReturn(Optional.of(faculty));

        assertThatThrownBy(() -> facultyService.create(createDto))
                .isInstanceOf(DuplicateResourceException.class);

        verify(facultyRepository, never()).save(any());
    }

    @Test
    void update_whenMissing_throwsNotFound() {
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.update(99L, new FacultyCreateDto("New name")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_whenNameTakenByAnotherFaculty_throwsDuplicate() {
        Faculty other = new Faculty("Physics");
        other.setId(2L);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.findByName("Physics")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> facultyService.update(1L, new FacultyCreateDto("Physics")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_whenNameBelongsToSameFaculty_updatesSuccessfully() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.findByName("Computer Science")).thenReturn(Optional.of(faculty));
        when(facultyMapper.toDto(faculty)).thenReturn(facultyDto);

        FacultyDto result = facultyService.update(1L, new FacultyCreateDto("Computer Science"));

        assertThat(result).isEqualTo(facultyDto);
    }

    @Test
    void delete_whenExists_deletesFaculty() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        facultyService.delete(1L);

        verify(facultyRepository).delete(faculty);
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(facultyRepository, never()).delete(any());
    }
}