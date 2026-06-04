package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;
import com.k3.examen.repository.RoomRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryImp implements RoomRepository {

    private final DatabaseConnection databaseConnection;

    public RoomRepositoryImp(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static int id(String s) { return Integer.parseInt(s); }

    private Room mapRow(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(String.valueOf(rs.getInt("id")))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, name FROM room ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error findAll rooms: " + e.getMessage());
        }
        return rooms;
    }

    @Override
    public Optional<Room> findRoomById(String id) {
        String sql = "SELECT id, name FROM room WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findRoomById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO room (name, event_id) VALUES (?, ?) RETURNING id, name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getName());
            ps.setNull(2, Types.INTEGER); // event_id optional on creation
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    room.setId(String.valueOf(rs.getInt("id")));
                    room.setName(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving room: " + e.getMessage());
        }
        return room;
    }

    @Override
    public Room update(String id, RoomUpdateRequest request) {
        if (id == null) throw new RuntimeException("id cannot be null");
        StringBuilder sql = new StringBuilder("UPDATE room SET ");
        List<Object> params = new ArrayList<>();
        if (request.getName() != null) { sql.append("name = ?, "); params.add(request.getName()); }
        if (params.isEmpty()) throw new IllegalArgumentException("At least one field required");
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(id(id));
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            if (ps.executeUpdate() == 0) throw new RuntimeException("Room " + id + " not found");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room: " + e.getMessage());
        }
        return findRoomById(id).orElseThrow();
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room: " + e.getMessage());
        }
        return true;
    }

    @Override
    public List<Room> findByEventId(String eventId) {
        String sql = """
            SELECT DISTINCT r.id, r.name FROM room r
            JOIN session s ON s.room_id = r.id
            WHERE s.event_id = ? ORDER BY r.name
            """;
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(eventId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventId rooms: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Room> findBySpeakerId(String speakerId) {
        String sql = """
            SELECT DISTINCT r.id, r.name FROM room r
            JOIN session s ON s.room_id = r.id
            JOIN session_speaker ss ON ss.session_id = s.id
            WHERE ss.speaker_id = ? ORDER BY r.name
            """;
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id(speakerId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySpeakerId rooms: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Room> findByAddress(String address) {
        String sql = "SELECT id, name FROM room WHERE name ILIKE ? ORDER BY name";
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + address + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByAddress rooms: " + e.getMessage());
        }
        return list;
    }
}
