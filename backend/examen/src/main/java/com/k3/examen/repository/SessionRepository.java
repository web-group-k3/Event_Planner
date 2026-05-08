package com.k3.examen.repository;

import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findAll();
    Optional<Session> findById(String id);
    List<Session> findByEventId(String EventId);
    List<Session> findByRoomId(Room room);
    List<Session> findByRoomId(String roomId);
    List<Session> findByRoomIdAndEventId(String roomId, String EventId);
    Session save(Session session);
    Session update(Session session);
    void delete(Session session);
    void addSpeaker(Speaker speakerId);
    void deleteSpeaker(String sessionId,Speaker speakerId);

}