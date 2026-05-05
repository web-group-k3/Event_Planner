package com.k3.examen.validator;

import com.k3.examen.dto.EventDto;

public class EventValidator {

    public static void validate(EventDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de l'événement est obligatoire");
        }
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
    }
}
