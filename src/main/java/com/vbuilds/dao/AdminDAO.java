package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    public Map<String, Long> getDashboardStats()
            throws SQLException {

        Map<String, Long> stats = new HashMap<>();

        String sql = """
            SELECT
                (SELECT COUNT(*) FROM users) AS total_users,
                COUNT(*) AS total_builds,
                COUNT(*) FILTER (
                    WHERE in_process = TRUE
                ) AS pending_builds,
                COUNT(*) FILTER (
                    WHERE approved = TRUE
                ) AS approved_builds
            FROM builds
            """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                stats.put("totalUsers",
                        rs.getLong("total_users"));

                stats.put("totalBuilds",
                        rs.getLong("total_builds"));

                stats.put("pendingBuilds",
                        rs.getLong("pending_builds"));

                stats.put("approvedBuilds",
                        rs.getLong("approved_builds"));
            }
        }

        return stats;
    }
}