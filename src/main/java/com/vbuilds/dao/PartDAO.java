package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.Part;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartDAO {
    public void addPart(Part part) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "INSERT INTO parts(part_name, manufacturer, category, part_reliability, est_power_added, part_description, part_cost, makes_louder)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, part.getPartName());
        stmt.setString(2, part.getManufacturer());
        stmt.setString(3, part.getCategory());
        stmt.setInt(4, part.getPartReliability());
        stmt.setInt(5, part.getEstPowerAdded());
        stmt.setString(6, part.getPartDescription());
        stmt.setInt(7, part.getPartCost());
        stmt.setBoolean(8, part.isMakesLouder());

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public Part getPartById(int id) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM parts WHERE part_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Part part = null;
        if (rs.next()) {
            part = new Part();

            part.setPartName(rs.getString("part_name"));
            part.setManufacturer(rs.getString("manufacturer"));
            part.setCategory(rs.getString("category"));
            part.setPartReliability(rs.getInt("part_reliability"));
            part.setEstPowerAdded(rs.getInt("est_power_added"));
            part.setPartDescription(rs.getString("part_description"));
            part.setPartCost(rs.getInt("part_cost"));
            part.setMakesLouder(rs.getBoolean("makes_louder"));
            part.setPartID(rs.getInt("part_id"));
        }
        rs.close();
        stmt.close();
        conn.close();
        return part;
    }



    public List<Part> getAllParts() throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM parts";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        ResultSet rs = stmt.executeQuery();

        List<Part> partList = new ArrayList<>();

        while (rs.next()) {
            Part part = new Part();

            part.setPartName(rs.getString("part_name"));
            part.setManufacturer(rs.getString("manufacturer"));
            part.setCategory(rs.getString("category"));
            part.setPartReliability(rs.getInt("part_reliability"));
            part.setEstPowerAdded(rs.getInt("est_power_added"));
            part.setPartDescription(rs.getString("part_description"));
            part.setPartCost(rs.getInt("part_cost"));
            part.setMakesLouder(rs.getBoolean("makes_louder"));
            part.setPartID(rs.getInt("part_id"));

            partList.add(part);
        }
        rs.close();
        stmt.close();
        conn.close();

        return partList;
    }

    public void updatePart(Part part) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE parts " +
                "SET part_name = ?, " +
                "manufacturer = ?, " +
                "category = ?, " +
                "part_reliability = ?, " +
                "est_power_added = ?, " +
                "part_description = ?, " +
                "part_cost = ?, " +
                "makes_louder = ? " +
                "WHERE part_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, part.getPartName());
        stmt.setString(2, part.getManufacturer());
        stmt.setString(3, part.getCategory());
        stmt.setInt(4, part.getPartReliability());
        stmt.setInt(5, part.getEstPowerAdded());
        stmt.setString(6, part.getPartDescription());
        stmt.setInt(7, part.getPartCost());
        stmt.setBoolean(8, part.isMakesLouder());
        stmt.setInt(9, part.getPartID());

        stmt.executeUpdate();

        stmt.close();
        conn.close();


    }

    public void deletePart(int id) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "DELETE FROM parts " +
                "WHERE part_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, id);

        stmt.executeUpdate();

        stmt.close();
        conn.close();

    }

    public List<Part> getBuildParts(int buildID) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM parts WHERE build_id = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);
        stmt.setInt(1, buildID);
        List<Part> partList = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            Part part = new Part();
            part.setPartName(rs.getString("part_name"));
            part.setManufacturer(rs.getString("manufacturer"));
            part.setCategory(rs.getString("category"));
            part.setPartReliability(rs.getInt("part_reliability"));
            part.setEstPowerAdded(rs.getInt("est_power_added"));
            part.setPartDescription(rs.getString("part_description"));
            part.setPartCost(rs.getInt("part_cost"));
            part.setMakesLouder(rs.getBoolean("makes_louder"));
            part.setPartID(rs.getInt("part_id"));

            partList.add(part);
        }
        rs.close();
        stmt.close();
        conn.close();
        return partList;
    }
}
