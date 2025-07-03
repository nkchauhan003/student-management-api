package com.cb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student entity representing a student record")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the student", example = "1")
    private Long id;

    @Schema(description = "Name of the student", example = "Alice Smith")
    private String name;

    @Schema(description = "Email address of the student", example = "alice@example.com")
    private String email;

    @Schema(description = "Course enrolled by the student", example = "Computer Science")
    private String course;
}