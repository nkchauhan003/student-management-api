package com.cb.unit.service;

import com.cb.dto.AuthRequest;
import com.cb.model.AppUser;
import com.cb.repository.UserRepository;
import com.cb.security.JwtUtil;
import com.cb.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, authenticationManager, userDetailsService);
    }

    @Test
    void register_shouldRegisterUser_whenUsernameNotExists() {
        AppUser user = new AppUser();
        user.setUsername("newuser");
        user.setPassword("rawpass");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");

        String result = authService.register(user);

        assertEquals("User registered successfully", result);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        assertEquals("newuser", captor.getValue().getUsername());
        assertEquals("encodedpass", captor.getValue().getPassword());
    }

    @Test
    void register_shouldThrow_whenUsernameExists() {
        AppUser user = new AppUser();
        user.setUsername("existing");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new AppUser()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(user));
        assertTrue(ex.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldAuthenticateAndReturnJwt_whenCredentialsValid() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user1");
        req.setPassword("pass1");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtUtil.generateToken("user1")).thenReturn("jwt-token");

        // authenticationManager.authenticate should not throw
        String token = authService.login(req);

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user1", "pass1")
        );
        verify(userDetailsService).loadUserByUsername("user1");
        verify(jwtUtil).generateToken("user1");
    }

    @Test
    void login_shouldThrow_whenCredentialsInvalid() {
        AuthRequest req = new AuthRequest();
        req.setUsername("baduser");
        req.setPassword("badpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Invalid username or password"));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}