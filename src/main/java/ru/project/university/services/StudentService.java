package ru.project.university.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.university.dto.StudentCreateDto;
import ru.project.university.dto.StudentDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.StudentMapper;
import ru.project.university.models.Faculty;
import ru.project.university.models.Student;
import ru.project.university.repositories.FacultyRepository;
import ru.project.university.repositories.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentMapper studentMapper;

    public List<StudentDto> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .toList();
    }

    public StudentDto findById(Long id) {
        return studentMapper.toDto(getStudentOrThrow(id));
    }

    @Transactional
    public StudentDto create(StudentCreateDto dto) {
        Faculty faculty = getFacultyOrThrow(dto.facultyId());
        Student student = new Student(dto.firstName(), dto.lastName(), faculty);
        return studentMapper.toDto(studentRepository.save(student));
    }

    @Transactional
    public StudentDto update(Long id, StudentCreateDto dto) {
        Student student = getStudentOrThrow(id);

        if (!student.getFaculty().getId().equals(dto.facultyId())) {
            student.setFaculty(getFacultyOrThrow(dto.facultyId()));
        }
        student.setFirstName(dto.firstName());
        student.setLastName(dto.lastName());

        return studentMapper.toDto(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = getStudentOrThrow(id);
        studentRepository.delete(student);
    }

    private Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", id));
    }

    private Faculty getFacultyOrThrow(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Faculty", id));
    }
}