package com.k3.examen.service;

import com.k3.examen.model.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(String id);
    Room createRoom(Room room);
    Room updateRoom(String id, Room room);
    void deleteRoom(String id);
}
