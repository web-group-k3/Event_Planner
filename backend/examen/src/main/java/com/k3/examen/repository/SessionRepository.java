package com.k3.examen.repository;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.dto.SpeakerDto;
import com.k3.examen.model.Event;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SessionRepository {
    private final DatabaseConnection databaseConnection;
    public SessionRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    private Session mapRow(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setId(rs.getString("id"));
        session.setTitle(rs.getString("title"));
        session.setDescription(rs.getString("description"));
        session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        session.setGuestNumber(rs.getInt("guestNumber"));
        Event event = new Event();
        event.setId(rs.getString("id"));
        session.setEventId(event);
        Room room = new Room();
        room.setId(rs.getString("id"));
        session.setRoomId(room);
        List<Speaker> speakers = new ArrayList<>();
        Speaker speaker = new Speaker();
        speaker.setFullName(rs.getString("full_name"));
        speakers.add(speaker);
        session.setSpeakers(speakers);
        return session;
    }
    public List<Session> findByEventId(String eventId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session WHERE event_id = ? ORDER BY start_time";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, eventId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) sessions.add(mapRow(rs));
        return sessions;
    }
    public List<Session> findByEventIdAndRoomId(String  eventId, String roomId) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session WHERE event_id = ? AND room_id = ? ORDER BY start_time";
       try(Connection conn= DatabaseConnection.getConnection()) {
           PreparedStatement ps=conn.prepareStatement(sql);
           ps.setString(1, eventId);
           ps.setString(2, roomId);
           ResultSet rs=ps.executeQuery();
           while (rs.next()) sessions.add(mapRow(rs));
           return sessions;

       }catch (
               SQLException e
       ){
           throw new RuntimeException("Erreur lors de la lecture de la session");
       }

    }
    public Session findById(String id) throws SQLException {
        String sql = "SELECT * FROM session WHERE id = ?";
        try(Connection conn= DatabaseConnection.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs=ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        }catch (
                SQLException e
        ){
            throw new RuntimeException("Erreur lors de la lecture de la session");
        }
        throw new RuntimeException("Erreur lors de la lecture de la session");
    }

    public List<Session> findByRoomId(String roomId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session WHERE room_id = ?";
        try(Connection conn= DatabaseConnection.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, roomId);
            ResultSet rs=ps.executeQuery();
            while (rs.next()) sessions.add(mapRow(rs));
            return sessions;
        }
        catch (SQLException e){
                throw new RuntimeException("Erreur lors de la lecture de la session");
        }
    }
    public Session create(Session session, List<String> speakersIds)  {
        try(Connection connection= DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO session (id,title, description, start_time, end_time, guest_Number, event_id, room_id) VALUES (?,?, ?, ?, ?, ?, ?, ?) RETURNING *";
            PreparedStatement ps=connection.prepareStatement(sql);
            ps.setString(1, session.getId());
            ps.setString(2, session.getTitle());
            ps.setString(3, session.getDescription());
            ps.setTimestamp(4,Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(5,Timestamp.valueOf(session.getEndTime()));
            ps.setInt(6, session.getGuestNumber());
            Event event = new Event();
            ps.setString(7, event.getId());
            Room room = new Room();
            ps.setString(8, room.getId());
            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                Session saved = mapRow(rs);
                if (speakersIds != null) {
                    for (String speakerId : speakersIds) {
                        String linkSql = "INSERT INTO session_speaker (session_id, speaker_id) VALUES (?, ?)";
                        PreparedStatement linkStmt = connection.prepareStatement(linkSql);
                        linkStmt.setString(1, saved.getId());
                        linkStmt.setString(2, speakerId);
                        linkStmt.executeUpdate();
                    }
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Erreur lors de la lecture de la session");
        }
        throw new RuntimeException("Erreur lors de la lecture de la session");
    }

public Session update(String id,Session session, List<String> speakersIds)  {
        try(Connection conn= DatabaseConnection.getConnection()) {
            String sql = "UPDATE session SET title=?, description=?, start_time=?, end_time=?, guest_Number=?, room_id=? WHERE id=? RETURNING *";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, session.getTitle());
            ps.setString(2, session.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            ps.setInt(5, session.getGuestNumber());
            Room room = new Room();
            ps.setString(6, room.getId());
            ps.setString(7, id);
            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                Session updated = mapRow(rs);
                if (speakersIds != null) {
                    conn.prepareStatement("DELETE FROM session_speaker WHERE session_id = " + id).executeUpdate();
                    for (String speakerId : speakersIds) {
                        String linkSql = "INSERT INTO session_speaker (session_id, speaker_id) VALUES (?, ?)";
                        PreparedStatement linkStmt = conn.prepareStatement(linkSql);
                        linkStmt.setString(1, id);
                        linkStmt.setString(2, speakerId);
                        linkStmt.executeUpdate();
                    }
                }
                return updated;
            }
        }catch (SQLException e){
            throw new RuntimeException("Erreur lors de la lecture de la session");
        }
        throw new RuntimeException("Erreur lors de la lecture de la session");
}
public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM session WHERE id=?";
        try(Connection conn= DatabaseConnection.getConnection()) {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, id);
            return ps.executeUpdate()>0;

        }
}
    public boolean isLive(Long sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM session WHERE id = ? AND NOW() BETWEEN start_time AND end_time";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}