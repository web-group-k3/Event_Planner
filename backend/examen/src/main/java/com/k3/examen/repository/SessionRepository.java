package com.k3.examen.repository;

import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.config.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionRepository {
    
    private final DatabaseConnection dbConnection;
    private final SpeakerRepository speakerRepository;
    
    public SessionRepository(DatabaseConnection dbConnection, SpeakerRepository speakerRepository) {
        this.dbConnection = dbConnection;
        this.speakerRepository = speakerRepository;
    }
    
    public Session save(Session session) {
        String sql = "INSERT INTO session (title, description, start_time, end_time, capacity, event_id, room_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getTitle());
            stmt.setString(2, session.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            if (session.getCapacity() != null) {
                stmt.setInt(5, session.getCapacity());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, session.getEventId());
            if (session.getRoomId() != null) {
                stmt.setInt(7, session.getRoomId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                session.setId(rs.getInt("id"));
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving session", e);
        }
    }
    
    public Optional<Session> findById(Integer id) {
        String sql = "SELECT * FROM session WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Session session = mapRowToSession(rs);
                // Load speakers
                session.setSpeakers(speakerRepository.findBySessionId(id));
                return Optional.of(session);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding session by id", e);
        }
    }
    
    public List<Session> findByEventId(Integer eventId) {
        String sql = "SELECT * FROM session WHERE event_id = ? ORDER BY start_time";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Session session = mapRowToSession(rs);
                session.setSpeakers(speakerRepository.findBySessionId(session.getId()));
                sessions.add(session);
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sessions by event id", e);
        }
    }
    
    public List<Session> findByRoomId(Integer roomId) {
        String sql = "SELECT * FROM session WHERE room_id = ? ORDER BY start_time";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Session session = mapRowToSession(rs);
                session.setSpeakers(speakerRepository.findBySessionId(session.getId()));
                sessions.add(session);
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sessions by room id", e);
        }
    }
    
    public List<Session> findAll() {
        String sql = "SELECT * FROM session ORDER BY start_time";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Session session = mapRowToSession(rs);
                session.setSpeakers(speakerRepository.findBySessionId(session.getId()));
                sessions.add(session);
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all sessions", e);
        }
    }
    
    public Session update(Session session) {
        String sql = "UPDATE session SET title = ?, description = ?, start_time = ?, end_time = ?, " +
                     "capacity = ?, event_id = ?, room_id = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getTitle());
            stmt.setString(2, session.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            if (session.getCapacity() != null) {
                stmt.setInt(5, session.getCapacity());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, session.getEventId());
            if (session.getRoomId() != null) {
                stmt.setInt(7, session.getRoomId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setInt(8, session.getId());
            stmt.executeUpdate();
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating session", e);
        }
    }
    
    public void deleteById(Integer id) {
        // First delete from session_speaker
        String deleteSpeakersSql = "DELETE FROM session_speaker WHERE session_id = ?";
        String deleteSessionSql = "DELETE FROM session WHERE id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement stmt1 = conn.prepareStatement(deleteSpeakersSql);
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                
                PreparedStatement stmt2 = conn.prepareStatement(deleteSessionSql);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting session", e);
        }
    }
    
    public void addSpeakerToSession(Integer sessionId, Integer speakerId) {
        String sql = "INSERT INTO session_speaker (session_id, speaker_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, speakerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding speaker to session", e);
        }
    }
    
    public void removeSpeakerFromSession(Integer sessionId, Integer speakerId) {
        String sql = "DELETE FROM session_speaker WHERE session_id = ? AND speaker_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, speakerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing speaker from session", e);
        }
    }
    
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM session WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if session exists", e);
        }
    }
    
    public void updateSpeakers(Integer sessionId, List<Integer> speakerIds) {
        // First remove all existing speakers
        String deleteSql = "DELETE FROM session_speaker WHERE session_id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, sessionId);
                deleteStmt.executeUpdate();
                
                // Add new speakers
                String insertSql = "INSERT INTO session_speaker (session_id, speaker_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                for (Integer speakerId : speakerIds) {
                    insertStmt.setInt(1, sessionId);
                    insertStmt.setInt(2, speakerId);
                    insertStmt.executeUpdate();
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating session speakers", e);
        }
    }
    
    private Session mapRowToSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        session.setTitle(rs.getString("title"));
        session.setDescription(rs.getString("description"));
        session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        int capacity = rs.getInt("capacity");
        if (!rs.wasNull()) {
            session.setCapacity(capacity);
        }
        session.setEventId(rs.getInt("event_id"));
        int roomId = rs.getInt("room_id");
        if (!rs.wasNull()) {
            session.setRoomId(roomId);
        }
        return session;
    }
}
