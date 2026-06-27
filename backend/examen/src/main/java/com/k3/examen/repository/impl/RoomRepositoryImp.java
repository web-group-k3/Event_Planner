package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import com.k3.examen.model.RoomUpdateRequest;
import com.k3.examen.repository.RoomRepository;
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
public class RoomRepositoryImp implements RoomRepository {
    @Autowired
    private DataSource dataSource;
    private Room mapRow(ResultSet rs) throws SQLException {
        return  Room.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .adress(rs.getString("adress"))
                .capacity(rs.getInt("capacity"))
                .build();

    }
    // FIND ALL ROOM
    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, name, adress, capacity FROM room ORDER BY name";
        try (Connection con = dataSource.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }
    //FIND ROOM BY ID
    @Override
    public Optional<Room> findRoomById(String id) {
        String sql = "SELECT id, name,adress,capacity  FROM room WHERE id = ?";
        try (Connection con = dataSource.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Error in findRoomById" + e.getMessage());
        }
        return Optional.empty();
    }
    //CREATE NEW ROOM
    @Override
    public Room save(Room room)  {
        String sql = "INSERT INTO room (id,name,adress,capacity) VALUES (?,?,?,?) RETURNING *";
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, room.getId());
            ps.setString(2, room.getName());
            ps.setString(3, room.getAdress());
            ps.setInt(4, room.getCapacity());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                room.setId(rs.getString("id"));
                room.setName(rs.getString("name"));
                room.setAdress(rs.getString("adress"));
                room.setCapacity(rs.getInt("capacity"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving room" + e.getMessage());
        }
        return room;
    }
    // CHANGE ROOM INFORMATION
    @Override
    public Room update(String id, RoomUpdateRequest request) {
       if (id ==null){
           throw new RuntimeException("id can not be null to update a room");
       }
        StringBuilder sql = new StringBuilder("UPDATE room SET ");
        List<Object> params = new ArrayList<>();
        if (request.getName() != null) {
            sql.append("name = ?, ");
            params.add(request.getName());
        }

        if (request.getCapacity() != null) {
            sql.append("capacity = ?, ");
            params.add(request.getCapacity());
        }
        // if no modification is giving
        if (params.isEmpty()) {
            throw new IllegalArgumentException("if want to uptade you have to give at least one param");
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(id);
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("Room with id " + id + " not found");
            }
        }catch (SQLException e){
            throw new RuntimeException("Error in updating room"+ e.getMessage());
        }
        return findRoomById(id).orElseThrow(() -> new RuntimeException("Room with id " + id + " not found after updating"));
    }
    public boolean delete(String id){
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e){
            throw new RuntimeException("Error in deleting room" + e.getMessage());
        }
    }

    @Override
    public List<Room> findByEventId(String eventId) {
        String sql = """
        SELECT DISTINCT r.* FROM room r
        JOIN session s ON s.room_id = r.id
        WHERE s.event_id = ?
        ORDER BY r.name
        """;
        List<Room> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByEventId rooms" +e);
        }
        return list;
    }

    @Override
    public List<Room> findBySpeakerId(String speakerId) {
        String sql = """
        SELECT DISTINCT r.* FROM room r
        JOIN session s ON s.room_id = r.id
        JOIN session_speakers ss ON ss.session_id = s.id
        WHERE ss.speaker_id = ?
        ORDER BY r.name
        """;
        List<Room> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speakerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findBySpeakerId rooms" + e);
        }
        return list;
    }

    @Override
    public List<Room> findByAddress(String address) {
        String sql = "SELECT * FROM room WHERE adress ILIKE ? ORDER BY name";
        List<Room> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + address + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByAddress rooms" + e.getMessage());
        }
        return list;
    }
}
