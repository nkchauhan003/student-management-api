package com.cb.integration;

import com.cb.dto.AuthRequest;
import com.cb.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwt;

    @BeforeEach
    void login() {
        AuthRequest req = new AuthRequest();
        req.setUsername("admin");
        req.setPassword("admin123");
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", req, String.class);
        jwt = response.getBody();
    }

    @Test
    void testGetAllStudentsSecured() {
        // Ensure JWT is present
        assertNotNull(jwt, "JWT token should not be null");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Student[]> response = restTemplate.exchange("/students", HttpMethod.GET, entity, Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
    }
}
