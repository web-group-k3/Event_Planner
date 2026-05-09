package com.k3.examen.validator;

import com.k3.examen.dto.SessionDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Event;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.repository.SessionRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;

@Component
public class SessionValidator {
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    public SessionValidator(EventRepository eventRepository, SessionRepository sessionRepository) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
    }
    public  void validate(Session session) {
        if (session.getTitle() == null || session.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is blank");
        }
        if (session.getStartTime() == null) {
            throw new IllegalArgumentException("time is null");
        }
        if (session.getEndTime() == null) {
            throw new IllegalArgumentException("endTime is null");
        }
        if (session.getEndTime().isBefore(session.getStartTime())
        || session.getEndTime().isEqual(session.getStartTime())) {
            throw new IllegalArgumentException("start time is before end time");
        }

    }
    public void validateSessionAgainstEvent(Session session) {
        Event event = eventRepository.findById(session.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("event not found " + session.getEventId()));

        if (session.getStartTime().isBefore(event.getStartDate()))
            throw new IllegalArgumentException(
                    "session's start time (" + session.getStartTime() +
                            ") is before the begining of the (" + event.getStartDate() + ")"
            );

        if (session.getEndTime().isAfter(event.getEndDate()))
            throw new IllegalArgumentException(
                    "session's end time is (" + session.getEndTime() +
                            ") not before the event end time (" + event.getEndDate() + ")"
            );
    }
    public void validateRoomAvailability(Session session) {
               boolean roomConflict = sessionRepository.existsConflictInRoom(
                session.getRoomId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getId()
        );
        if (roomConflict) {
            throw new IllegalStateException(
                    "there are room conflict in session this room is already taked "
            );
        }
    }
    public void validateSpeakersAvailability(Session session) {
        if (session.getSpeakers() == null || session.getSpeakers().isEmpty()) return;

        for (Speaker speaker : session.getSpeakers()) {
            boolean speakerConflict = sessionRepository.existsConflictForSpeaker(
                    speaker.getId(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getId()
            );
            if (speakerConflict) {
                throw new IllegalStateException(
                        "the speaker '" + speaker.getFullName() +
                                "  is already taked "
                );
            }
        }
    }
}
