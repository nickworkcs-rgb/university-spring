package ru.project.university.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.project.university.dto.StudentDto;
import ru.project.university.models.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "faculty.id", target = "facultyId")
    @Mapping(source = "faculty.name", target = "facultyName")
    StudentDto toDto(Student student);
}