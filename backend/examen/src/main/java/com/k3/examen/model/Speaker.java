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
    private List<Session> sessions; // ✔️ correct


   /* public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Speaker s = new Speaker();
        public Builder id(Long id) { s.id = id; return this; }
        public Builder fullName(String v) { s.fullName = v; return this; }
        public Builder photoUrl(String v) { s.photoUrl = v; return this; }
        public Builder bio(String v) { s.bio = v; return this; }
        public Builder links(String v) { s.links = v; return this; }
        public Builder eventId(Long v) { s.eventId = v; return this; }
        public Builder sessions(List<Session> v) { s.sessions = v; return this; }
        public Speaker build() { return s; }
    }*/
}