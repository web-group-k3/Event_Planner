package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Question;
import com.k3.examen.repository.QuestionRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {

    private final DatabaseConnection connection;

    public QuestionRepositoryImpl(DatabaseConnection connection) {
        this.connection = connection;
    }

    private static int id(String s) { return Integer.parseInt(s); }

    private Question mapRow(ResultSet rs) throws SQLException {
        return Question.builder()
                .id(String.valueOf(rs.getInt("id")))
                .sessionId(String.valueOf(rs.getInt("session_id")))
                .content(rs.getString("content"))
                .authorName(rs.getString("author_name"))
                .upvotes(rs.getInt("upvotes"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }

    @Override
    public List<Question> findBySessionId(String sessionId) {
        String sql = "SELECT id, content, author_name, session_id, upvotes, created_at FROM question WHERE session_id = ? ORDER BY upvotes DESC, created_at ASC";
        List<Question> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(sessionId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySessionId questions: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<Question> findById(String id) {
        String sql = "SELECT id, session_id, content, author_name, upvotes, created_at FROM question WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findById question: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Question save(Question question) {
        String sql = "INSERT INTO question (content, session_id, author_name, upvotes, created_at) VALUES (?, ?, ?, 0, NOW()) RETURNING id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question.getContent());
            ps.setInt(2, id(question.getSessionId()));
            ps.setString(3, question.getAuthorName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    question.setId(String.valueOf(rs.getInt("id")));
                    question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    question.setUpvotes(0);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving question: " + e.getMessage());
        }
        return question;
    }

    @Override
    public void upvote(String id) {
        String sql = "UPDATE question SET upvotes = upvotes + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error upvoting question: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM question WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting question: " + e.getMessage());
        }
    }

    @Override
    public void updateContent(String id, String newContent) {
        String sql = "UPDATE question SET content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newContent);
            ps.setInt(2, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updateContent question: " + e.getMessage());
        }
    }
}
