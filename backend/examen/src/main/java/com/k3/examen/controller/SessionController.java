package com.k3.examen.controller;

import com.k3.examen.dto.CreateSessionRequest;
import com.k3.examen.dto.SessionDTO;
import com.k3.examen.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/sessions")
public class SessionController {
    
    private final SessionService sessionService;
    
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    // GET /api/events/{eventId}/sessions - Get all sessions for an event
    @GetMapping
    public ResponseEntity<List<SessionDTO>> getSessionsByEventId(@PathVariable Integer eventId) {
        List<SessionDTO> sessions = sessionService.getSessionsByEventId(eventId);
        return ResponseEntity.ok(sessions);
    }
    
    // GET /api/events/{eventId}/sessions/{sessionId} - Get a specific session
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> getSessionById(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId) {
        SessionDTO session = sessionService.getSessionById(sessionId);
        // Verify session belongs to the event
        if (!session.getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }
    
    // POST /api/events/{eventId}/sessions - Create a new session
    @PostMapping
    public ResponseEntity<SessionDTO> createSession(
            @PathVariable Integer eventId,
            @Valid @RequestBody CreateSessionRequest request) {
        SessionDTO createdSession = sessionService.createSession(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }
    
    // PUT /api/events/{eventId}/sessions/{sessionId} - Update a session
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> updateSession(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId,
            @Valid @RequestBody CreateSessionRequest request) {
        // Verify session exists and belongs to event
        SessionDTO existingSession = sessionService.getSessionById(sessionId);
        if (!existingSession.getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        SessionDTO updatedSession = sessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(updatedSession);
    }
    
    // DELETE /api/events/{eventId}/sessions/{sessionId} - Delete a session
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId) {
        // Verify session exists and belongs to event
        try {
            SessionDTO existingSession = sessionService.getSessionById(sessionId);
            if (!existingSession.getEventId().equals(eventId)) {
                return ResponseEntity.notFound().build();
            }
            sessionService.deleteSession(sessionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /api/events/{eventId}/sessions/{sessionId}/speakers/{speakerId} - Add speaker to session
    @PostMapping("/{sessionId}/speakers/{speakerId}")
    public ResponseEntity<SessionDTO> addSpeakerToSession(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId,
            @PathVariable Integer speakerId) {
        // Verify session belongs to event
        SessionDTO existingSession = sessionService.getSessionById(sessionId);
        if (!existingSession.getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        SessionDTO updatedSession = sessionService.addSpeakerToSession(sessionId, speakerId);
        return ResponseEntity.ok(updatedSession);
    }
    
    // DELETE /api/events/{eventId}/sessions/{sessionId}/speakers/{speakerId} - Remove speaker from session
    @DeleteMapping("/{sessionId}/speakers/{speakerId}")
    public ResponseEntity<SessionDTO> removeSpeakerFromSession(
            @PathVariable Integer eventId,
            @PathVariable Integer sessionId,
            @PathVariable Integer speakerId) {
        // Verify session belongs to event
        try {
            SessionDTO existingSession = sessionService.getSessionById(sessionId);
            if (!existingSession.getEventId().equals(eventId)) {
                return ResponseEntity.notFound().build();
            }
            SessionDTO updatedSession = sessionService.removeSpeakerFromSession(sessionId, speakerId);
            return ResponseEntity.ok(updatedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
