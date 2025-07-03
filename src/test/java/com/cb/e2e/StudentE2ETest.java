package com.cb.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentE2ETest {

    @LocalServerPort
    private int port;

    private String token;

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        // Step 1: Register user
        Map<String, String> registerPayload = new HashMap<>();
        registerPayload.put("username", "testuser");
        registerPayload.put("password", "testpass");
        registerPayload.put("role", "ADMIN");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // Step 2: Login and extract token
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("username", "testuser");
        loginPayload.put("password", "testpass");

        token = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    @Test
    void createAndGetStudent_success() {
        // Step 3: Create a student
        Map<String, String> studentPayload = new HashMap<>();
        studentPayload.put("name", "Rahul");
        studentPayload.put("email", "rahul@mail.com");
        studentPayload.put("course", "CS");

        ValidatableResponse postResponse = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(studentPayload)
                .when()
                .post("/students")
                .then()
                .statusCode(201)
                .body("name", equalTo("Rahul"))
                .body("email", equalTo("rahul@mail.com"));

        // Step 4: Get list of students
        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/students")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }
}
