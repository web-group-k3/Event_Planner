package com.k3.examen.repository;

import com.k3.examen.model.Event;
import com.k3.examen.model.Room;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<Event> findAll();
    Optional<Event> findById(String id);
    Event save(Event event);
    Event update(Event event);
    boolean delete(String id);
    List<Event> findByRoom(Room room);
}
