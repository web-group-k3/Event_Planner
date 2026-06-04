package com.k3.examen.repository;

import com.k3.examen.model.Speaker;

import java.util.List;
import java.util.Optional;

public interface SpeakerRepository {
    List<Speaker> findAll();
    Optional<Speaker> findById(String id);
    List<Speaker> findBySessionId(String sessionId);
    Speaker save(Speaker speaker);
    Speaker update(Speaker speaker);
    boolean delete(String id);
    List<Speaker> findByRoomId(String roomId);
    List<Speaker> findByEventId(String eventId);
}
