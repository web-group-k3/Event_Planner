package com.k3.examen.validator;

import com.k3.examen.dto.SessionDto;
import com.k3.examen.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionValidator {
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
        if (session.getEndTime().isBefore(session.getStartTime())) {
            throw new IllegalArgumentException("start time is before end time");
        }

    }
}
