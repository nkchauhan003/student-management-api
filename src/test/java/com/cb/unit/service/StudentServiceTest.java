package com.cb.unit.service;

import com.cb.model.Student;
import com.cb.repository.StudentRepository;
import com.cb.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents_shouldReturnList() {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", "alice@mail.com", "Math"),
                new Student(2L, "Bob", "bob@mail.com", "Physics")
        );
        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.getAllStudents();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(studentRepository).findAll();
    }

    @Test
    void getAllStudents_shouldReturnEmptyList() {
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Student> result = studentService.getAllStudents();

        assertTrue(result.isEmpty());
        verify(studentRepository).findAll();
    }

    @Test
    void getStudent_shouldReturnStudent() {
        Student s = new Student(1L, "John", "john@mail.com", "Math");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));

        Student result = studentService.getStudent(1L);

        assertEquals("John", result.getName());
        verify(studentRepository).findById(1L);
    }

    @Test
    void getStudent_shouldThrowIfNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.getStudent(99L));
        assertTrue(ex.getMessage().contains("Student not found with ID: 99"));
        verify(studentRepository).findById(99L);
    }

    @Test
    void createStudent_shouldSaveAndReturnStudent() {
        Student s = new Student(null, "Jane", "jane@mail.com", "CS");
        Student saved = new Student(10L, "Jane", "jane@mail.com", "CS");
        when(studentRepository.save(s)).thenReturn(saved);

        Student result = studentService.createStudent(s);

        assertEquals(10L, result.getId());
        assertEquals("Jane", result.getName());
        verify(studentRepository).save(s);
    }

    @Test
    void updateStudent_shouldUpdateAndReturnStudent() {
        Student existing = new Student(1L, "Old", "old@mail.com", "History");
        Student updated = new Student(null, "New", "new@mail.com", "Math");
        Student saved = new Student(1L, "New", "new@mail.com", "Math");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        Student result = studentService.updateStudent(1L, updated);

        assertEquals("New", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("Math", result.getCourse());

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        Student toSave = captor.getValue();
        assertEquals("New", toSave.getName());
        assertEquals("new@mail.com", toSave.getEmail());
        assertEquals("Math", toSave.getCourse());
    }

    @Test
    void updateStudent_shouldThrowIfNotFound() {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        Student updated = new Student(null, "X", "x@mail.com", "Y");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.updateStudent(2L, updated));
        assertTrue(ex.getMessage().contains("Student not found with ID: 2"));
        verify(studentRepository).findById(2L);
    }

    @Test
    void deleteStudent_shouldDeleteIfExists() {
        when(studentRepository.existsById(5L)).thenReturn(true);

        studentService.deleteStudent(5L);

        verify(studentRepository).deleteById(5L);
    }

    @Test
    void deleteStudent_shouldThrowIfNotFound() {
        when(studentRepository.existsById(6L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.deleteStudent(6L));
        assertTrue(ex.getMessage().contains("Student not found with ID: 6"));
        verify(studentRepository, never()).deleteById(anyLong());
    }
}