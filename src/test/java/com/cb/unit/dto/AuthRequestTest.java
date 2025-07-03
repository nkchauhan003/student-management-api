package com.cb.unit.dto;

import com.cb.dto.AuthRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthRequestTest {

    @Test
    @DisplayName("Should set and get username and password")
    void testGettersAndSetters() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user1");
        req.setPassword("pass1");

        assertThat(req.getUsername()).isEqualTo("user1");
        assertThat(req.getPassword()).isEqualTo("pass1");
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void testEqualsAndHashCode() {
        AuthRequest req1 = new AuthRequest();
        req1.setUsername("user1");
        req1.setPassword("pass1");

        AuthRequest req2 = new AuthRequest();
        req2.setUsername("user1");
        req2.setPassword("pass1");

        assertThat(req1).isEqualTo(req2);
        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user1");
        req.setPassword("pass1");

        String str = req.toString();
        assertThat(str).contains("user1");
        assertThat(str).contains("pass1");
    }
}