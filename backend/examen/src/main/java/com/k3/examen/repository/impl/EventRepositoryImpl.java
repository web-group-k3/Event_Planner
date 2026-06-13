package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Event;
import com.k3.examen.model.Room;
import com.k3.examen.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class EventRepositoryImpl implements EventRepository {
    @Autowired
    private DataSource dataSource;
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
       try( Connection conn = dataSource.getConnection()) {
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery();
           while (rs.next()) events.add(mapRow(rs));
       }catch (SQLException e) {
           throw new RuntimeException("Error finding all events" + e.getMessage());
       }
       return events;
    }
    // FIND EVENT bY ID
    public Optional<Event> findById(String id)  {
        String sql = "SELECT id,title, description, start_date,end_date, location  FROM event WHERE id = ?";
        try (Connection conn = dataSource.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            try( ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error finding event by id" + e.getMessage());
        }
        return Optional.empty();
    }
    public Event save(Event event)  {
        String sql = "INSERT INTO event (id,title, description, start_date, end_date, location) VALUES (?,?, ?, ?, ?, ?) ";
        try(Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndDate()));
            stmt.setString(6, event.getLocation());
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error saving event" + e.getMessage());
        }
        return event;
    }
    public Event update( Event event)  {
        String sql = "UPDATE event SET title=?, description=?, start_date=?, end_date=?, location=? WHERE id=?";
        try(Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getId());
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating event" + e.getMessage());
        }
        return event;
    }
    public boolean delete(String id) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (Connection conn=dataSource.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error deleting event" + e.getMessage());
        }

        return true;
    }

    @Override
    public List<Event> findByRoomId(String roomId) {
        String sql = """
        SELECT DISTINCT e.* FROM event e
        JOIN session s ON s.event_id = e.id
        WHERE s.room_id = ?
        ORDER BY e.start_date
        """;
        List<Event> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("no Event in this room" + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Event> findBySpeakerId(String speakerId) {
        String sql = """
        SELECT DISTINCT e.* FROM event e
        JOIN session s ON s.event_id = e.id
        JOIN session_speakers ss ON ss.session_id = s.id
        WHERE ss.speaker_id = ?
        ORDER BY e.start_date
        """;
        List<Event> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, speakerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("this speaker have no event" +   e.getMessage());
        }
        return list;
    }

}
