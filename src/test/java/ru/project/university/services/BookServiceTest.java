package ru.project.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.project.university.dto.BookCreateDto;
import ru.project.university.dto.BookDto;
import ru.project.university.exceptions.ResourceNotFoundException;
import ru.project.university.mappers.BookMapper;
import ru.project.university.models.Book;
import ru.project.university.models.Faculty;
import ru.project.university.models.Student;
import ru.project.university.repositories.BookRepository;
import ru.project.university.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDto bookDto;
    private Student student;

    @BeforeEach
    void setUp() {
        book = new Book("Author Name", "Book Title");
        book.setId(1L);

        bookDto = new BookDto(1L, "Author Name", "Book Title", null);

        Faculty faculty = new Faculty("Computer Science");
        faculty.setId(1L);
        student = new Student("John", "Doe", faculty);
        student.setId(10L);
    }

    @Test
    void findAll_returnsMappedList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.findAll();

        assertThat(result).containsExactly(bookDto);
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesBookWithoutStudent() {
        BookCreateDto createDto = new BookCreateDto("Author Name", "Book Title");
        when(bookMapper.toEntity(createDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.create(createDto);

        assertThat(result).isEqualTo(bookDto);
        assertThat(book.getStudent()).isNull();
    }

    @Test
    void update_whenExists_updatesAuthorAndTitle() {
        BookCreateDto updateDto = new BookCreateDto("New Author", "New Title");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        bookService.update(1L, updateDto);

        assertThat(book.getAuthor()).isEqualTo("New Author");
        assertThat(book.getTitle()).isEqualTo("New Title");
    }

    @Test
    void update_whenMissing_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, new BookCreateDto("A", "T")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenExists_deletesBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never()).delete(any());
    }

    @Test
    void assignToStudent_whenBothExist_setsStudent() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        bookService.assignToStudent(1L, 10L);

        assertThat(book.getStudent()).isEqualTo(student);
    }

    @Test
    void assignToStudent_whenBookMissing_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.assignToStudent(99L, 10L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).findById(any());
    }

    @Test
    void assignToStudent_whenStudentMissing_throwsNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.assignToStudent(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void unassign_whenExists_clearsStudent() {
        book.setStudent(student);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        bookService.unassign(1L);

        assertThat(book.getStudent()).isNull();
    }

    @Test
    void unassign_whenBookMissing_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.unassign(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}