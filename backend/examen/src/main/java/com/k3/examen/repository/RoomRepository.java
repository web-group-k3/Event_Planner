package com.k3.examen.repository;


import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    List<Room> findAll();
    Optional<Room> findRoomById(String id);
    Room save(Room room);
    Room update(String id, RoomUpdateRequest request);
    boolean delete(String id);

}
