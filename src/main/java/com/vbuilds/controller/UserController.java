package com.vbuilds.controller;
import jakarta.servlet.http.HttpSession;
import com.vbuilds.dto.LoginRequest;
import com.vbuilds.dto.CreateAccountRequest;
import com.vbuilds.model.User;
import com.vbuilds.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vbuilds.dto.LoginResponse;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/users")



public class UserController {

    private UserService userService;

    public UserController() { userService = new UserService(); }



    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public LoginResponse getCurrentUser(HttpSession session) throws SQLException {
    Integer userId = (Integer) session.getAttribute("userId");

    if (userId == null) {
        return null;
    }

    User user = userService.getUserById(userId);

    return new LoginResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) throws SQLException {
        User user = userService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (user == null) {
            return null;
        }

        session.setAttribute("userId", user.getId());

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

    }



    @PostMapping
    public ResponseEntity<String> addUser(
            @RequestBody CreateAccountRequest request) throws SQLException {

        try {
            userService.addUser(request);
            return ResponseEntity.status(201).body("Account created successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) throws SQLException {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable int id, @RequestBody User user) throws SQLException {
        user.setUserId(id);
        userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws SQLException {
        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) throws SQLException {
        return userService.getUserByUsername(username);
    }

    @GetMapping
    public List<User> getAllUsers() throws SQLException {
        return userService.getAllUsers();
    }

}
