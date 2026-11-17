
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

    private Part mapPart(ResultSet rs) throws SQLException {
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

        return part;
    }

    private List<Part> findParts(String sql, Integer parameter) throws SQLException {
        List<Part> parts = new ArrayList<>();

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (parameter != null) {
                stmt.setInt(1, parameter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parts.add(mapPart(rs));
                }
            }
        }

        return parts;
    }

    public List<Part> orderPartsByManufacturer() throws SQLException {
        return findParts("select * from parts order by manufacturer desc", null);
    }

    public List<Part> orderPartsByNameAscending() throws SQLException {
        return findParts("select * from parts order by part_name asc", null);
    }

    public List<Part> orderPartsByNameDescending() throws SQLException {
        return findParts("select * from parts order by part_name desc", null);
    }

    public List<Part> getMostReliableParts() throws SQLException {
        return findParts("select * from parts order by part_reliability desc", null);
    }

    public List<Part> getLeastReliableParts() throws SQLException {
        return findParts("select * from parts order by part_reliability asc", null);
    }

    public List<Part> getLoudParts() throws SQLException {
        return findParts("select * from parts where makes_louder = true", null);
    }


    public List<String> getPartCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM parts ORDER BY category";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    public List<Part> getMostExpensiveParts() throws SQLException {
        return findParts("select * from parts order by part_cost desc", null);
    }

    public List<Part> getLeastExpensiveParts() throws SQLException {
        return findParts("select * from parts order by part_cost asc", null);
    }


    public void addPart(Part part) throws SQLException {
        String sql = "INSERT INTO parts (part_name, manufacturer, category, part_reliability, " +
                "est_power_added, part_description, part_cost, makes_louder) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, part.getPartName());
            stmt.setString(2, part.getManufacturer());
            stmt.setString(3, part.getCategory());
            stmt.setInt(4, part.getPartReliability());
            stmt.setInt(5, part.getEstPowerAdded());
            stmt.setString(6, part.getPartDescription());
            stmt.setInt(7, part.getPartCost());
            stmt.setBoolean(8, part.isMakesLouder());

            stmt.executeUpdate();
        }
    }

    public Part getPartById(int id) throws SQLException {
        String sql = "SELECT * FROM parts WHERE part_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapPart(rs) : null;
            }
        }
    }

    public List<Part> getAllParts() throws SQLException {
        return findParts("SELECT * FROM parts", null);
    }

    public void updatePart(Part part) throws SQLException {
        String sql = "UPDATE parts SET part_name = ?, manufacturer = ?, category = ?, " +
                "part_reliability = ?, est_power_added = ?, part_description = ?, " +
                "part_cost = ?, makes_louder = ? WHERE part_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
    }

    public void deletePart(int id) throws SQLException {
        String sql = "DELETE FROM parts WHERE part_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Part> getBuildParts(int buildID) throws SQLException {
        return findParts("SELECT * FROM parts WHERE build_id = ?", buildID);
    }
}
