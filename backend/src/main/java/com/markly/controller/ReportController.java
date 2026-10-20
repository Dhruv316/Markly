package com.markly.controller;

import com.markly.model.AttendanceRecord;
import com.markly.repository.AttendanceRepository;
import com.markly.repository.StudentRepository;
import com.markly.repository.SubjectRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {
    /** Attendance below this percentage is flagged as low. */
    private static final double MINIMUM_PERCENTAGE = 75.0;

    public record StudentSummary(Long studentId, String rollNo, String name, String className,
                          long total, long present, double percentage) {}

    private final AttendanceRepository attendance;
    private final StudentRepository students;
    private final SubjectRepository subjects;

    public ReportController(AttendanceRepository attendance, StudentRepository students,
                            SubjectRepository subjects) {
        this.attendance = attendance;
        this.students = students;
        this.subjects = subjects;
    }

    @GetMapping("/reports/summary")
    public List<StudentSummary> summary(@RequestParam(required = false) Long subjectId) {
        return summarize(subjectId);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<StudentSummary> withData = summarize(null).stream().filter(s -> s.total() > 0).toList();
        double average = withData.stream().mapToDouble(StudentSummary::percentage).average().orElse(0);
        List<StudentSummary> low = withData.stream().filter(s -> s.percentage() < MINIMUM_PERCENTAGE).toList();
        return Map.of(
                "students", students.count(),
                "subjects", subjects.count(),
                "markedToday", attendance.countByAttendanceDate(LocalDate.now()),
                "averageAttendance", Math.round(average * 10) / 10.0,
                "lowAttendance", low);
    }

    private List<StudentSummary> summarize(Long subjectId) {
        List<AttendanceRecord> records = subjectId == null
                ? attendance.findAll() : attendance.findBySubjectId(subjectId);
        Map<Long, List<AttendanceRecord>> byStudent = records.stream()
                .collect(Collectors.groupingBy(a -> a.student.id));

        return students.findAll(Sort.by("rollNo")).stream().map(s -> {
            List<AttendanceRecord> mine = byStudent.getOrDefault(s.id, List.of());
            long total = mine.size();
            long present = mine.stream().filter(a -> a.present).count();
            double pct = total == 0 ? 0 : Math.round(present * 1000.0 / total) / 10.0;
            return new StudentSummary(s.id, s.rollNo, s.name, s.className, total, present, pct);
        }).toList();
    }
}
