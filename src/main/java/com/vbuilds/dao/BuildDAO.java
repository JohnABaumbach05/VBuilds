package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.Build;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BuildDAO {

    private Build mapBuild(ResultSet rs) throws SQLException {
        Build build = new Build();
        build.setTitle(rs.getString("title"));
        build.setHorsepower(rs.getInt("horsepower"));
        build.setHappiness(rs.getInt("happiness"));
        build.setReliability(rs.getInt("reliability"));
        build.setLoudness(rs.getInt("loudness"));
        build.setCost(rs.getInt("build_cost"));
        build.setTuner(rs.getString("tuner"));
        build.setBuildYear(rs.getInt("build_year"));
        build.setTrim(rs.getString("build_trim"));
        build.setBuildId(rs.getInt("build_id"));
        build.setOwnerID(rs.getInt("owner_id"));
        build.setFeatured(rs.getBoolean("featured"));
        build.setMainPhoto(rs.getString("main_photo"));
        build.setDescription(rs.getString("description"));
        build.setInProcess(rs.getBoolean("in_process"));
        build.setApproved(rs.getBoolean("approved"));
        return build;
    }


    public List<Build> getAllBuildsForAdmin() throws SQLException {
        return findBuilds(
                "SELECT * FROM builds ORDER BY date_created DESC",
                null
        );
    }


    private List<Build> findBuilds(String sql, Integer parameter) throws SQLException {
        List<Build> builds = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parameter != null) {
                stmt.setInt(1, parameter);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    builds.add(mapBuild(rs));
                }
            }
        }
        return builds;
    }

    private Build findOneBuild(String sql, int id) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapBuild(rs) : null;
            }
        }
    }

    public int addBuild(Build build) throws SQLException {
        String sql = "INSERT INTO builds " +
                "(owner_id, title, horsepower, happiness, reliability, loudness, " +
                "build_cost, tuner, build_year, build_trim, featured, main_photo, description, in_process, approved) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE, ?, ?, TRUE, FALSE) RETURNING build_id";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, build.getOwnerID());
            stmt.setString(2, build.getTitle());
            stmt.setInt(3, build.getHorsepower());
            stmt.setInt(4, build.getHappiness());
            stmt.setInt(5, build.getReliability());
            stmt.setInt(6, build.getLoudness());
            stmt.setInt(7, build.getCost());
            stmt.setString(8, build.getTuner());
            stmt.setInt(9, build.getBuildYear());
            stmt.setString(10, build.getTrim());
            stmt.setString(11, build.getMainPhoto());
            stmt.setString(12, build.getDescription());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("build_id");
            }
        }
        throw new SQLException("Failed to retrieve new build ID");
    }

    private void setFeatured(int id, boolean featured) throws SQLException {
        String sql = "UPDATE builds SET featured = ? WHERE build_id = ? AND approved = TRUE";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, featured);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
    public void unfeatureBuild(Build build) throws SQLException { setFeatured(build.getBuildId(), false); }
    public void unfeatureBuild(int id) throws SQLException { setFeatured(id, false); }
    public void makeBuildFeatured(Build build) throws SQLException { setFeatured(build.getBuildId(), true); }
    public void makeBuildFeatured(int id) throws SQLException { setFeatured(id, true); }


    public void updateBuild(Build build) throws SQLException {
        String sql = "UPDATE builds SET title = ?, horsepower = ?, happiness = ?, reliability = ?, " +
                "loudness = ?, build_cost = ?, tuner = ?, build_year = ?, build_trim = ?, " +
                "main_photo = ?, description = ? WHERE build_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, build.getTitle());
            stmt.setInt(2, build.getHorsepower());
            stmt.setInt(3, build.getHappiness());
            stmt.setInt(4, build.getReliability());
            stmt.setInt(5, build.getLoudness());
            stmt.setInt(6, build.getCost());
            stmt.setString(7, build.getTuner());
            stmt.setInt(8, build.getBuildYear());
            stmt.setString(9, build.getTrim());
            stmt.setString(10, build.getMainPhoto());
            stmt.setString(11, build.getDescription());
            stmt.setInt(12, build.getBuildId());
            stmt.executeUpdate();
        }
    }

    public List<Build> getAllBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE", null);
    }


    public Build getBuildById(int id) throws SQLException {
        return findOneBuild("SELECT * FROM builds WHERE build_id = ? AND approved = TRUE", id);
    }


    public Build getBuildByIdForModeration(int id) throws SQLException {
        return findOneBuild("SELECT * FROM builds WHERE build_id = ?", id);
    }


    public List<Build> getUserBuilds(int ownerID) throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE owner_id = ?", ownerID);
    }


    public void deleteBuild(int id) throws SQLException {
        String sql = "DELETE FROM builds WHERE build_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Build> getTopHorsepowerBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE ORDER BY horsepower DESC", null);
    }

    public List<Build> getTopHorsepowerBuilds(int limit) throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE ORDER BY horsepower DESC LIMIT ?", limit);
    }

    public List<Build> getFeaturedBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE AND featured = TRUE LIMIT 4", null);
    }

    public List<Build> getRandomBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE AND featured = FALSE ORDER BY RANDOM() LIMIT 4", null);
    }

    public List<Build> getNewestBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE approved = TRUE ORDER BY date_created DESC LIMIT 4", null);
    }


    public List<Build> getPendingBuilds() throws SQLException {
        return findBuilds("SELECT * FROM builds WHERE in_process = TRUE AND approved = FALSE ORDER BY date_created DESC", null);
    }

    public boolean approveBuild(int id) throws SQLException {
        String sql = "UPDATE builds SET in_process = FALSE, approved = TRUE " +
                "WHERE build_id = ? AND in_process = TRUE AND approved = FALSE";
        return updateModerationStatus(sql, id);
    }

    public boolean rejectBuild(int id) throws SQLException {
        String sql = "UPDATE builds SET in_process = FALSE, approved = FALSE, featured = FALSE " +
                "WHERE build_id = ? AND in_process = TRUE AND approved = FALSE";
        return updateModerationStatus(sql, id);
    }

    private boolean updateModerationStatus(String sql, int id) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}