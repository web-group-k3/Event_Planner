package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class SpeakerRepositoryImpl implements SpeakerRepository {
    @Autowired
    private DataSource dataSource;
    private Speaker mapRow(ResultSet rs) throws SQLException {
        return Speaker.builder()
                .id(rs.getString("id") )
                .fullName(rs.getString("full_name"))
                .photoUrl(rs.getString("photo_url"))
                .bio(rs.getString("bio"))
                .links(rs.getString("links"))
                .build();
    }
    // FIND ALL SPEAKERS
    @Override
    public List<Speaker> findAll() {
        List<Speaker> list = new ArrayList<>();
        String sql = """
        SELECT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links,
               COUNT(ss.session_id) as session_count
        FROM speaker sp
        LEFT JOIN session_speakers ss ON ss.speaker_id = sp.id
        GROUP BY sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links
        ORDER BY sp.full_name
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Speaker speaker = mapRow(rs);
                speaker.setSessionCount((int) rs.getLong("session_count")); // ✅
                list.add(speaker);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all speakers " + e.getMessage());
        }
        return list;
    }
    @Override
    public Optional<Speaker> findById(String id)  {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,photo_url,bio,links FROM speaker where id = ?")) {
             ps.setString(1, id);
             try (ResultSet rs=ps.executeQuery()){
                 if (rs.next()) return Optional.of(mapRow(rs));
             }
        }catch (SQLException e) {
            throw new RuntimeException("Error finding speaker " +e.getMessage());
        }
        return Optional.empty();
    }
    public Optional<Speaker> findByIdWithSessions(String id) {
        String sql = """
        SELECT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links,
               s.id as session_id, s.title as session_title, s.description as session_desc,
               s.start_time, s.end_time, s.guestNumber,
               r.id as room_id_val, r.name as room_name, r.adress as room_adress
        FROM speaker sp
        LEFT JOIN session_speakers ss ON ss.speaker_id = sp.id
        LEFT JOIN session s ON s.id = ss.session_id
        LEFT JOIN room r ON r.id = s.room_id
        WHERE sp.id = ?
        ORDER BY s.start_time
        """;

        Speaker[] result = {null};
        List<Session> sessions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (result[0] == null) {
                        result[0] = Speaker.builder()
                                .id(rs.getString("id"))
                                .fullName(rs.getString("full_name"))
                                .photoUrl(rs.getString("photo_url"))
                                .bio(rs.getString("bio"))
                                .links(rs.getString("links"))
                                .build();
                    }
                    String sessionId = rs.getString("session_id");
                    if (sessionId != null) {
                        Session session = Session.builder()
                                .id(sessionId)
                                .title(rs.getString("session_title"))
                                .description(rs.getString("session_desc"))
                                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                                .guestNumber(rs.getInt("guestNumber"))
                                .room(rs.getString("room_name") != null ? Room.builder()
                                        .id(rs.getString("room_id_val"))
                                        .name(rs.getString("room_name"))
                                        .adress(rs.getString("room_adress"))
                                        .build() : null)
                                .build();
                        sessions.add(session);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByIdWithSessions: " + e.getMessage());
        }

        if (result[0] == null) return Optional.empty();
        result[0].setSessions(sessions);
        result[0].setSessionCount(sessions.size());
        return Optional.of(result[0]);
    }
    // CREATE NEW SPEAKER
    @Override
    public Speaker save(Speaker speaker) {
        String sql = "INSERT INTO speaker (id,full_name, photo_url, bio, links) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,speaker.getId());
            ps.setString(2, speaker.getFullName());
            ps.setString(3, speaker.getPhotoUrl());
            ps.setString(4, speaker.getBio());
            ps.setString(5, speaker.getLinks());
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error saving speaker "+e.getMessage());
        }
        return speaker;
    }
    @Override
    public Speaker update( Speaker speaker)  {
        String sql = "UPDATE speaker SET full_name=?, photo_url=?, bio=?, links=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            ps.setString(5, speaker.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating speaker "+e.getMessage());
        }
        return speaker;
    }

    public boolean delete(String id)  {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM speaker WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error deleting speaker "+e.getMessage());
        }
        return true;
    }

    @Override
    public List<Speaker> findByRoomId(String roomId) {
        String sql = """
        SELECT DISTINCT sp.* FROM speaker sp
        JOIN session_speakers ss ON ss.speaker_id = sp.id
        JOIN session s ON s.id = ss.session_id
        WHERE s.room_id = ?
        ORDER BY sp.full_name
        """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByRoomId speakers"+  e.getMessage());
        }
        return list;
    }

    @Override
    public List<Speaker> findByEventId(String eventId) {
        String sql = """
        
                SELECT DISTINCT sp.* FROM speaker sp
        JOIN session_speakers ss ON ss.speaker_id = sp.id
        JOIN session s ON s.id = ss.session_id
        WHERE s.event_id = ?
        ORDER BY sp.full_name
        """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventId speakers"+ e.getMessage());
        }
        return list;
    }
    @Override
    public List<Speaker> findBySessionId(String sessionId){
        String sql = """
            SELECT s.* FROM speaker s
            JOIN session_speakers ss ON ss.speaker_id = s.id
            WHERE ss.session_id = ?
            ORDER BY s.full_name
            """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySessionId speakers" +e.getMessage());
        }
        return list;
   }
}
