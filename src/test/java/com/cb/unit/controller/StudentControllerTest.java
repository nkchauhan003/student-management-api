package com.cb.unit.controller;

import com.cb.controller.StudentController;
import com.cb.model.Student;
import com.cb.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    private StudentService service;
    private StudentController controller;

    @BeforeEach
    void setUp() {
        service = mock(StudentService.class);
        controller = new StudentController(service);
    }

    @Test
    @DisplayName("Should return all students")
    @Tag("regression")
    void getAll_shouldReturnList() {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", "alice@example.com", "English"),
                new Student(2L, "Bob", "bob@example.com", "English")
        );
        when(service.getAllStudents()).thenReturn(students);

        List<Student> result = controller.getAll();

        assertThat(result).isEqualTo(students);
        verify(service).getAllStudents();
    }

    @Test
    @DisplayName("Should create a student")
    void create_shouldReturnCreatedStudent() {
        Student input = new Student();
        input.setName("Carol");
        input.setEmail("carol@example.com");
        Student saved = new Student(3L, "Carol", "carol@example.com", "Mathematics");
        when(service.createStudent(input)).thenReturn(saved);

        Student result = controller.create(input);

        assertThat(result).isEqualTo(saved);
        verify(service).createStudent(input);
    }

    @Test
    @DisplayName("Should get a student by id")
    void get_shouldReturnStudent() {
        Student student = new Student(4L, "Dave", "dave@example.com", "Science");
        when(service.getStudent(4L)).thenReturn(student);

        Student result = controller.get(4L);

        assertThat(result).isEqualTo(student);
        verify(service).getStudent(4L);
    }

    @Test
    @DisplayName("Should update a student")
    void update_shouldReturnUpdatedStudent() {
        Student update = new Student();
        update.setName("Eve");
        update.setEmail("eve@example.com");
        Student updated = new Student(5L, "Eve", "eve@example.com", "Physics");
        when(service.updateStudent(5L, update)).thenReturn(updated);

        Student result = controller.update(5L, update);

        assertThat(result).isEqualTo(updated);
        verify(service).updateStudent(5L, update);
    }

    @Test
    @DisplayName("Should delete a student")
    void delete_shouldCallService() {
        controller.delete(6L);

        verify(service).deleteStudent(6L);
    }

    @Test
    @DisplayName("Should pass correct arguments to service on update")
    void update_shouldPassCorrectArguments() {
        Student update = new Student();
        update.setName("Frank");
        update.setEmail("frank@example.com");
        controller.update(7L, update);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(service).updateStudent(idCaptor.capture(), studentCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(7L);
        assertThat(studentCaptor.getValue()).isEqualTo(update);
    }
}