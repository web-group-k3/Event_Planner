package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Event;
import com.k3.examen.repository.EventRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final DatabaseConnection databaseConnection;

    public EventRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static int id(String s) { return Integer.parseInt(s); }

    private Event mapRow(ResultSet rs) throws SQLException {
        return Event.builder()
                .id(String.valueOf(rs.getInt("id")))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .startDate(rs.getTimestamp("start_date").toLocalDateTime())
                .endDate(rs.getTimestamp("end_date").toLocalDateTime())
                .location(rs.getString("location"))
                .build();
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, description, start_date, end_date, location FROM event ORDER BY start_date";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) events.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error findAll events: " + e.getMessage());
        }
        return events;
    }

    @Override
    public Optional<Event> findById(String id) {
        String sql = "SELECT id, title, description, start_date, end_date, location FROM event WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findById event: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Event save(Event event) {
        String sql = "INSERT INTO event (title, description, start_date, end_date, location) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            ps.setString(5, event.getLocation());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) event.setId(String.valueOf(rs.getInt("id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving event: " + e.getMessage());
        }
        return event;
    }

    @Override
    public Event update(Event event) {
        String sql = "UPDATE event SET title=?, description=?, start_date=?, end_date=?, location=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            ps.setString(5, event.getLocation());
            ps.setInt(6, id(event.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating event: " + e.getMessage());
        }
        return event;
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting event: " + e.getMessage());
        }
        return true;
    }

    @Override
    public List<Event> findByRoomId(String roomId) {
        String sql = """
            SELECT DISTINCT e.id, e.title, e.description, e.start_date, e.end_date, e.location
            FROM event e JOIN session s ON s.event_id = e.id
            WHERE s.room_id = ? ORDER BY e.start_date
            """;
        List<Event> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(roomId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByRoomId events: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Event> findBySpeakerId(String speakerId) {
        String sql = """
            SELECT DISTINCT e.id, e.title, e.description, e.start_date, e.end_date, e.location
            FROM event e
            JOIN session s ON s.event_id = e.id
            JOIN session_speaker ss ON ss.session_id = s.id
            WHERE ss.speaker_id = ? ORDER BY e.start_date
            """;
        List<Event> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(speakerId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySpeakerId events: " + e.getMessage());
        }
        return list;
    }
}
