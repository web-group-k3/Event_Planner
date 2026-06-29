package com.k3.examen.dto;

import com.k3.examen.model.Event;
import com.k3.examen.model.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerDto {
    private String id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private String links;
    private List<Event> eventId;
    private List<Session> sessionId;
}
