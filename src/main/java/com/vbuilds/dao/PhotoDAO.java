package com.vbuilds.dao;
import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAO {
    public void addPhoto(Photo newPhoto) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "INSERT INTO photos(build_id, file_path, caption) VALUES (?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, newPhoto.getBuildId());
        stmt.setString(2, newPhoto.getFilePath());
        stmt.setString(3, newPhoto.getCaption());

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public void deletePhoto(int photoId) throws SQLException {
        Connection conn = Database.connect();

        String a = "DELETE FROM photos WHERE photoID = ?;";

        PreparedStatement stmt = conn.prepareStatement(a);

        stmt.setInt(1, photoId);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public void updatePhoto(Photo photo) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE photos SET  file_path = ?, caption = ? WHERE photoID = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, photo.getFilePath());
        stmt.setString(2, photo.getCaption());
        stmt.setInt(3, photo.getPhotoId());

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public Photo getPhotoById(int photoId) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM photos WHERE photoID = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, photoId);

        ResultSet rs = stmt.executeQuery();

        Photo photo = null;

        if(rs.next()) {
            photo = new Photo();

            photo.setPhotoId(rs.getInt("photoID"));
            photo.setBuildId(rs.getInt("build_id"));
            photo.setFilePath(rs.getString("file_path"));
            photo.setCaption(rs.getString("caption"));
        }

        rs.close();
        stmt.close();
        conn.close();

        return photo;

    }

    public List<Photo> getPhotosByBuild(int buildId) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM photos WHERE build_id = ? ORDER BY photoID ASC";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, buildId);

        ResultSet rs = stmt.executeQuery();

        List<Photo> photosList = new ArrayList<>();

        while (rs.next()) {
            Photo photo = new Photo();

            photo.setPhotoId(rs.getInt("photoID"));
            photo.setBuildId(rs.getInt("build_id"));
            photo.setFilePath(rs.getString("file_path"));
            photo.setCaption(rs.getString("caption"));

            photosList.add(photo);
        }
        rs.close();
        stmt.close();
        conn.close();

        return photosList;
    }
}
