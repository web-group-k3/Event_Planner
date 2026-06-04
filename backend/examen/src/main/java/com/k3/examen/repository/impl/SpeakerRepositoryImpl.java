package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Speaker;
import com.k3.examen.repository.SpeakerRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SpeakerRepositoryImpl implements SpeakerRepository {

    public SpeakerRepositoryImpl(DatabaseConnection databaseConnection) {
        // DatabaseConnection uses static methods — no instance needed
    }

    /** Parse string id to int — all PK/FK are INTEGER in DB */
    private static int id(String s) {
        return Integer.parseInt(s);
    }

    private Speaker mapRow(ResultSet rs) throws SQLException {
        return Speaker.builder()
                .id(String.valueOf(rs.getInt("id")))
                .fullName(rs.getString("full_name"))
                .photoUrl(rs.getString("photo_url"))
                .bio(rs.getString("bio"))
                .links(rs.getString("links"))
                .build();
    }

    @Override
    public List<Speaker> findAll() {
        List<Speaker> list = new ArrayList<>();
        String sql = """
            SELECT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links,
                   COUNT(ss.session_id) AS session_count
            FROM speaker sp
            LEFT JOIN session_speaker ss ON ss.speaker_id = sp.id
            GROUP BY sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links
            ORDER BY sp.full_name
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Speaker spk = mapRow(rs);
                // Build a minimal sessions list just to carry the count
                int count = rs.getInt("session_count");
                if (count > 0) {
                    List<com.k3.examen.model.Session> placeholder = new java.util.ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        placeholder.add(new com.k3.examen.model.Session());
                    }
                    spk.setSessions(placeholder);
                }
                list.add(spk);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all speakers: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<Speaker> findById(String id) {
        String sql = "SELECT id, full_name, photo_url, bio, links FROM speaker WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding speaker: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Speaker save(Speaker speaker) {
        String sql = "INSERT INTO speaker (full_name, photo_url, bio, links, event_id) VALUES (?, ?, ?, ?::jsonb, 1) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) speaker.setId(String.valueOf(rs.getInt("id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving speaker: " + e.getMessage());
        }
        return speaker;
    }

    @Override
    public Speaker update(Speaker speaker) {
        String sql = "UPDATE speaker SET full_name=?, photo_url=?, bio=?, links=?::jsonb WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            ps.setInt(5, id(speaker.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating speaker: " + e.getMessage());
        }
        return speaker;
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM speaker WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting speaker: " + e.getMessage());
        }
        return true;
    }

    @Override
    public List<Speaker> findByRoomId(String roomId) {
        String sql = """
            SELECT DISTINCT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links
            FROM speaker sp
            JOIN session_speaker ss ON ss.speaker_id = sp.id
            JOIN session s ON s.id = ss.session_id
            WHERE s.room_id = ?
            ORDER BY sp.full_name
            """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(roomId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByRoomId speakers: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Speaker> findByEventId(String eventId) {
        String sql = """
            SELECT DISTINCT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links
            FROM speaker sp
            JOIN session_speaker ss ON ss.speaker_id = sp.id
            JOIN session s ON s.id = ss.session_id
            WHERE s.event_id = ?
            ORDER BY sp.full_name
            """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(eventId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventId speakers: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Speaker> findBySessionId(String sessionId) {
        String sql = """
            SELECT sp.id, sp.full_name, sp.photo_url, sp.bio, sp.links
            FROM speaker sp
            JOIN session_speaker ss ON ss.speaker_id = sp.id
            WHERE ss.session_id = ?
            ORDER BY sp.full_name
            """;
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(sessionId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySessionId speakers: " + e.getMessage());
        }
        return list;
    }
}
