package com.k3.examen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerDTO {
    private Integer id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private Map<String, String> links;
    private Integer eventId;
}
