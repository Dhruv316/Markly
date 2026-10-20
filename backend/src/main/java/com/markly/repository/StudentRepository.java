package com.markly.repository;

import com.markly.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByRollNo(String rollNo);
}
