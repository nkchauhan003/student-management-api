package com.cb.unit.security;

import com.cb.security.JwtFilter;
import com.cb.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig
@Import({SecurityConfigTest.Config.class, SecurityConfig.class})
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain filterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtFilter jwtFilter;

    @Test
    void securityBeans_shouldBePresent() {
        assertNotNull(filterChain);
        assertNotNull(passwordEncoder);
        assertNotNull(authenticationManager);
        assertNotNull(jwtFilter);
    }

    @Test
    void passwordEncoder_shouldBeBCrypt() {
        assertEquals("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder", passwordEncoder.getClass().getName());
    }

    @Test
    void filterChain_shouldContainJwtFilter() {
        assertNotNull(jwtFilter);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public JwtFilter jwtFilter() {
            return mock(JwtFilter.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }
    }
}