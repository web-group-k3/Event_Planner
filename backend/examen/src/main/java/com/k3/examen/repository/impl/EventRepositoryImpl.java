package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Event;
import com.k3.examen.repository.EventRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepositoryImpl implements EventRepository {
    private Event mapRow(ResultSet rs) throws SQLException {
        return new Event(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getString("location")
        );
    }
    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event ORDER BY start_date";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) events.add(mapRow(rs));
        return events;
    }
    public Event findById(String id) throws SQLException {
        String sql = "SELECT * FROM event WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }
    public Event save(Event event) throws SQLException {
        String sql = "INSERT INTO event (id,title, description, start_date, end_date, location) VALUES (?,?, ?, ?, ?, ?) RETURNING *";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, event.getId());
        stmt.setString(2, event.getTitle());
        stmt.setString(3, event.getDescription());
        stmt.setTimestamp(4, Timestamp.valueOf(event.getStartDate()));
        stmt.setTimestamp(5, Timestamp.valueOf(event.getEndDate()));
        stmt.setString(6, event.getLocation());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }
    public Event update(String id, Event event) throws SQLException {
        String sql = "UPDATE event SET title=?, description=?, start_date=?, end_date=?, location=? WHERE id=? RETURNING *";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, event.getTitle());
        stmt.setString(2, event.getDescription());
        stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
        stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
        stmt.setString(5, event.getLocation());
        stmt.setString(6, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);
        return stmt.executeUpdate() > 0;
    }
}
