package ru.project.university.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.university.dto.BookCreateDto;
import ru.project.university.dto.BookDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.BookMapper;
import ru.project.university.models.Book;
import ru.project.university.models.Student;
import ru.project.university.repositories.BookRepository;
import ru.project.university.repositories.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final BookMapper bookMapper;

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto findById(Long id) {
        return bookMapper.toDto(getBookOrThrow(id));
    }

    @Transactional
    public BookDto create(BookCreateDto dto) {
        Book book = bookMapper.toEntity(dto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto update(Long id, BookCreateDto dto) {
        Book book = getBookOrThrow(id);
        book.setAuthor(dto.author());
        book.setTitle(dto.title());
        return bookMapper.toDto(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        bookRepository.delete(book);
    }

    @Transactional
    public BookDto assignToStudent(Long bookId, Long studentId) {
        Book book = getBookOrThrow(bookId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", studentId));
        book.setStudent(student);
        return bookMapper.toDto(book);
    }

    @Transactional
    public BookDto unassign(Long bookId) {
        Book book = getBookOrThrow(bookId);
        book.setStudent(null);
        return bookMapper.toDto(book);
    }

    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Book", id));
    }
}