package com.vbuilds.service;
import java.time.LocalDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.UserDAO;
import com.vbuilds.dto.CreateAccountRequest;
import com.vbuilds.model.Users;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private BuildDAO buildDAO;

    public UserService() {
        userDAO = new UserDAO();
        buildDAO = new BuildDAO();
    }


    public void addUser(CreateAccountRequest request) throws SQLException {
        Users user = new Users();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateJoined(LocalDate.now().toString());


        // PASSWORD
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hashedPassword = encoder.encode(request.getPassword());

        user.setPasswordHash(hashedPassword);

        userDAO.addUser(user);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }

    public void updateUser(Users user) throws SQLException {
        userDAO.updateUser(user);
    }

    public Users getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    public Users getUserByUsername(String user) throws SQLException {
        return userDAO.getUserByUsername(user);
    }

    public List<Users> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }
}
