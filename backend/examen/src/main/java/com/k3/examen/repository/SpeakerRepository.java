package com.k3.examen.repository;

import com.k3.examen.model.Speaker;
import com.k3.examen.config.DatabaseConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SpeakerRepository {
    
    private final DatabaseConnection dbConnection;
    private final ObjectMapper objectMapper;
    
    public SpeakerRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.objectMapper = new ObjectMapper();
    }
    
    public Speaker save(Speaker speaker) {
        String sql = "INSERT INTO speaker (full_name, photo_url, bio, links, event_id) " +
                     "VALUES (?, ?, ?, ?::jsonb, ?) RETURNING id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, speaker.getFullName());
            stmt.setString(2, speaker.getPhotoUrl());
            stmt.setString(3, speaker.getBio());
            if (speaker.getLinks() != null) {
                stmt.setString(4, objectMapper.writeValueAsString(speaker.getLinks()));
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setInt(5, speaker.getEventId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                speaker.setId(rs.getInt("id"));
            }
            return speaker;
        } catch (Exception e) {
            throw new RuntimeException("Error saving speaker", e);
        }
    }
    
    public Optional<Speaker> findById(Integer id) {
        String sql = "SELECT * FROM speaker WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToSpeaker(rs));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding speaker by id", e);
        }
    }
    
    public List<Speaker> findByEventId(Integer eventId) {
        String sql = "SELECT * FROM speaker WHERE event_id = ? ORDER BY full_name";
        List<Speaker> speakers = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                speakers.add(mapRowToSpeaker(rs));
            }
            return speakers;
        } catch (Exception e) {
            throw new RuntimeException("Error finding speakers by event id", e);
        }
    }
    
    public List<Speaker> findBySessionId(Integer sessionId) {
        String sql = "SELECT s.* FROM speaker s " +
                     "JOIN session_speaker ss ON s.id = ss.speaker_id " +
                     "WHERE ss.session_id = ? ORDER BY s.full_name";
        List<Speaker> speakers = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                speakers.add(mapRowToSpeaker(rs));
            }
            return speakers;
        } catch (Exception e) {
            throw new RuntimeException("Error finding speakers by session id", e);
        }
    }
    
    public List<Speaker> findAll() {
        String sql = "SELECT * FROM speaker ORDER BY full_name";
        List<Speaker> speakers = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                speakers.add(mapRowToSpeaker(rs));
            }
            return speakers;
        } catch (Exception e) {
            throw new RuntimeException("Error finding all speakers", e);
        }
    }
    
    public Speaker update(Speaker speaker) {
        String sql = "UPDATE speaker SET full_name = ?, photo_url = ?, bio = ?, links = ?::jsonb, event_id = ? " +
                     "WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, speaker.getFullName());
            stmt.setString(2, speaker.getPhotoUrl());
            stmt.setString(3, speaker.getBio());
            if (speaker.getLinks() != null) {
                stmt.setString(4, objectMapper.writeValueAsString(speaker.getLinks()));
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setInt(5, speaker.getEventId());
            stmt.setInt(6, speaker.getId());
            stmt.executeUpdate();
            return speaker;
        } catch (Exception e) {
            throw new RuntimeException("Error updating speaker", e);
        }
    }
    
    public void deleteById(Integer id) {
        // First delete from session_speaker
        String deleteAssociationSql = "DELETE FROM session_speaker WHERE speaker_id = ?";
        String deleteSpeakerSql = "DELETE FROM speaker WHERE id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement stmt1 = conn.prepareStatement(deleteAssociationSql);
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                
                PreparedStatement stmt2 = conn.prepareStatement(deleteSpeakerSql);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting speaker", e);
        }
    }
    
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM speaker WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if speaker exists", e);
        }
    }
    
    private Speaker mapRowToSpeaker(ResultSet rs) throws Exception {
        Speaker speaker = new Speaker();
        speaker.setId(rs.getInt("id"));
        speaker.setFullName(rs.getString("full_name"));
        speaker.setPhotoUrl(rs.getString("photo_url"));
        speaker.setBio(rs.getString("bio"));
        String linksJson = rs.getString("links");
        if (linksJson != null) {
            speaker.setLinks(objectMapper.readValue(linksJson, new TypeReference<Map<String, String>>() {}));
        }
        speaker.setEventId(rs.getInt("event_id"));
        return speaker;
    }
}
