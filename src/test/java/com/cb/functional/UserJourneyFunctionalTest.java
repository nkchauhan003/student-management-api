package com.cb.functional;

import com.cb.dto.AuthRequest;
import com.cb.model.AppUser;
import com.cb.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserJourneyFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    @Test
    void registerLoginAndAccessStudentFlow() {
        // Step 1: Register
        AppUser user = new AppUser(null, "john", "john123", "ADMIN");
        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/auth/register", user, String.class);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // Step 2: Login
        AuthRequest loginReq = new AuthRequest("john", "john123");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/auth/login", loginReq, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        jwtToken = loginResponse.getBody();

        // Step 3: Create a Student
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Student student = new Student(null, "Anita", "anita@mail.com", "Maths");
        HttpEntity<Student> createRequest = new HttpEntity<>(student, headers);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity("/students", createRequest, Student.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Step 4: Fetch Student
        Long studentId = createResponse.getBody().getId();
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<Student> getResponse = restTemplate.exchange("/students/" + studentId, HttpMethod.GET, getRequest, Student.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Anita", getResponse.getBody().getName());
    }
}
