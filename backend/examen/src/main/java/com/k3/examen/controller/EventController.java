package com.k3.examen.controller;

import com.k3.examen.dto.EventDto;
import com.k3.examen.service.impl.EventServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private EventServiceImpl eventServiceImpl;
    public EventController(EventServiceImpl eventServiceImpl) {
        this.eventServiceImpl = eventServiceImpl;
    }
    @GetMapping
    public ResponseEntity<List<EventDto>> findAll() throws SQLException {
        return ResponseEntity.ok(eventServiceImpl.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable String id) throws SQLException {
        return ResponseEntity.ok(eventServiceImpl.findById(id));
    }
    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody EventDto dto) throws SQLException {
        return ResponseEntity.status(201).body(eventServiceImpl.create(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> update(@PathVariable String id, @RequestBody EventDto dto) throws SQLException {
        return ResponseEntity.ok(eventServiceImpl.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws SQLException {
        eventServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
