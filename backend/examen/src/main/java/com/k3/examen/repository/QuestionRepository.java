package com.k3.examen.repository;

import com.k3.examen.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
//@Repository
public class QuestionRepository {

    private final DataSource dataSource;

    public List<Question> findBySessionId(Long sessionId) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE session_id = ? ORDER BY upvotes DESC, created_at ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapQuestion(rs));
            }
        }
        return list;
    }

    public Optional<Question> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM question WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapQuestion(rs));
            }
        }
        return Optional.empty();
    }

    public Question save(Question question) throws SQLException {
        String sql = "INSERT INTO question (content, author_name, upvotes, created_at, session_id) VALUES (?, ?, 0, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question.getContent());
            ps.setString(2, question.getAuthorName());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(4, question.getSessionId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) question.setId(rs.getLong("id"));
            }
        }
        question.setUpvotes(0);
        question.setCreatedAt(LocalDateTime.now());
        return question;
    }

    public int upvote(Long id) throws SQLException {
        String sql = "UPDATE question SET upvotes = upvotes + 1 WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    public int delete(Long id) throws SQLException {
        String sql = "DELETE FROM question WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        return Question.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .authorName(rs.getString("author_name"))
                .upvotes(rs.getInt("upvotes"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .sessionId(rs.getLong("session_id"))
                .build();
    }
}