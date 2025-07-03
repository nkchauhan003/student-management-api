package com.cb.controller;

import com.cb.model.Student;
import com.cb.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Operations related to students")
public class StudentController {

    private final StudentService service;

    @GetMapping
    @Operation(
            summary = "Get all students",
            description = "Returns a list of all students"
    )
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @Operation(
            summary = "Create a new student",
            description = "Creates a new student and returns the created entity"
    )
    public Student create(@RequestBody Student student) {
        return service.createStudent(student);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a student by ID",
            description = "Returns a student by their unique ID"
    )
    public Student get(@PathVariable Long id) {
        return service.getStudent(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a student",
            description = "Updates an existing student by ID"
    )
    public Student update(@PathVariable Long id, @RequestBody Student s) {
        return service.updateStudent(id, s);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a student",
            description = "Deletes a student by their unique ID"
    )
    public void delete(@PathVariable Long id) {
        service.deleteStudent(id);
    }
}