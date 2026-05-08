package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Session;
import com.k3.examen.repository.SessionRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class SessionRepositoryImpl implements SessionRepository {
    private final DatabaseConnection databaseConnection;
    public SessionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    private Session mapRow(ResultSet rs) throws SQLException {
        return Session.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .guestNumber(rs.getInt("guestNumber"))
                .build();
    }
    // FIND ALL SESSION
    @Override
    public List<Session> findAll() {
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber,event_id,room_id FROM session";
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
    public List<Session> findByEventId(String eventId)  {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber,event_id,room_id  FROM session WHERE event_id = ? ORDER BY start_time";
        try(Connection conn = databaseConnection.getConnection()){
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
        try(Connection conn= DatabaseConnection.getConnection()) {
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
        try (Connection conn = DatabaseConnection.getConnection();
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
    public Optional<Session> findById(String id)  {
        String sql = "SELECT id,title,description,start_time,end_time,guestNumber FROM session WHERE id = ? ORDER BY start_time";
        try(Connection conn= databaseConnection.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, id);
            try(ResultSet rs=ps.executeQuery()){
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }catch (
                SQLException e
        ){
            throw new RuntimeException("Error finding session by id " + id +e.getMessage());
        }
        return Optional.empty();
    }

}
