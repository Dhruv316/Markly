package com.markly.controller;

import com.markly.exception.ApiException;
import com.markly.model.AttendanceRecord;
import com.markly.model.Student;
import com.markly.model.Subject;
import com.markly.repository.AttendanceRepository;
import com.markly.repository.StudentRepository;
import com.markly.repository.SubjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    /** One line of the attendance sheet; present is null when nothing is saved yet. */
    public record Row(Long studentId, String rollNo, String name, Boolean present) {}

    public record Mark(@NotNull Long studentId, boolean present) {}

    public record SaveRequest(@NotNull(message = "Choose a subject") Long subjectId,
                       @NotNull(message = "Choose a date") LocalDate date,
                       @NotNull List<Mark> records) {}

    private final AttendanceRepository attendance;
    private final StudentRepository students;
    private final SubjectRepository subjects;

    public AttendanceController(AttendanceRepository attendance, StudentRepository students,
                                SubjectRepository subjects) {
        this.attendance = attendance;
        this.students = students;
        this.subjects = subjects;
    }

    @GetMapping
    public List<Row> sheet(@RequestParam Long subjectId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<Long, Boolean> saved = attendance.findBySubjectIdAndAttendanceDate(subjectId, date).stream()
                .collect(Collectors.toMap(a -> a.student.id, a -> a.present));
        return students.findAll(Sort.by("rollNo")).stream()
                .map(s -> new Row(s.id, s.rollNo, s.name, saved.get(s.id)))
                .toList();
    }

    @PostMapping
    @Transactional
    public Map<String, Integer> save(@Valid @RequestBody SaveRequest request) {
        Subject subject = subjects.findById(request.subjectId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subject not found"));
        if (request.date().isAfter(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot mark attendance for a future date");
        }
        Map<Long, AttendanceRecord> existing = attendance
                .findBySubjectIdAndAttendanceDate(subject.id, request.date()).stream()
                .collect(Collectors.toMap(a -> a.student.id, Function.identity()));

        for (Mark mark : request.records()) {
            Student student = students.findById(mark.studentId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
            AttendanceRecord record = existing.getOrDefault(student.id, new AttendanceRecord());
            record.student = student;
            record.subject = subject;
            record.attendanceDate = request.date();
            record.present = mark.present();
            attendance.save(record);
        }
        return Map.of("saved", request.records().size());
    }
}
