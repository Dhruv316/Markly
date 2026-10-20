package com.markly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Subject code is required")
    @Column(unique = true, nullable = false)
    public String code;

    @NotBlank(message = "Subject name is required")
    @Column(nullable = false)
    public String name;

    public Subject() {}

    public Subject(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
