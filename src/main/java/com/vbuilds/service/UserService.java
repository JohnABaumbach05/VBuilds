package com.vbuilds.service;

import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.UserDAO;
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


    public void addUser(Users user) throws SQLException {
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
