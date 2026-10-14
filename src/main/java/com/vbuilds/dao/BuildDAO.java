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

    public int addBuild(Build build) throws SQLException {

        String sql = "INSERT INTO builds " +
                "(owner_id, title, horsepower, happiness, reliability, loudness, " +
                "build_cost, tuner, build_year, build_trim, featured, main_photo, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING build_id";

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
            stmt.setBoolean(11, false);
            stmt.setString(12, build.getMainPhoto());
            stmt.setString(13, build.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("build_id");
                }
            }

            throw new SQLException("Failed to retrieve new build ID");
        }
    }



    public void unfeatureBuild(Build build) throws SQLException {
        Connection conn = Database.connect();

        String q = "UPDATE builds SET featured = ? WHERE build_id = ?";

        PreparedStatement stmt = conn.prepareStatement(q);

        stmt.setBoolean(1, false);
        stmt.setInt(2, build.getBuildId());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void unfeatureBuild(int id) throws SQLException {
        Connection conn = Database.connect();

        String q = "UPDATE builds SET featured = ? WHERE build_id = ?";

        PreparedStatement stmt = conn.prepareStatement(q);

        stmt.setBoolean(1, false);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void makeBuildFeatured(Build build) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE builds SET featured = ? WHERE build_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setBoolean(1, true);
        stmt.setInt(2, build.getBuildId());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void makeBuildFeatured(int id) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE builds SET featured = ? WHERE build_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setBoolean(1, true);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void updateBuild(Build build) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE builds SET title = ?, horsepower = ?, happiness = ?, reliability = ?, loudness = ?, " +
                "build_cost = ?, tuner = ?, build_year = ?, build_trim = ?, featured = ?, main_photo = ?, description = ? WHERE build_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, build.getTitle());
        stmt.setInt(2, build.getHorsepower());
        stmt.setInt(3, build.getHappiness());
        stmt.setInt(4, build.getReliability());
        stmt.setInt(5, build.getLoudness());
        stmt.setInt(6, build.getCost());
        stmt.setString(7, build.getTuner());
        stmt.setInt(8, build.getBuildYear());
        stmt.setString(9, build.getTrim());
        stmt.setBoolean(10, build.isFeatured());
        stmt.setString(11, build.getMainPhoto());
        stmt.setString(12, build.getDescription());
        stmt.setInt(13, build.getBuildId());


        stmt.executeUpdate();
        stmt.close();
        conn.close();



    }

    public List<Build> getAllBuilds() throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM BUILDS";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        ResultSet rs = stmt.executeQuery();

        List<Build> allBuilds = new ArrayList<>();

        while (rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            allBuilds.add(build);
        }
        rs.close();
        stmt.close();
        conn.close();
        return allBuilds;
    }

    public Build getBuildById(int id) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM builds where build_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        Build build = null;
        if(rs.next()) {
            build = new Build();

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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));
        }
        rs.close();
        stmt.close();
        conn.close();

        return build;
    }




    public List<Build> getUserBuilds(int ownerID) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM builds WHERE owner_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, ownerID);

        ResultSet rs = stmt.executeQuery();

        List<Build> userBuilds = new ArrayList<>();

        while (rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            userBuilds.add(build);
        }
        rs.close();
        stmt.close();
        conn.close();

        return userBuilds;

    }






    public void deleteBuild(int id) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "DELETE FROM builds WHERE build_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, id);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public List<Build> getTopHorsepowerBuilds() throws SQLException {
        Connection conn =  Database.connect();

        String sq1 = "SELECT * FROM builds ORDER BY horsepower DESC;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        List<Build> builds = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();



        while (rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            builds.add(build);
        }

        rs.close();
        stmt.close();
        conn.close();
        return builds;
    }

    public List<Build> getTopHorsepowerBuilds(int limit) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM builds ORDER BY horsepower DESC LIMIT ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);
        stmt.setInt(1, limit);
        List<Build> builds = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();


        while (rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            builds.add(build);
        }

        rs.close();
        stmt.close();
        conn.close();
        return builds;
    }

    public List<Build> getFeaturedBuilds() throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM builds WHERE featured = true LIMIT 4;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        List<Build> builds = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            builds.add(build);
        }
        rs.close();
        stmt.close();
        conn.close();
        return builds;
    }

    public List<Build> getRandomBuilds() throws SQLException {
        Connection conn = Database.connect();

        String q = "SELECT * FROM builds WHERE featured = false ORDER BY RANDOM() LIMIT 4;";

        PreparedStatement stmt = conn.prepareStatement(q);

        List<Build> builds = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            builds.add(build);
        }
        rs.close();
        stmt.close();
        conn.close();
        return builds;
    }

    public List<Build> getNewestBuilds() throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM builds ORDER BY date_created DESC LIMIT 4;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        List<Build> builds = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
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
            build.setFeatured(rs.getBoolean(("featured")));
            build.setMainPhoto(rs.getString(("main_photo")));
            build.setDescription(rs.getString(("description")));

            builds.add(build);
        }
        rs.close();
        stmt.close();
        conn.close();
        return builds;
    }

    
}
