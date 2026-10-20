package com.markly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Roll number is required")
    @Column(unique = true, nullable = false)
    public String rollNo;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    public String name;

    @Email(message = "Enter a valid email address")
    public String email;

    @NotBlank(message = "Class is required")
    @Column(nullable = false)
    public String className;

    public Student() {}

    public Student(String rollNo, String name, String email, String className) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.className = className;
    }
}
