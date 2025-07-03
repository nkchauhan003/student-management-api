package com.cb.integration;

import com.cb.dto.AuthRequest;
import com.cb.model.AppUser;
import com.cb.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwt;

    @BeforeEach
    void login() {
        // Register admin user (ignore if already exists)
        var appUser = new AppUser();
        appUser.setUsername("admin");
        appUser.setPassword("admin123");
        appUser.setRole("ROLE_ADMIN");
        restTemplate.postForEntity("/auth/register", appUser, String.class);

        // Now login
        var req = new AuthRequest();
        req.setUsername("admin");
        req.setPassword("admin123");
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", req, String.class);
        jwt = response.getBody();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void testStudentCrudEndpoints() {
        // CREATE
        Student student = new Student();
        student.setName("Alice");
        student.setEmail("alice@example.com");
        HttpEntity<Student> createEntity = new HttpEntity<>(student, authHeaders());
        ResponseEntity<Student> createResp = restTemplate.postForEntity("/students", createEntity, Student.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        Student created = createResp.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());

        Long id = created.getId();

        // GET ALL
        HttpEntity<Void> getAllEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<Student[]> getAllResp = restTemplate.exchange("/students", HttpMethod.GET, getAllEntity, Student[].class);
        assertEquals(HttpStatus.OK, getAllResp.getStatusCode());
        assertNotNull(getAllResp.getBody());
        assertTrue(getAllResp.getBody().length > 0);

        // GET BY ID
        ResponseEntity<Student> getResp = restTemplate.exchange("/students/" + id, HttpMethod.GET, getAllEntity, Student.class);
        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertNotNull(getResp.getBody());
        assertEquals("Alice", getResp.getBody().getName());

        // UPDATE
        Student update = new Student();
        update.setName("Alice Updated");
        update.setEmail("alice.updated@example.com");
        HttpEntity<Student> updateEntity = new HttpEntity<>(update, authHeaders());
        ResponseEntity<Student> updateResp = restTemplate.exchange("/students/" + id, HttpMethod.PUT, updateEntity, Student.class);
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertNotNull(updateResp.getBody());
        assertEquals("Alice Updated", updateResp.getBody().getName());

        // DELETE
        ResponseEntity<Void> deleteResp = restTemplate.exchange("/students/" + id, HttpMethod.DELETE, getAllEntity, Void.class);
        assertEquals(HttpStatus.OK, deleteResp.getStatusCode());

        // GET BY ID after delete (should be 404 or null)
        ResponseEntity<Student> getAfterDelete = restTemplate.exchange("/students/" + id, HttpMethod.GET, getAllEntity, Student.class);
        assertTrue(getAfterDelete.getStatusCode().is4xxClientError() || getAfterDelete.getBody() == null);
    }
}