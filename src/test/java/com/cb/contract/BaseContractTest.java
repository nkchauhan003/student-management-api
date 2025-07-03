package com.cb.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseContractTest {
    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        RestAssuredMockMvc.mockMvc(mockMvc);

        String username = "admin_" + java.util.UUID.randomUUID();
        String password = "admin123";

        // Register the user (ignore if already exists)
        String registerJson = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"roles\":\"ROLE_ADMIN\"}", username, password);
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(registerJson)
        );

        // Login to get JWT
        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        String token = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestAssuredMockMvc.requestSpecification = RestAssuredMockMvc
                .given()
                .header("Authorization", "Bearer " + token);
    }
}
