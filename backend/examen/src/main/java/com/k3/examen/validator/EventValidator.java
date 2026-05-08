package com.k3.examen.validator;

import com.k3.examen.dto.EventDto;
import com.k3.examen.model.Event;
public class EventValidator {

    public void validate(Event event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("title cannot be blank");
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (event.getEndDate() == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("date cannot be before start date");
        }
    }
}
