package com.k3.examen.service;

import com.k3.examen.dto.EventDto;
import com.k3.examen.exception.ResourceNotFoundException;
import com.k3.examen.model.Event;
import com.k3.examen.repository.EventRepository;
import com.k3.examen.validator.EventValidator;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private EventRepository eventRepository;
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    public List<EventDto> findAll() throws SQLException {
        return eventRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public EventDto findById(String id) throws SQLException {
        Event event = eventRepository.findById(id);
        if (event == null) throw new ResourceNotFoundException("Événement introuvable avec l'id : " + id);
        return toDto(event);
    }
    public EventDto create(EventDto dto) throws SQLException {
        EventValidator.validate(dto);
        Event saved = eventRepository.save(toEntity(dto));
        return toDto(saved);
    }
    public EventDto update(String id, EventDto dto) throws SQLException {
        EventValidator.validate(dto);
        if (eventRepository.findById(id) == null)
            throw new ResourceNotFoundException("Événement introuvable avec l'id : " + id);
        Event updated = eventRepository.update(id, toEntity(dto));
        return toDto(updated);
    }
    public void delete(String id) throws SQLException {
        if (eventRepository.findById(id) == null)
            throw new ResourceNotFoundException("Événement introuvable avec l'id : " + id);
        eventRepository.delete(id);
    }
    private EventDto toDto(Event e) {
        return new EventDto(e.getId(), e.getTitle(), e.getDescription(),
                e.getStartDate(), e.getEndDate(), e.getLocation());
    }

    private Event toEntity(EventDto dto) {
        return new Event(null, dto.getTitle(), dto.getDescription(),
                dto.getStartDate(), dto.getEndDate(), dto.getLocation());
    }
}
