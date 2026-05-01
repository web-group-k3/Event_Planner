package com.k3.examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speaker {
    private Integer id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private Map<String, String> links; // JSONB stored as Map
    private Integer eventId;
    
    // Constructor without ID (for creation)
    public Speaker(String fullName, String photoUrl, String bio, Map<String, String> links, Integer eventId) {
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.bio = bio;
        this.links = links;
        this.eventId = eventId;
    }
}
