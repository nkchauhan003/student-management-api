package com.cb.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectWithoutToken() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldAllowWithValidToken() throws Exception {
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

        // Use the token in the Authorization header
        mockMvc.perform(get("/students").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
