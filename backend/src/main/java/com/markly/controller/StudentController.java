package com.markly.controller;

import com.markly.exception.ApiException;
import com.markly.model.Student;
import com.markly.repository.AttendanceRepository;
import com.markly.repository.StudentRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRepository students;
    private final AttendanceRepository attendance;

    public StudentController(StudentRepository students, AttendanceRepository attendance) {
        this.students = students;
        this.attendance = attendance;
    }

    @GetMapping
    public List<Student> all() {
        return students.findAll(Sort.by("rollNo"));
    }

    @PostMapping
    public Student create(@Valid @RequestBody Student s) {
        if (students.existsByRollNo(s.rollNo)) {
            throw new ApiException(HttpStatus.CONFLICT, "A student with this roll number already exists");
        }
        s.id = null;
        return students.save(s);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student s) {
        Student existing = students.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!existing.rollNo.equals(s.rollNo) && students.existsByRollNo(s.rollNo)) {
            throw new ApiException(HttpStatus.CONFLICT, "A student with this roll number already exists");
        }
        existing.rollNo = s.rollNo;
        existing.name = s.name;
        existing.email = s.email;
        existing.className = s.className;
        return students.save(existing);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendance.deleteByStudentId(id);
        students.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
