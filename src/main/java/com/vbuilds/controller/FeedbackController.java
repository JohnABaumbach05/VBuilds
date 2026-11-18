package com.vbuilds.controller;

import com.vbuilds.dao.FeedbackDAO;
import com.vbuilds.model.FeedbackComment;
import com.vbuilds.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final UserService userService = new UserService();

    @GetMapping
    public List<FeedbackComment> getComments() throws SQLException {
        return feedbackDAO.getComments();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackComment addComment(@RequestBody CreateFeedbackRequest request, HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || userService.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to post feedback");
        }

        String comment = request.comment() == null ? "" : request.comment().strip();
        if (comment.isBlank() || comment.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a comment of 1 to 1000 characters");
        }
        return feedbackDAO.addComment(userId, comment);
    }

    public record CreateFeedbackRequest(String comment) {}
}
