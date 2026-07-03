package ru.project.university.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.university.models.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByFacultyId(Long facultyId, Pageable pageable);
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

}
