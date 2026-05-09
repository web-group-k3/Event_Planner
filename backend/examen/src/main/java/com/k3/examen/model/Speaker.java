package com.k3.examen.model;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Speaker {
    private String id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private String links;
    private List<Session> sessions;
}