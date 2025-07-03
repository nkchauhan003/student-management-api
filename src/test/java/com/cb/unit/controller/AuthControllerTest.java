package com.cb.unit.controller;

import com.cb.controller.AuthController;
import com.cb.dto.AuthRequest;
import com.cb.model.AppUser;
import com.cb.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    @DisplayName("Should register user and return response")
    void register_shouldCallServiceAndReturnResult() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        String expected = "registered";
        when(authService.register(user)).thenReturn(expected);

        String result = authController.register(user);

        assertThat(result).isEqualTo(expected);
        verify(authService).register(user);
    }

    @Test
    @DisplayName("Should login user and return token")
    void login_shouldCallServiceAndReturnResult() {
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("pass");
        String expected = "token";
        when(authService.login(req)).thenReturn(expected);

        String result = authController.login(req);

        assertThat(result).isEqualTo(expected);
        verify(authService).login(req);
    }

    @Test
    @DisplayName("Should pass correct user to AuthService on register")
    void register_shouldPassCorrectUser() {
        AppUser user = new AppUser();
        user.setUsername("user1");

        authController.register(user);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(authService).register(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Should pass correct AuthRequest to AuthService on login")
    void login_shouldPassCorrectRequest() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user2");
        req.setPassword("secret");

        authController.login(req);

        ArgumentCaptor<AuthRequest> captor = ArgumentCaptor.forClass(AuthRequest.class);
        verify(authService).login(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("user2");
        assertThat(captor.getValue().getPassword()).isEqualTo("secret");
    }
}