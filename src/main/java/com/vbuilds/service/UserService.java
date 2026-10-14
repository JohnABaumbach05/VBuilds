package com.vbuilds.service;
import java.time.LocalDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.UserDAO;
import com.vbuilds.dto.CreateAccountRequest;
import com.vbuilds.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private BuildDAO buildDAO;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService() {
        userDAO = new UserDAO();
        buildDAO = new BuildDAO();
        passwordEncoder = new BCryptPasswordEncoder();
    }


    public User getUserByEmail(String email) throws SQLException{
        return userDAO.getUserByEmail(email);
    }

    public User login(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }



    public void addUser(CreateAccountRequest request) throws SQLException {

        if (userDAO.getUserByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateJoined(LocalDate.now().toString());


        // PASSWORD
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        user.setPasswordHash(hashedPassword);

        userDAO.addUser(user);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    public User getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    public User getUserByUsername(String user) throws SQLException {
        return userDAO.getUserByUsername(user);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }
}
