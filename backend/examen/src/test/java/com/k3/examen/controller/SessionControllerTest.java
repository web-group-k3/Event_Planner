package com.k3.examen.controller;

import com.k3.examen.dto.CreateSessionRequest;
import com.k3.examen.dto.SessionDTO;
import com.k3.examen.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void testGetSessionsByEventId() throws Exception {
        SessionDTO session1 = new SessionDTO();
        session1.setId(1);
        session1.setTitle("Session 1");
        session1.setEventId(1);

        SessionDTO session2 = new SessionDTO();
        session2.setId(2);
        session2.setTitle("Session 2");
        session2.setEventId(1);

        when(sessionService.getSessionsByEventId(1)).thenReturn(Arrays.asList(session1, session2));

        mockMvc.perform(get("/api/events/1/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Session 1"))
                .andExpect(jsonPath("$[1].title").value("Session 2"));
    }

    @Test
    void testGetSessionById() throws Exception {
        SessionDTO session = new SessionDTO();
        session.setId(1);
        session.setTitle("Test Session");
        session.setEventId(1);

        when(sessionService.getSessionById(1)).thenReturn(session);

        mockMvc.perform(get("/api/events/1/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Session"))
                .andExpect(jsonPath("$.eventId").value(1));
    }

    @Test
    void testCreateSession() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setTitle("New Session");
        request.setDescription("Session description");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        request.setCapacity(100);
        request.setRoomId(1);

        SessionDTO response = new SessionDTO();
        response.setId(1);
        response.setTitle("New Session");
        response.setEventId(1);

        when(sessionService.createSession(eq(1), any(CreateSessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/events/1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Session"));
    }

    @Test
    void testCreateSessionValidationError() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest();
        // Missing required fields

        mockMvc.perform(post("/api/events/1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSession() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setTitle("Updated Session");
        request.setDescription("Updated description");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        SessionDTO existingSession = new SessionDTO();
        existingSession.setId(1);
        existingSession.setTitle("Old Session");
        existingSession.setEventId(1);

        SessionDTO response = new SessionDTO();
        response.setId(1);
        response.setTitle("Updated Session");
        response.setEventId(1);

        when(sessionService.getSessionById(1)).thenReturn(existingSession);
        when(sessionService.updateSession(eq(1), any(CreateSessionRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/events/1/sessions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Session"));
    }

    @Test
    void testDeleteSession() throws Exception {
        SessionDTO existingSession = new SessionDTO();
        existingSession.setId(1);
        existingSession.setEventId(1);

        when(sessionService.getSessionById(1)).thenReturn(existingSession);

        mockMvc.perform(delete("/api/events/1/sessions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSessionNotFound() throws Exception {
        // Test when session doesn't exist - service throws exception
        when(sessionService.getSessionById(999)).thenThrow(new RuntimeException("Session not found"));

        mockMvc.perform(delete("/api/events/999/sessions/999"))
                .andExpect(status().isNotFound());
    }
}
