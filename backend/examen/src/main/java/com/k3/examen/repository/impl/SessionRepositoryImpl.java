package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class SessionRepositoryImpl implements SessionRepository {
    @Autowired
    private DataSource dataSource;
    private Session mapRow(ResultSet rs) throws SQLException {
        return Session.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .guestNumber(rs.getInt("guestNumber"))
                .roomId(rs.getString("room_id"))
                .eventId(rs.getString("event_id"))
                .build();
    }
    // FIND ALL SESSION
    @Override
    public List<Session> findAll() {
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber,event_id,room_id FROM session";
        List<Session> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error findAll sessions" + e.getMessage());
        }
        return list;
    }
    @Override
    public List<Session> findByEventId(String eventId)  {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber,event_id,room_id  FROM session WHERE event_id = ? ORDER BY start_time";
        try(Connection conn = dataSource.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, eventId);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) sessions.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error find sessions by event id" + e.getMessage());
        }
        return sessions;
    }
    @Override
    public List<Session> findByRoomId(String RoomId) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber,event_id,room_id FROM session WHERE room_id = ? ORDER BY start_time";
        try(Connection conn= dataSource.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, RoomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) sessions.add(mapRow(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding session by room id " + RoomId + e.getMessage());
        }
        return sessions;
    }

    @Override
    public List<Session> findByRoomIdAndEventId(String roomId, String eventId) {
        String sql = """
        SELECT * FROM session
        WHERE event_id = ? AND room_id = ?
        ORDER BY start_time
        """;
        List<Session> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventIdAndRoomId"+ e.getMessage());
        }
        return list;
    }

    @Override
    public Session save(Session session) {
            String sql = "INSERT INTO session (id,title, description, start_time, end_time, guestNumber, event_id, room_id) VALUES (?,?, ?, ?, ?, ?, ?, ?)";
            try(Connection conn = dataSource.getConnection()) {
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
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
        String sql = """
        SELECT s.id, s.title, s.description, s.start_time, s.end_time,
               s.guestNumber, s.event_id, s.room_id,
               r.id as room_id_val, r.name as room_name, r.adress as room_adress,
               sp.id as speaker_id, sp.full_name, sp.bio, sp.photo_url, sp.links
        FROM session s
        LEFT JOIN room r ON r.id = s.room_id
        LEFT JOIN session_speakers ss ON ss.session_id = s.id
        LEFT JOIN speaker sp ON sp.id = ss.speaker_id
        WHERE s.id = ?
        """;

        java.util.Map<String, Session> sessionMap = new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sessionId = rs.getString("id");

                    if (!sessionMap.containsKey(sessionId)) {
                        Session session = Session.builder()
                                .id(sessionId)
                                .title(rs.getString("title"))
                                .description(rs.getString("description"))
                                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                                .guestNumber(rs.getInt("guestNumber"))
                                .room(rs.getString("room_name") != null ? Room.builder()
                                        .id(rs.getString("room_id_val"))
                                        .name(rs.getString("room_name"))
                                        .adress(rs.getString("room_adress"))
                                        .build() : null)
                                .speakers(new ArrayList<>())
                                .build();
                        sessionMap.put(sessionId, session);
                    }

                    String speakerId = rs.getString("speaker_id");
                    if (speakerId != null) {
                        Speaker speaker = Speaker.builder()
                                .id(speakerId)
                                .fullName(rs.getString("full_name"))
                                .bio(rs.getString("bio"))
                                .photoUrl(rs.getString("photo_url"))
                                .links(rs.getString("links"))
                                .build();
                        sessionMap.get(sessionId).getSpeakers().add(speaker);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error findById session: " + e.getMessage());
        }

        return sessionMap.isEmpty() ? Optional.empty() : Optional.of(sessionMap.values().iterator().next());
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
        try (Connection conn = dataSource.getConnection();
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
    @Override
    public List<Session> findByEventIdWithSpeakers(String eventId) {
        String sql = """
        SELECT s.id, s.title, s.description, s.start_time, s.end_time,
               s.guestNumber, s.event_id, s.room_id,
               sp.id as speaker_id, sp.full_name, sp.bio, sp.photo_url
        FROM session s
        LEFT JOIN session_speakers ss ON ss.session_id = s.id
        LEFT JOIN speaker sp ON sp.id = ss.speaker_id
        WHERE s.event_id = ?
        ORDER BY s.start_time
        """;

        java.util.Map<String, Session> sessionMap = new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sessionId = rs.getString("id");

                    // ✅ if/else direct — pas de lambda, pas de SQLException cachée
                    if (!sessionMap.containsKey(sessionId)) {
                        Session session = Session.builder()
                                .id(sessionId)
                                .title(rs.getString("title"))
                                .description(rs.getString("description"))
                                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                                .guestNumber(rs.getInt("guestNumber"))
                                .speakers(new ArrayList<>())
                                .build();
                        sessionMap.put(sessionId, session);
                    }

                    String speakerId = rs.getString("speaker_id");
                    if (speakerId != null) {
                        Speaker speaker = Speaker.builder()
                                .id(speakerId)
                                .fullName(rs.getString("full_name"))
                                .bio(rs.getString("bio"))
                                .build();
                        sessionMap.get(sessionId).getSpeakers().add(speaker);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventIdWithSpeakers: " + e.getMessage());
        }

        return new ArrayList<>(sessionMap.values());
    }
    @Override
    public List<Session> findAllWithSpeakers() {
        String sql = """
    SELECT s.id, s.title, s.description, s.start_time, s.end_time,
           s.guestNumber, s.event_id, s.room_id,
           r.id as room_id_val, r.name as room_name, r.adress as room_adress,
           sp.id as speaker_id, sp.full_name, sp.bio
    FROM session s
    LEFT JOIN room r ON r.id = s.room_id
    LEFT JOIN session_speakers ss ON ss.session_id = s.id
    LEFT JOIN speaker sp ON sp.id = ss.speaker_id
    ORDER BY s.start_time
    """;

        java.util.Map<String, Session> sessionMap = new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String sessionId = rs.getString("id");

                if (!sessionMap.containsKey(sessionId)) {
                    Session session = Session.builder()
                            .id(sessionId)
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                            .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                            .guestNumber(rs.getInt("guestNumber"))
                            .room(rs.getString("room_name") != null ? Room.builder()  // ✅
                                    .id(rs.getString("room_id_val"))
                                    .name(rs.getString("room_name"))
                                    .adress(rs.getString("room_adress"))
                                    .build() : null)
                            .speakers(new ArrayList<>())
                            .build();
                    sessionMap.put(sessionId, session);
                }

                String speakerId = rs.getString("speaker_id");
                if (speakerId != null) {
                    Speaker speaker = Speaker.builder()
                            .id(speakerId)
                            .fullName(rs.getString("full_name"))
                            .bio(rs.getString("bio"))
                            .build();
                    sessionMap.get(sessionId).getSpeakers().add(speaker);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error findAllWithSpeakers: " + e.getMessage());
        }

        return new ArrayList<>(sessionMap.values());
    }
}
