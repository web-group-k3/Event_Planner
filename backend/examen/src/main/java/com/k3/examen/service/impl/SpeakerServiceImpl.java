package com.k3.examen.service.impl;

import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.repository.RoomRepository;
import com.k3.examen.repository.SessionRepository;
import com.k3.examen.repository.SpeakerRepository;
import com.k3.examen.service.SpeakerService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpeakerServiceImpl implements SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final SessionRepository sessionRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    public SpeakerServiceImpl(SpeakerRepository speakerRepository,
                              SessionRepository sessionRepository,
                              RoomRepository roomRepository,
                              EventRepository eventRepository) {
        this.speakerRepository  = speakerRepository;
        this.sessionRepository  = sessionRepository;
        this.roomRepository     = roomRepository;
        this.eventRepository    = eventRepository;
    }

    @Override
    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }

    @Override
    public Speaker getSpeakerById(String id) {
        return speakerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Speaker not found"));
    }

    @Override
    public Speaker getSpeakerWithSessions(String id) {
        Speaker speaker = getSpeakerById(id);
        List<Session>  sessions=sessionRepository.findAll().stream()
                .filter(s->speakerRepository.findBySessionId(s.getId())
                        .stream().anyMatch(sp->sp.getId().equals(id)))
                .toList();
        return speaker;
    }

    @Override
    public Speaker createSpeaker(Speaker speaker) {
        if (speaker.getId() == null) {
            throw new IllegalArgumentException("Speaker id cannot be null");
        }
        if (speaker.getFullName() == null || speaker.getFullName().isBlank()) {
            throw new IllegalArgumentException("Speaker full name cannot be null");
        }
        return speakerRepository.save(speaker);
    }

    @Override
    public Speaker updateSpeaker(String id, Speaker speaker) {
        getSpeakerById(id);
        speaker.setId(id);
        return speakerRepository.update(speaker);
    }

    @Override
    public void deleteSpeaker(String id) {
        getSpeakerById(id);
        speakerRepository.delete(id);
    }

    @Override
    public List<Speaker> getSpeakersByRoom(String roomId) {
        roomRepository.findRoomById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found"));
        return speakerRepository.findByRoomId(roomId);
    }

    @Override
    public List<Speaker> getSpeakersByEvent(String eventId) {
       eventRepository.findById(eventId)
               .orElseThrow(()-> new RuntimeException("Event not found"));
       return speakerRepository.findByEventId(eventId);
    }
}