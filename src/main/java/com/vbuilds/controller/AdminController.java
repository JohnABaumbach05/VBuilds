package com.vbuilds.controller;
import com.vbuilds.dao.AdminDAO;
import java.util.Map;
import com.vbuilds.dao.BuildDAO;
import com.vbuilds.model.Build;
import com.vbuilds.model.User;
import com.vbuilds.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BuildDAO buildDAO = new BuildDAO();
    private final UserService userService = new UserService();
    private final AdminDAO adminDAO = new AdminDAO();

    private boolean isAdmin(HttpSession session)
            throws SQLException {

        Integer userId =
                (Integer) session.getAttribute("userId");

        if (userId == null) {
            return false;
        }

        User user = userService.getUserById(userId);

        return user != null && user.isAdmin();
    }

    @GetMapping("/builds/pending")
    public ResponseEntity<?> getPendingBuilds(
            HttpSession session) throws SQLException {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<Build> builds = buildDAO.getPendingBuilds();

        return ResponseEntity.ok(builds);
    }

    @PutMapping("/builds/{id}/approve")
    public ResponseEntity<?> approveBuild(
            @PathVariable int id,
            HttpSession session) throws SQLException {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        buildDAO.approveBuild(id);

        return ResponseEntity.ok("Build approved");
    }

    @PutMapping("/builds/{id}/reject")
    public ResponseEntity<?> rejectBuild(
            @PathVariable int id,
            HttpSession session) throws SQLException {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        buildDAO.rejectBuild(id);

        return ResponseEntity.ok("Build rejected");
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(
            HttpSession session) throws SQLException {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Map<String, Long> stats =
                adminDAO.getDashboardStats();

        return ResponseEntity.ok(stats);
    }

}