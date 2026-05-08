package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Event;
import com.k3.examen.model.Room;
import com.k3.examen.repository.EventRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryImpl implements EventRepository {
    private DatabaseConnection databaseConnection;
    public EventRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    private Event mapRow(ResultSet rs) throws SQLException {
        return Event.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .startDate(rs.getTimestamp("start_date").toLocalDateTime())
                .endDate(rs.getTimestamp("end_date").toLocalDateTime())
                .location(rs.getString("location"))
                .build();
    }
    // TO FIND ALL EVENT
    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id,title, description, start_date,end_date, location FROM event ORDER BY start_date";
       try( Connection conn = databaseConnection.getConnection()) {
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery();
           while (rs.next()) events.add(mapRow(rs));
       }catch (SQLException e) {
           throw new RuntimeException("Error finding all events",e);
       }
       return events;
    }
    // FIND EVENT bY ID
    public Optional<Event> findById(String id)  {
        String sql = "SELECT id,title, description, start_date,end_date, location  FROM event WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            try( ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error finding event by id",e);
        }
        return Optional.empty();
    }
    public Event save(Event event)  {
        String sql = "INSERT INTO event (id,title, description, start_date, end_date, location) VALUES (?,?, ?, ?, ?, ?) RETURNING *";
        try(Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndDate()));
            stmt.setString(6, event.getLocation());
            try (ResultSet rs = stmt.executeQuery();){
                if (rs.next()) return mapRow(rs);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error saving event",e);
        }
        return event;
    }
    public Event update( Event event)  {
        String sql = "UPDATE event SET title=?, description=?, start_date=?, end_date=?, location=? WHERE id=?";
        try(Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getId());
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating event",e);
        }
        return event;
    }
    public boolean delete(String id) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (Connection conn=databaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error deleting event",e);
        }

        return true;
    }
    public List<Event> findByRoom(Room room) {
        return null;
    }
}
