package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByEmail(String email) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "select * from users where email = ?;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        User user = null;
        if (rs.next()) {
            user = new User();

            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setBio(rs.getString("bio"));
            user.setPFP(rs.getString("profile_picture"));
            user.setDateJoined(rs.getString("date_joined"));
            user.setUserId(rs.getInt("user_id"));
        }
        rs.close();
        stmt.close();
        conn.close();

        return user;
    }


    public void addUser(User user) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "INSERT INTO users(username, email, password_hash) " +
                "VALUES (?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPasswordHash());

        stmt.executeUpdate();

        stmt.close();
        conn.close();

    }

    public void deleteUser(int userId) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "DELETE FROM users WHERE user_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, userId);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void updateUser(User user) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "UPDATE users SET username = ?, email = ?, password_hash = ?, bio = ?, profile_picture = ? WHERE user_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPasswordHash());
        stmt.setString(4, user.getBio());
        stmt.setString(5, user.getPFP());
        stmt.setInt(6, user.getId());

        stmt.executeUpdate();
        stmt.close();
        conn.close();

    }

    public User getUserById(int userId) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM users WHERE user_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setInt(1, userId);


        ResultSet rs = stmt.executeQuery();

        User user = null;

        if (rs.next()) {
            user = new User();

            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setBio(rs.getString("bio"));
            user.setPFP(rs.getString("profile_picture"));
            user.setDateJoined(rs.getString("date_joined"));
            user.setUserId(rs.getInt("user_id"));
        }
        rs.close();
        stmt.close();
        conn.close();

        return user;

    }

    public User getUserByUsername(String username) throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM users WHERE username = ?";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        stmt.setString(1, username);

        ResultSet rs = stmt.executeQuery();

        User user = null;

        if(rs.next()) {
            user = new User();

            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setBio(rs.getString("bio"));
            user.setPFP(rs.getString("profile_picture"));
            user.setDateJoined(rs.getString("date_joined"));
            user.setUserId(rs.getInt("user_id"));
        }
        rs.close();
        stmt.close();
        conn.close();

        return user;

    }

    public List<User> getAllUsers() throws SQLException {
        Connection conn = Database.connect();

        String sq1 = "SELECT * FROM users;";

        PreparedStatement stmt = conn.prepareStatement(sq1);

        ResultSet rs = stmt.executeQuery();

        List<User> userList = new ArrayList<>();

        while (rs.next()) {
            User user = new User();

            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setBio(rs.getString("bio"));
            user.setPFP(rs.getString("profile_picture"));
            user.setDateJoined(rs.getString("date_joined"));
            user.setUserId(rs.getInt("user_id"));

            userList.add(user);
        }
        rs.close();
        stmt.close();
        conn.close();
        return userList;


    }
}
