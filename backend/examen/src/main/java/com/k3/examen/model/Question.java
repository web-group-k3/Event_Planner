package com.k3.examen.model;

import java.time.LocalDateTime;

public class Question {
    private Long id;
    private String content;
    private String authorName;
    private Integer upvotes;
    private LocalDateTime createdAt;
    private Long sessionId;

    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Integer getUpvotes() { return upvotes; }
    public void setUpvotes(Integer upvotes) { this.upvotes = upvotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Question q = new Question();
        public Builder id(Long id) { q.id = id; return this; }
        public Builder content(String v) { q.content = v; return this; }
        public Builder authorName(String v) { q.authorName = v; return this; }
        public Builder upvotes(Integer v) { q.upvotes = v; return this; }
        public Builder createdAt(LocalDateTime v) { q.createdAt = v; return this; }
        public Builder sessionId(Long v) { q.sessionId = v; return this; }
        public Question build() { return q; }
    }
}