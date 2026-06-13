package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
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
    public List<Speaker> findAll()  {
        List<Speaker> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,photo_url,bio,links FROM speaker ORDER BY full_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }catch (SQLException e) {
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
