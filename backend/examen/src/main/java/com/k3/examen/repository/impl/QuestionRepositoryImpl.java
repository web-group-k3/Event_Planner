package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Question;
import com.k3.examen.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class QuestionRepositoryImpl implements QuestionRepository {
    @Autowired
    private DataSource dataSource;
    private Question mapRow(ResultSet rs) throws SQLException {
        return Question.builder()
                .id(rs.getString("id"))
                .sessionId(rs.getString("session_id"))
                .content(rs.getString("content"))
                .authorName(rs.getString("author"))
                .upvotes(rs.getInt("upvotes"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
    @Override
    public List<Question> findBySessionId(String sessionId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT id,content,author,session_id,upvotes,created_at FROM question WHERE session_id = ? ORDER BY upvotes DESC, created_at ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error finding question by sessionId"+ sessionId+e.getMessage());
        }
        return list;
    }
    @Override
    public Optional<Question> findById(String id)  {
        String sql = "SELECT id,session_id,content,author,upvotes,created_at FROM question WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error finding question by id" + id +e.getMessage());
        }
        return Optional.empty();
    }

    public Question save(Question question) {
        String sql = "INSERT INTO question (id,content, session_id, author, upvotes, created_at) VALUES (?,?, ?,?, 0,NOW()) RETURNING id, created_at";
        try (Connection conn =dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,question.getId());
            ps.setString(2, question.getContent());
            ps.setString(3, question.getSessionId());
            ps.setString(4, question.getAuthorName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    question.setUpvotes(0);
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error saving question" + e.getMessage());
        }
        return question;
    }
    @Override
    public void upvote(String id)  {
        String sql = "UPDATE question SET upvotes = upvotes + 1 WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, id);
             ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error upvoting question"+  e.getMessage());
        }
    }
    @Override
    public void delete(String id)  {
        String sql = "DELETE FROM question WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error deleting question"+ e.getMessage());
        }
    }

    @Override
    public void updateContent(String id, String newContent) {
        String sql = "UPDATE question SET content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newContent);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updateContent question" + e.getMessage());
        }
    }
    @Override
    public boolean hasVoted(String questionId, String anonymousId, String fingerprintId) {
        String sql = """
    SELECT 1 FROM question_votes
    WHERE question_id = ?
    AND (anonymous_voter_id = ? OR fingerprint_id = ?)
    LIMIT 1
    """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, questionId);
            ps.setString(2, (anonymousId != null && !anonymousId.isBlank()) ? anonymousId : "NON_SPECIFIE");
            ps.setString(3, (fingerprintId != null && !fingerprintId.isBlank()) ? fingerprintId : "NON_SPECIFIE");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification hasVoted : " + e.getMessage());
        }
    }
    @Override
    public void addVote(String questionId, String anonymousId, String fingerprintId) {
        String sql = """
        INSERT INTO question_votes (
            question_id,
            anonymous_voter_id,
            fingerprint_id,
            created_at
        )
        VALUES (?, ?, ?, NOW())
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, questionId);
            ps.setString(2, (anonymousId != null && !anonymousId.isBlank()) ? anonymousId : null);
            ps.setString(3, (fingerprintId != null && !fingerprintId.isBlank()) ? fingerprintId : null);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur SQL lors de addVote : " + e.getMessage());
        }
    }
}
