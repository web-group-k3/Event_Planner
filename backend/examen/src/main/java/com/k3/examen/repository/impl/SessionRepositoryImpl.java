package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Session;
import com.k3.examen.repository.SessionRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final DatabaseConnection databaseConnection;

    public SessionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static int id(String s) { return Integer.parseInt(s); }

    private Session mapRow(ResultSet rs) throws SQLException {
        return Session.builder()
                .id(String.valueOf(rs.getInt("id")))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .guestNumber(rs.getObject("capacity") != null ? rs.getInt("capacity") : null)
                .roomId(String.valueOf(rs.getInt("room_id")))
                .eventId(String.valueOf(rs.getInt("event_id")))
                .build();
    }

    @Override
    public List<Session> findAll() {
        String sql = "SELECT id, title, description, start_time, end_time, capacity, event_id, room_id FROM session";
        List<Session> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error findAll sessions: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Session> findByEventId(String eventId) {
        String sql = "SELECT id, title, description, start_time, end_time, capacity, event_id, room_id FROM session WHERE event_id = ? ORDER BY start_time";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(eventId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) sessions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventId sessions: " + e.getMessage());
        }
        return sessions;
    }

    @Override
    public List<Session> findByRoomId(String roomId) {
        String sql = "SELECT id, title, description, start_time, end_time, capacity, event_id, room_id FROM session WHERE room_id = ? ORDER BY start_time";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(roomId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) sessions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByRoomId sessions: " + e.getMessage());
        }
        return sessions;
    }

    @Override
    public List<Session> findBySpeakerId(String speakerId) {
        String sql = """
            SELECT s.id, s.title, s.description, s.start_time, s.end_time, s.capacity, s.event_id, s.room_id
            FROM session s
            JOIN session_speaker ss ON ss.session_id = s.id
            WHERE ss.speaker_id = ?
            ORDER BY s.start_time
            """;
        List<Session> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(speakerId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySpeakerId sessions: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Session> findByRoomIdAndEventId(String roomId, String eventId) {
        String sql = """
            SELECT id, title, description, start_time, end_time, capacity, event_id, room_id
            FROM session WHERE event_id = ? AND room_id = ? ORDER BY start_time
            """;
        List<Session> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(eventId));
            ps.setInt(2, id(roomId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByRoomIdAndEventId: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<Session> findById(String id) {
        String sql = "SELECT id, title, description, start_time, end_time, capacity, event_id, room_id FROM session WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findById session: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Session save(Session session) {
        String sql = "INSERT INTO session (title, description, start_time, end_time, capacity, event_id, room_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, session.getTitle());
            ps.setString(2, session.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            if (session.getGuestNumber() != null) ps.setInt(5, session.getGuestNumber());
            else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, id(session.getEventId()));
            ps.setInt(7, id(session.getRoomId()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) session.setId(String.valueOf(rs.getInt("id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving session: " + e.getMessage());
        }
        return session;
    }

    @Override
    public Session update(Session session) {
        String sql = """
            UPDATE session SET event_id=?, room_id=?, title=?, description=?,
            start_time=?, end_time=?, capacity=? WHERE id=?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(session.getEventId()));
            ps.setInt(2, id(session.getRoomId()));
            ps.setString(3, session.getTitle());
            ps.setString(4, session.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(session.getEndTime()));
            if (session.getGuestNumber() != null) ps.setInt(7, session.getGuestNumber());
            else ps.setNull(7, Types.INTEGER);
            ps.setInt(8, id(session.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating session: " + e.getMessage());
        }
        return session;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM session WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting session: " + e.getMessage());
        }
    }

    @Override
    public void addSpeaker(String sessionId, String speakerId) {
        String sql = "INSERT INTO session_speaker (session_id, speaker_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(sessionId));
            ps.setInt(2, id(speakerId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error addSpeaker: " + e.getMessage());
        }
    }

    @Override
    public void deleteSpeaker(String sessionId, String speakerId) {
        String sql = "DELETE FROM session_speaker WHERE session_id = ? AND speaker_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(sessionId));
            ps.setInt(2, id(speakerId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removeSpeaker: " + e.getMessage());
        }
    }

    @Override
    public boolean existsConflictInRoom(String roomId, LocalDateTime startTime, LocalDateTime endTime, String excludeSessionId) {
        String sql = """
            SELECT 1 FROM session WHERE room_id=? AND id!=? AND start_time<? AND end_time>? LIMIT 1
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(roomId));
            ps.setInt(2, excludeSessionId == null ? -1 : id(excludeSessionId));
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            ps.setTimestamp(4, Timestamp.valueOf(startTime));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error existsConflictInRoom: " + e.getMessage());
        }
    }

    @Override
    public boolean existsConflictForSpeaker(String speakerId, LocalDateTime startTime, LocalDateTime endTime, String excludeSessionId) {
        String sql = """
            SELECT 1 FROM session s
            JOIN session_speaker ss ON ss.session_id = s.id
            WHERE ss.speaker_id=? AND s.id!=? AND s.start_time<? AND s.end_time>? LIMIT 1
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(speakerId));
            ps.setInt(2, excludeSessionId == null ? -1 : id(excludeSessionId));
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            ps.setTimestamp(4, Timestamp.valueOf(startTime));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error existsConflictForSpeaker: " + e.getMessage());
        }
    }
}
