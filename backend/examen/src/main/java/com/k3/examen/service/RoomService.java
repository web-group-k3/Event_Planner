package com.k3.examen.service;

import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(String id);
    Room createRoom(Room room);
    Room updateRoom(String id, RoomUpdateRequest request);
    void deleteRoom(String id);
    List<Room> getRoomsByEvent(String eventId);
    List<Room> getRoomsBySpeaker(String speakerId);
    List<Room> getRoomsByAddress(String address);
}
