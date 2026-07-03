package ru.project.university.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.university.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByStudentIsNull(Pageable pageable);
    List<Book> findByStudentId(Long studentId);
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
}
