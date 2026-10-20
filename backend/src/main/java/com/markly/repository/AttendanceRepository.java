package com.markly.repository;

import com.markly.model.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findBySubjectIdAndAttendanceDate(Long subjectId, LocalDate date);
    List<AttendanceRecord> findBySubjectId(Long subjectId);
    long countByAttendanceDate(LocalDate date);
    void deleteByStudentId(Long studentId);
    void deleteBySubjectId(Long subjectId);
}
