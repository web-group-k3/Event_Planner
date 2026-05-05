package com.k3.examen.repository;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Room;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Repository
public class RoomRepository {
    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("adress"),
                rs.getInt("capacity")
        );
    }
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY name";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public Room findRoomById(String id) {
            String sql = "SELECT * FROM room WHERE id = ?";
            try (Connection con = DatabaseConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()){
                    return mapRow(rs);
                }
                return null;
            }
            catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    public Room save(Room room) throws SQLException {
        String sql = "INSERT INTO room (id,name,adress,capacity) VALUES (?,?,?,?) RETURNING *";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, room.getId());
            ps.setString(2, room.getName());
            ps.setString(3, room.getAddress());
            ps.setInt(4, room.getCapacity());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Error saving room");
    }
    public Room update(String id, Room room) throws SQLException {
        String sql = "UPDATE room SET name=? WHERE id=? RETURNING *";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, room.getName());
            ps.setString(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Error updating room");
    }
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            return ps.executeUpdate() > 0;
        }
    }
}
