package com.k3.examen.service;

import com.k3.examen.dto.RoomDTO;
import com.k3.examen.model.Room;
import com.k3.examen.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    
    private final RoomRepository roomRepository;
    
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = new Room(roomDTO.getName(), roomDTO.getEventId());
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }
    
    public RoomDTO getRoomById(Integer id) {
        return roomRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }
    
    public List<RoomDTO> getRoomsByEventId(Integer eventId) {
        return roomRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RoomDTO updateRoom(Integer id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        
        room.setName(roomDTO.getName());
        room.setEventId(roomDTO.getEventId());
        
        Room updatedRoom = roomRepository.update(room);
        return convertToDTO(updatedRoom);
    }
    
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }
    
    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setEventId(room.getEventId());
        return dto;
    }
}
