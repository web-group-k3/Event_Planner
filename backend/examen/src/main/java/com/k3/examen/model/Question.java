package com.k3.examen.model;

import java.time.LocalDateTime;

public class Question {
    private String id;
    private String content;
    private String authorName;
    private Integer upvotes;
    private LocalDateTime createdAt;
    private Session sessionId;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Question q = new Question();
        public Builder id(String id) { q.id = id; return this; }
        public Builder content(String v) { q.content = v; return this; }
        public Builder authorName(String v) { q.authorName = v; return this; }
        public Builder upvotes(Integer v) { q.upvotes = v; return this; }
        public Builder createdAt(LocalDateTime v) { q.createdAt = v; return this; }
        public Builder sessionId(Session v) { q.sessionId = v; return this; }
        public Question build() { return q; }
    }
}