package com.k3.examen.dto;

import com.k3.examen.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerDto {
    private String id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private String links;
    private Event eventId;
}
