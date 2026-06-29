package com.k3.examen.service;

import com.k3.examen.model.Speaker;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SpeakerService {
    List<Speaker> getAllSpeakers();
    Speaker getSpeakerById(String id);
    Speaker getSpeakerWithSessions(String id);
    Speaker createSpeaker(Speaker speaker);
    Speaker updateSpeaker(String id, Speaker speaker);
    void deleteSpeaker(String id);
    List<Speaker> getSpeakersByRoom(String roomId);
    List<Speaker> getSpeakersByEvent(String eventId);
}
