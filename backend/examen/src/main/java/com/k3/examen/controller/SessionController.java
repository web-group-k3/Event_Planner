package com.k3.examen.controller;

import com.k3.examen.dto.SessionDto;
import com.k3.examen.service.impl.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/sessions")
public class SessionController {
    private SessionService sessionService;
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    @GetMapping
    public ResponseEntity<List<SessionDto>> findAll(@PathVariable String eventId,
                                                    @RequestParam(required = false) String roomId) throws SQLException {
        return ResponseEntity.ok(sessionService.findByEventId(eventId, roomId));
    }
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDto> findById(@PathVariable String eventId,
                                               @PathVariable String sessionId) throws SQLException {
        return ResponseEntity.ok(sessionService.findById(eventId, sessionId));
    }
    @PostMapping
    public ResponseEntity<SessionDto> create(@PathVariable String eventId,
                                             @RequestBody SessionDto dto,
                                             @RequestParam(required = false) List<String> speakerIds) throws SQLException {
        return ResponseEntity.status(201).body(sessionService.create(eventId, dto, speakerIds));
    }
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionDto> update(@PathVariable String eventId,
                                             @PathVariable String sessionId,
                                             @RequestBody SessionDto dto,
                                             @RequestParam(required = false) List<String> speakerIds) throws SQLException {
        return ResponseEntity.ok(sessionService.update(eventId, sessionId, dto, speakerIds));
    }
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable String eventId,
                                       @PathVariable String sessionId) throws SQLException {
        sessionService.delete(eventId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
