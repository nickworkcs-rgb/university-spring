package ru.project.university.mappers;

import org.mapstruct.Mapper;
import ru.project.university.dto.FacultyCreateDto;
import ru.project.university.dto.FacultyDto;
import ru.project.university.models.Faculty;

@Mapper(componentModel = "spring")
public interface FacultyMapper {
    FacultyDto toDto(Faculty faculty);
    Faculty toEntity(FacultyCreateDto dto);
}
