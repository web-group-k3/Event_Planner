package com.k3.examen.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

//@Repository
public class SessionRepository {

    private final DataSource dataSource;

    public SessionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isLive(Long sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM session WHERE id = ? AND NOW() BETWEEN start_time AND end_time";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}