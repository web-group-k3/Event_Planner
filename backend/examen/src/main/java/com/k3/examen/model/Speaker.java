package com.k3.examen.model;

import java.util.List;

public class Speaker {
    private Long id;
    private String fullName;
    private String photoUrl;
    private String bio;
    private String links;
    private Long eventId;
    private List<Session> sessions;

    public Speaker() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getLinks() { return links; }
    public void setLinks(String links) { this.links = links; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public static Builder builder() { return new Builder(); }

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
    }
}