package com.k3.examen.controller;

import com.k3.examen.dto.EventDto;
import com.k3.examen.service.impl.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @GetMapping
    public ResponseEntity<List<EventDto>> findAll() throws SQLException {
        return ResponseEntity.ok(eventService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable String id) throws SQLException {
        return ResponseEntity.ok(eventService.findById(id));
    }
    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody EventDto dto) throws SQLException {
        return ResponseEntity.status(201).body(eventService.create(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> update(@PathVariable String id, @RequestBody EventDto dto) throws SQLException {
        return ResponseEntity.ok(eventService.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws SQLException {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
