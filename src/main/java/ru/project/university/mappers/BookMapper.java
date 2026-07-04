package ru.project.university.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.project.university.dto.BookCreateDto;
import ru.project.university.dto.BookDto;
import ru.project.university.models.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "student.id", target = "studentId")
    BookDto toDto(Book book);

    Book toEntity(BookCreateDto dto); // student не маппится, остаётся null — то, что нужно
}

