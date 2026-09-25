package com.vbuilds.controller;

import com.vbuilds.dto.CreateAccountRequest;
import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.model.Users;
import com.vbuilds.model.parts;
import com.vbuilds.service.BuildService;
import com.vbuilds.service.PhotoService;
import com.vbuilds.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private UserService userService;

    public UserController() { userService = new UserService(); }

    @PostMapping
    public void addUser(@RequestBody CreateAccountRequest request) throws SQLException {
        userService.addUser(request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) throws SQLException {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable int id, @RequestBody Users user) throws SQLException {
        user.setUserId(id);
        userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable int id) throws SQLException {
        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public Users getUserByUsername(@PathVariable String username) throws SQLException {
        return userService.getUserByUsername(username);
    }

    @GetMapping
    public List<Users> getAllUsers() throws SQLException {
        return userService.getAllUsers();
    }

}
