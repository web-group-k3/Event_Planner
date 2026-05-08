package com.k3.examen.service.impl;

import com.k3.examen.dto.RoomDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Room;
import com.k3.examen.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    public List<RoomDto> findAll() throws SQLException {
        return roomRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public RoomDto findById(String id) throws SQLException {
        Room room = roomRepository.findRoomById(id);
        if (room == null)
            throw new ResourceNotFoundException("Salle introuvable avec l'id : " + id);
        return toDto(room);
    }
    public RoomDto create(RoomDto dto) throws SQLException {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("Le nom de la salle est obligatoire");
        return toDto(roomRepository.save(new Room(null, dto.getName())));
    }
    public RoomDto update(String id, RoomDto dto) throws SQLException {
        if (roomRepository.findRoomById(id) == null)
            throw new ResourceNotFoundException("Salle introuvable avec l'id : " + id);
        return toDto(roomRepository.update(id, new Room(null, dto.getName())));
    }
    public void delete(String id) throws SQLException {
        if (roomRepository.findRoomById(id) == null)
            throw new ResourceNotFoundException("Salle introuvable avec l'id : " + id);
        roomRepository.delete(id);
    }
    private RoomDto toDto(Room r) {
        return new RoomDto(r.getId(), r.getName());
    }
}
