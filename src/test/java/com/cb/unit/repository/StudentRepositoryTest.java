package com.cb.unit.repository;

import com.cb.model.Student;
import com.cb.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should save and retrieve a student by id")
    void saveAndFindById() {
        Student student = new Student();
        student.setName("Alice");
        student.setEmail("alice@example.com");
        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(studentRepository.findById(saved.getId())).contains(saved);
    }

    @Test
    @DisplayName("Should return true if student exists by email")
    void existsByEmail_true() {
        Student student = new Student();
        student.setName("Bob");
        student.setEmail("bob@example.com");
        studentRepository.save(student);

        boolean exists = studentRepository.existsByEmail("bob@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if student does not exist by email")
    void existsByEmail_false() {
        boolean exists = studentRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find all students")
    void findAllStudents() {
        Student s1 = new Student();
        s1.setName("Carol");
        s1.setEmail("carol@example.com");
        Student s2 = new Student();
        s2.setName("Dave");
        s2.setEmail("dave@example.com");
        studentRepository.save(s1);
        studentRepository.save(s2);

        assertThat(studentRepository.findAll())
                .extracting(Student::getEmail)
                .containsExactlyInAnyOrder("carol@example.com", "dave@example.com");
    }
}