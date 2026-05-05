package com.k3.examen.validator;

import com.k3.examen.dto.SessionDto;

public class SessionValidator {
    public static void validate(SessionDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de la session est obligatoire");
        }
        if (dto.getStartTime() == null) {
            throw new IllegalArgumentException("L'heure de début est obligatoire");
        }
        if (dto.getEndTime() == null) {
            throw new IllegalArgumentException("L'heure de fin est obligatoire");
        }
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }
        if (dto.getRoomId() == null) {
            throw new IllegalArgumentException("La salle est obligatoire");
        }
    }
}
