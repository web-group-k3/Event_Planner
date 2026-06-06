package com.k3.examen.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.repository.SessionRepository;
@Repository
public class SessionRepositoryImpl implements SessionRepository {
    private final DatabaseConnection databaseConnection;
    public SessionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
private Session mapRow(ResultSet rs) throws SQLException {
    // Room imbriquée
    Room room = new Room();
    room.setId(rs.getString("room_id"));
    room.setName(rs.getString("room_name"));
    room.setAdress(rs.getString("room_adress"));
    room.setCapacity(rs.getInt("room_capacity"));

    return Session.builder()
            .id(rs.getString("id"))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .startTime(rs.getTimestamp("start_time").toLocalDateTime())
            .endTime(rs.getTimestamp("end_time").toLocalDateTime())
            .guestNumber(rs.getInt("guestNumber"))
            .eventId(rs.getString("event_id"))
            .roomId(rs.getString("room_id"))
            .room(room)  // ✅ room imbriquée
            .build();
}
// ✅ Requête avec JOIN room — à utiliser partout
private static final String SELECT_WITH_ROOM = """
    SELECT 
        s.id, s.title, s.description,
        s.start_time, s.end_time,
        s.guestNumber, s.event_id, s.room_id,
        r.name     AS room_name,
        r.adress   AS room_adress,
        r.capacity AS room_capacity
    FROM session s
    LEFT JOIN room r ON s.room_id = r.id
""";
    // FIND ALL SESSION
  @Override
public List<Session> findAll() {
    String sql = SELECT_WITH_ROOM + "ORDER BY s.start_time";
    List<Session> list = new ArrayList<>();
    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapRow(rs));
    } catch (SQLException e) {
        throw new RuntimeException("Error findAll sessions" + e.getMessage());
    }
    return list;
}
  @Override
public List<Session> findByEventId(String eventId) {
    String sql = SELECT_WITH_ROOM + "WHERE s.event_id = ? ORDER BY s.start_time";
    List<Session> sessions = new ArrayList<>();
    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, eventId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) sessions.add(mapRow(rs));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error find sessions by event id" + e.getMessage());
    }
    return sessions;
}
  @Override
public List<Session> findByRoomId(String roomId) {
    String sql = SELECT_WITH_ROOM + "WHERE s.room_id = ? ORDER BY s.start_time";
    List<Session> sessions = new ArrayList<>();
    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, roomId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) sessions.add(mapRow(rs));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error finding session by room id " + e.getMessage());
    }
    return sessions;
}

   @Override
public List<Session> findByRoomIdAndEventId(String roomId, String eventId) {
    String sql = SELECT_WITH_ROOM + "WHERE s.event_id = ? AND s.room_id = ? ORDER BY s.start_time";
    List<Session> list = new ArrayList<>();
    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, eventId);
        ps.setString(2, roomId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error findByEventIdAndRoomId" + e.getMessage());
    }
    return list;
}

    @Override
    public Session save(Session session) {
            String sql = "INSERT INTO session (id,title, description, start_time, end_time, guestNumber, event_id, room_id) VALUES (?,?, ?, ?, ?, ?, ?, ?)";
            try(Connection conn = databaseConnection.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, session.getId());
            ps.setString(2, session.getTitle());
            ps.setString(3, session.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(5,Timestamp.valueOf(session.getEndTime()));
            ps.setObject(6, session.getGuestNumber());
            ps.setObject(7, session.getEventId());
            ps.setObject(8, session.getRoomId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error saving session" +e.getMessage());
        }
        return session;
    }

    @Override
    public Session update(Session session) {
        String sql = """
            UPDATE session
            SET event_id = ?, room_id = ?, title = ?, description = ?,
                start_time = ?, end_time = ?, guestNumber = ?
            WHERE id = ?
            """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, session.getEventId());
            ps.setString(2, session.getRoomId());
            ps.setString(3, session.getTitle());
            ps.setString(4, session.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(session.getEndTime()));
            if (session.getGuestNumber() != null) ps.setInt(7, session.getGuestNumber());
            else ps.setNull(7, Types.INTEGER);
            ps.setString(8, session.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating session" + e.getMessage());
        }
        return session;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM session WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error delete session" + e.getMessage());
        }
    }

    @Override
    public void addSpeaker(String sessionId, String speakerId) {
        String sql = "INSERT INTO session_speakers (session_id, speaker_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setString(2, speakerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addSpeaker" + e.getMessage());
        }
    }

    @Override
    public void deleteSpeaker(String sessionId, String speakerId) {
        String sql = "DELETE FROM session_speakers WHERE session_id = ? AND speaker_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setString(2, speakerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removeSpeaker" + e.getMessage());
        }
    }

 @Override
public Optional<Session> findById(String id) {
    String sql = SELECT_WITH_ROOM + "WHERE s.id = ?";
    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Optional.of(mapRow(rs));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error finding session by id " + id + e.getMessage());
    }
    return Optional.empty();
}
    @Override
    public boolean existsConflictInRoom(String roomId, LocalDateTime startTime, LocalDateTime endTime, String excludeSessionId) {
        String sql = """
                SELECT 1 FROM session
                WHERE room_id = ?
                  AND id != ?
                  AND start_time < ?
                  AND end_time > ?
                LIMIT 1
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ps.setString(2, excludeSessionId == null ? "-1" : excludeSessionId);
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            ps.setTimestamp(4, Timestamp.valueOf(startTime));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error existsConflictInRoom"+ e.getMessage());
        }
    }
    @Override
    public boolean existsConflictForSpeaker(String speakerId, LocalDateTime startTime, LocalDateTime endTime, String excludeSessionId) {
        String sql = """
        SELECT 1 FROM session s
        JOIN session_speakers ss ON ss.session_id = s.id
        WHERE ss.speaker_id = ?
          AND s.id != ?
          AND s.start_time < ?
          AND s.end_time > ?
        LIMIT 1
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speakerId);
            ps.setString(2, excludeSessionId == null ? "-1" : excludeSessionId);
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            ps.setTimestamp(4, Timestamp.valueOf(startTime));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error existsConflictForSpeaker", e);
        }
    }
}
