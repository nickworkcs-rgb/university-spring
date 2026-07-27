package ru.project.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.project.university.dto.StudentCreateDto;
import ru.project.university.dto.StudentDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.StudentMapper;
import ru.project.university.models.Faculty;
import ru.project.university.models.Student;
import ru.project.university.repositories.FacultyRepository;
import ru.project.university.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Faculty faculty;
    private Student student;
    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        faculty = new Faculty("Computer Science");
        faculty.setId(1L);

        student = new Student("John", "Doe", faculty);
        student.setId(10L);

        studentDto = new StudentDto(10L, "John", "Doe", 1L, "Computer Science");
    }

    @Test
    void findAll_returnsMappedList() {
        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        List<StudentDto> result = studentService.findAll();

        assertThat(result).containsExactly(studentDto);
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_whenFacultyExists_savesStudent() {
        StudentCreateDto createDto = new StudentCreateDto("John", "Doe", 1L);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        StudentDto result = studentService.create(createDto);

        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void create_whenFacultyMissing_throwsNotFound() {
        StudentCreateDto createDto = new StudentCreateDto("John", "Doe", 99L);
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    void update_whenFacultyUnchanged_doesNotLookUpFaculty() {
        StudentCreateDto updateDto = new StudentCreateDto("Jane", "Doe", 1L);
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        studentService.update(10L, updateDto);

        verify(facultyRepository, never()).findById(any());
        assertThat(student.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void update_whenFacultyChanged_resolvesNewFaculty() {
        Faculty newFaculty = new Faculty("Physics");
        newFaculty.setId(2L);
        StudentCreateDto updateDto = new StudentCreateDto("John", "Doe", 2L);

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(newFaculty));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        studentService.update(10L, updateDto);

        assertThat(student.getFaculty()).isEqualTo(newFaculty);
    }

    @Test
    void update_whenNewFacultyMissing_throwsNotFound() {
        StudentCreateDto updateDto = new StudentCreateDto("John", "Doe", 99L);
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.update(10L, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenExists_deletesStudent() {
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));

        studentService.delete(10L);

        verify(studentRepository).delete(student);
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).delete(any());
    }
}