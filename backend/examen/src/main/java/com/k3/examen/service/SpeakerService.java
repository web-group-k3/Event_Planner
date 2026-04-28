package com.k3.examen.service;

import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SpeakerRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    public List<Speaker> getAll() throws SQLException {
        return speakerRepository.findAll();
    }

    public List<Speaker> getByEvent(Long eventId) throws SQLException {
        return speakerRepository.findByEventId(eventId);
    }

    public Speaker getDetail(Long id) throws SQLException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker non trouvé : " + id));
        speaker.setSessions(speakerRepository.findSessionsBySpeakerId(id));
        return speaker;
    }

    public Speaker create(Speaker speaker) throws SQLException {
        return speakerRepository.save(speaker);
    }

    public Speaker update(Long id, Speaker speaker) throws SQLException {
        speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker non trouvé : " + id));
        speakerRepository.update(id, speaker);
        speaker.setId(id);
        return speaker;
    }

    public void delete(Long id) throws SQLException {
        int rows = speakerRepository.delete(id);
        if (rows == 0) throw new RuntimeException("Speaker non trouvé : " + id);
    }
}