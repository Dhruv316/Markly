package com.markly.controller;

import com.markly.exception.ApiException;
import com.markly.model.Subject;
import com.markly.repository.AttendanceRepository;
import com.markly.repository.SubjectRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;

    public SubjectController(SubjectRepository subjects, AttendanceRepository attendance) {
        this.subjects = subjects;
        this.attendance = attendance;
    }

    @GetMapping
    public List<Subject> all() {
        return subjects.findAll(Sort.by("code"));
    }

    @PostMapping
    public Subject create(@Valid @RequestBody Subject s) {
        if (subjects.existsByCode(s.code)) {
            throw new ApiException(HttpStatus.CONFLICT, "A subject with this code already exists");
        }
        s.id = null;
        return subjects.save(s);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendance.deleteBySubjectId(id);
        subjects.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
