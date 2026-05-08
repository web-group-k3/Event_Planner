package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SpeakerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpeakerRepositoryImpl implements SpeakerRepository {
    private DatabaseConnection databaseConnection;
    public SpeakerRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
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
    public List<Speaker> findAll()  {
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,photo_url,bio,links FROM speaker ORDER BY full_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }catch (SQLException e) {
            throw new RuntimeException("Error finding all speakers ",e);
        }
        return list;
    }
    @Override
    public Optional<Speaker> findById(String id)  {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,photo_url,bio,links FROM speaker ORDER BY full_name")) {
             ps.setString(1, id);
             try (ResultSet rs=ps.executeQuery()){
                 if (rs.next()) return Optional.of(mapRow(rs));
             }
        }catch (SQLException e) {
            throw new RuntimeException("Error finding speaker ",e);
        }
        return Optional.empty();
    }

    /*public List<Speaker> findByEventId(Long eventId) throws SQLException {
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM speaker WHERE event_id = ?")) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSpeaker(rs));
            }
        }
        return list;
    }*/
    // CREATE NEW SPEAKER
    @Override
    public Speaker save(Speaker speaker) {
        String sql = "INSERT INTO speaker (id,full_name, photo_url, bio, links, event_id) VALUES (?,?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,speaker.getId());
            ps.setString(2, speaker.getFullName());
            ps.setString(3, speaker.getPhotoUrl());
            ps.setString(4, speaker.getBio());
            ps.setString(5, speaker.getLinks());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) speaker.setId(rs.getString("id"));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error saving speaker ",e);
        }
        return speaker;
    }
    @Override
    public Speaker update( Speaker speaker)  {
        String sql = "UPDATE speaker SET full_name=?, photo_url=?, bio=?, links=?, event_id=? WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            ps.setString(5, speaker.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating speaker ",e);
        }
        return speaker;
    }

    public boolean delete(String id)  {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM speaker WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error deleting speaker ",e);
        }
        return true;
    }

    /*public List<Session> findSessionsBySpeakerId(Long speakerId) throws SQLException {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT s.id, s.title, s.start_time, s.end_time, s.room_id, s.event_id " +
                "FROM session s " +
                "JOIN session_speaker ss ON ss.session_id = s.id " +
                "WHERE ss.speaker_id = ? " +
                "ORDER BY s.start_time";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, speakerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(Session.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                            .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                            .roomId(rs.getLong("room_id"))
                            .eventId(rs.getLong("event_id"))
                            .build());
                }
            }
        }
        return list;
    }*/
    @Override
   public List<Speaker> findBySessionId(String sessionId){
        String sql = """
            SELECT s.* FROM speakers s
            JOIN session_speakers ss ON ss.speaker_id = s.id
            WHERE ss.session_id = ?
            ORDER BY s.full_name
            """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySessionId speakers", e);
        }
        return list;
   }
}
