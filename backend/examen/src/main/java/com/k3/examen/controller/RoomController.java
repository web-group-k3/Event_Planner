package com.k3.examen.controller;

import com.k3.examen.dto.RoomDto;
import com.k3.examen.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private  final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    @GetMapping
    public ResponseEntity<List<RoomDto>> findAll() throws SQLException {
        return ResponseEntity.ok(roomService.findAll());
    }
    @PostMapping
    public ResponseEntity<RoomDto> create(@RequestBody RoomDto dto) throws SQLException {
        return ResponseEntity.status(201).body(roomService.create(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> update(@PathVariable String id,
                                          @RequestBody RoomDto dto) throws SQLException {
        return ResponseEntity.ok(roomService.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws SQLException {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
