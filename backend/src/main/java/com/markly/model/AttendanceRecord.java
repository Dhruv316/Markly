package com.markly.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "attendance_date"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    public Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    public Subject subject;

    @Column(name = "attendance_date", nullable = false)
    public LocalDate attendanceDate;

    public boolean present;
}
