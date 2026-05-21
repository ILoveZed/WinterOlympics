package org.informatics.winterolympics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.informatics.winterolympics.dto.LoginRequest;
import org.informatics.winterolympics.dto.LoginResponse;
import org.informatics.winterolympics.dto.RegisterRequest;
import org.informatics.winterolympics.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserService userService;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(userService))
                .build();
    }

    @Test
    void register_validRequest_returns200() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser", "pass123", "John", "Doe",
                "Bulgaria", "Male", Date.valueOf("2000-01-01"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void register_serviceThrowsException_returns500() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "existinguser", "pass123", "John", "Doe",
                "Bulgaria", "Male", Date.valueOf("2000-01-01"));

        doThrow(new RuntimeException("Username already taken"))
                .when(userService).register(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void login_validCredentials_returns200WithTokens() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "pass123");
        LoginResponse response = new LoginResponse(
                "eyJ.access.token", "eyJ.refresh.token", 300, "Bearer");

        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("eyJ.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("eyJ.refresh.token"))
                .andExpect(jsonPath("$.expiresIn").value(300))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_serviceThrowsException_returns500() throws Exception {
        LoginRequest request = new LoginRequest("user", "wrongpass");

        doThrow(new RuntimeException("Invalid credentials"))
                .when(userService).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
