package com.k3.examen.repository;

import com.k3.examen.model.Room;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRepository {
    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("adress"),
                rs.getInt("capacity")
        );
    }

}
