package com.k3.examen.repository;

import com.k3.examen.model.Room;
import com.k3.examen.config.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepository {
    
    private final DatabaseConnection dbConnection;
    
    public RoomRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public Room save(Room room) {
        String sql = "INSERT INTO room (name, event_id) VALUES (?, ?) RETURNING id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getEventId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                room.setId(rs.getInt("id"));
            }
            return room;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving room", e);
        }
    }
    
    public Optional<Room> findById(Integer id) {
        String sql = "SELECT * FROM room WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToRoom(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room by id", e);
        }
    }
    
    public List<Room> findByEventId(Integer eventId) {
        String sql = "SELECT * FROM room WHERE event_id = ? ORDER BY name";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapRowToRoom(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms by event id", e);
        }
    }
    
    public List<Room> findAll() {
        String sql = "SELECT * FROM room ORDER BY name";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapRowToRoom(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all rooms", e);
        }
    }
    
    public Room update(Room room) {
        String sql = "UPDATE room SET name = ?, event_id = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getEventId());
            stmt.setInt(3, room.getId());
            stmt.executeUpdate();
            return room;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room", e);
        }
    }
    
    public void deleteById(Integer id) {
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room", e);
        }
    }
    
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM room WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if room exists", e);
        }
    }
    
    private Room mapRowToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setName(rs.getString("name"));
        room.setEventId(rs.getInt("event_id"));
        return room;
    }
}
