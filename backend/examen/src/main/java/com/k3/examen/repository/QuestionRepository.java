package com.k3.examen.repository;

import com.k3.examen.model.Question;
import com.k3.examen.config.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository {
    
    private final DatabaseConnection dbConnection;
    
    public QuestionRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public Question save(Question question) {
        String sql = "INSERT INTO question (content, author_name, session_id) " +
                     "VALUES (?, ?, ?) RETURNING id, upvotes, created_at";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question.getContent());
            stmt.setString(2, question.getAuthorName());
            stmt.setInt(3, question.getSessionId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                question.setId(rs.getInt("id"));
                question.setUpvotes(rs.getInt("upvotes"));
                question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            return question;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving question", e);
        }
    }
    
    public Optional<Question> findById(Integer id) {
        String sql = "SELECT * FROM question WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToQuestion(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding question by id", e);
        }
    }
    
    public List<Question> findBySessionId(Integer sessionId) {
        String sql = "SELECT * FROM question WHERE session_id = ? ORDER BY upvotes DESC, created_at DESC";
        List<Question> questions = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(mapRowToQuestion(rs));
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding questions by session id", e);
        }
    }
    
    public List<Question> findAll() {
        String sql = "SELECT * FROM question ORDER BY created_at DESC";
        List<Question> questions = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(mapRowToQuestion(rs));
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all questions", e);
        }
    }
    
    public Question upvote(Integer id) {
        String sql = "UPDATE question SET upvotes = upvotes + 1 WHERE id = ? RETURNING upvotes";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Question question = findById(id).orElseThrow();
                question.setUpvotes(rs.getInt("upvotes"));
                return question;
            }
            throw new RuntimeException("Question not found");
        } catch (SQLException e) {
            throw new RuntimeException("Error upvoting question", e);
        }
    }
    
    public void deleteById(Integer id) {
        String sql = "DELETE FROM question WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting question", e);
        }
    }
    
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM question WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if question exists", e);
        }
    }
    
    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setContent(rs.getString("content"));
        question.setAuthorName(rs.getString("author_name"));
        question.setUpvotes(rs.getInt("upvotes"));
        question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        question.setSessionId(rs.getInt("session_id"));
        return question;
    }
}
