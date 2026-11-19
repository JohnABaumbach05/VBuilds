package com.vbuilds.controller;

import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.BuildEngagementDAO;
import com.vbuilds.dao.BuildEngagementDAO.Likes;
import com.vbuilds.model.BuildComment;
import com.vbuilds.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/builds")
public class BuildEngagementController {
    private final BuildDAO buildDAO = new BuildDAO();
    private final BuildEngagementDAO engagementDAO = new BuildEngagementDAO();
    private final UserService userService = new UserService();

    public record Engagement(Likes likes, List<BuildComment> comments, boolean signedIn) {}
    public record CommentRequest(String comment) {}

    @GetMapping("/{buildId}/engagement")
    public Engagement getEngagement(@PathVariable int buildId, HttpServletRequest request) throws SQLException {
        requirePublishedBuild(buildId);
        Integer userId = getUserId(request);
        return new Engagement(engagementDAO.getLikes(buildId, userId), engagementDAO.getComments(buildId),
                userId != null);
    }

    public record Activity(List<BuildEngagementDAO.LikedBuild> likedBuilds,
                           List<BuildEngagementDAO.ActivityComment> comments) {}

    @GetMapping("/mine/activity")
    public Activity getMyActivity(HttpServletRequest request) throws SQLException {
        int userId = requireUserId(request);
        return new Activity(engagementDAO.getLikedBuilds(userId), engagementDAO.getUserComments(userId));
    }

    @PutMapping("/{buildId}/engagement/like")
    public Likes like(@PathVariable int buildId, HttpServletRequest request) throws SQLException {
        return setLiked(buildId, request, true);
    }

    @DeleteMapping("/{buildId}/engagement/like")
    public Likes unlike(@PathVariable int buildId, HttpServletRequest request) throws SQLException {
        return setLiked(buildId, request, false);
    }

    @PostMapping(path = "/{buildId}/engagement/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BuildComment addComment(@PathVariable int buildId, @RequestBody CommentRequest body,
                                   HttpServletRequest request) throws SQLException {
        int userId = requireUserId(request);
        requirePublishedBuild(buildId);
        String comment = body.comment() == null ? "" : body.comment().strip();
        if (comment.isBlank() || comment.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a comment of 1 to 1000 characters");
        }
        return engagementDAO.addComment(buildId, userId, comment);
    }

    private Likes setLiked(int buildId, HttpServletRequest request, boolean liked) throws SQLException {
        int userId = requireUserId(request);
        requirePublishedBuild(buildId);
        engagementDAO.setLiked(buildId, userId, liked);
        return engagementDAO.getLikes(buildId, userId);
    }

    private void requirePublishedBuild(int buildId) throws SQLException {
        if (buildDAO.getBuildById(buildId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found");
        }
    }

    private Integer getUserId(HttpServletRequest request) throws SQLException {
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute("userId");
        return userId != null && userService.getUserById(userId) != null ? userId : null;
    }

    private int requireUserId(HttpServletRequest request) throws SQLException {
        Integer userId = getUserId(request);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to interact with this build");
        }
        return userId;
    }
}
