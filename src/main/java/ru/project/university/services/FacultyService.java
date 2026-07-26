package ru.project.university.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.university.dto.FacultyCreateDto;
import ru.project.university.dto.FacultyDto;
import ru.project.university.exceptions.DuplicateResourceException;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.FacultyMapper;
import ru.project.university.models.Faculty;
import ru.project.university.repositories.FacultyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;

    public List<FacultyDto> findAll() {
        return facultyRepository.findAll().stream()
                .map(facultyMapper::toDto)
                .toList();
    }

    public FacultyDto findById(Long id) {
        return facultyMapper.toDto(getFacultyOrThrow(id));
    }

    @Transactional
    public FacultyDto create(FacultyCreateDto dto) {
        facultyRepository.findByName(dto.name()).ifPresent(f -> {
            throw new DuplicateResourceException("Faculty with name '" + dto.name() + "' already exists");
        });
        Faculty faculty = facultyMapper.toEntity(dto);
        return facultyMapper.toDto(facultyRepository.save(faculty));
    }

    @Transactional
    public FacultyDto update(Long id, FacultyCreateDto dto) {
        Faculty faculty = getFacultyOrThrow(id);

        facultyRepository.findByName(dto.name())
                .filter(f -> !f.getId().equals(id))
                .ifPresent(f -> {
                    throw new DuplicateResourceException("Faculty with name '" + dto.name() + "' already exists");
                });

        faculty.setName(dto.name());
        return facultyMapper.toDto(faculty);
    }

    @Transactional
    public void delete(Long id) {
        Faculty faculty = getFacultyOrThrow(id);
        facultyRepository.delete(faculty);
    }

    private Faculty getFacultyOrThrow(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Faculty", id));
    }
}