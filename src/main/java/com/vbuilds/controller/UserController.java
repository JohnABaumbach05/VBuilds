package com.vbuilds.controller;

import com.vbuilds.dto.*;
import com.vbuilds.model.User;
import com.vbuilds.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService = new UserService();

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();
        User user = userService.getUserById(userId);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(AccountResponse.fromUser(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UpdateAccountRequest request,
                                               HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();
        try {
            User user = userService.updateAccount(userId, request);
            return ResponseEntity.ok(AccountResponse.fromUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                   HttpSession session) throws SQLException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(Map.of("profilePicture",
                    userService.saveProfilePicture(userId, file)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) throws SQLException {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");
        HttpSession previous = httpRequest.getSession(false);
        if (previous != null) previous.invalidate();
        httpRequest.getSession(true).setAttribute("userId", user.getId());
        return ResponseEntity.ok(new LoginResponse(user.getId(), user.getUsername(),
                user.getEmail(), user.isAdmin()));
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) { session.invalidate(); }

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody CreateAccountRequest request) throws SQLException {
        try {
            userService.addUser(request);
            return ResponseEntity.status(201).body("Account created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
