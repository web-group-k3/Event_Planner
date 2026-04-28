package com.k3.examen.repository;

import com.k3.examen.model.Session;
import com.k3.examen.model.Speaker;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SpeakerRepository {

    private final DataSource dataSource;

    public SpeakerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Speaker> findAll() throws SQLException {
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM speaker");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapSpeaker(rs));
        }
        return list;
    }

    public Optional<Speaker> findById(Long id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM speaker WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapSpeaker(rs));
            }
        }
        return Optional.empty();
    }

    public List<Speaker> findByEventId(Long eventId) throws SQLException {
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM speaker WHERE event_id = ?")) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSpeaker(rs));
            }
        }
        return list;
    }

    public Speaker save(Speaker speaker) throws SQLException {
        String sql = "INSERT INTO speaker (full_name, photo_url, bio, links, event_id) VALUES (?, ?, ?, ?::jsonb, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            ps.setLong(5, speaker.getEventId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) speaker.setId(rs.getLong("id"));
            }
        }
        return speaker;
    }

    public int update(Long id, Speaker speaker) throws SQLException {
        String sql = "UPDATE speaker SET full_name=?, photo_url=?, bio=?, links=?::jsonb, event_id=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getPhotoUrl());
            ps.setString(3, speaker.getBio());
            ps.setString(4, speaker.getLinks());
            ps.setLong(5, speaker.getEventId());
            ps.setLong(6, id);
            return ps.executeUpdate();
        }
    }

    public int delete(Long id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM speaker WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    public List<Session> findSessionsBySpeakerId(Long speakerId) throws SQLException {
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
    }

    private Speaker mapSpeaker(ResultSet rs) throws SQLException {
        return Speaker.builder()
                .id(rs.getLong("id"))
                .fullName(rs.getString("full_name"))
                .photoUrl(rs.getString("photo_url"))
                .bio(rs.getString("bio"))
                .links(rs.getString("links"))
                .eventId(rs.getLong("event_id"))
                .build();
    }
}