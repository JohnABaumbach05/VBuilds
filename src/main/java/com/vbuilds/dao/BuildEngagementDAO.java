package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.BuildComment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BuildEngagementDAO {
    public record Likes(long count, boolean liked) {}

    public Likes getLikes(int buildId, Integer userId) throws SQLException {
        String sql = """
                SELECT count(*) AS count, coalesce(bool_or(user_id = ?), false) AS liked
                FROM build_likes WHERE build_id = ?
                """;
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, userId, java.sql.Types.INTEGER);
            statement.setInt(2, buildId);
            try (var result = statement.executeQuery()) {
                result.next();
                return new Likes(result.getLong("count"), result.getBoolean("liked"));
            }
        }
    }

    public void setLiked(int buildId, int userId, boolean liked) throws SQLException {
        String sql = liked
                ? "INSERT INTO build_likes (build_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING"
                : "DELETE FROM build_likes WHERE build_id = ? AND user_id = ?";
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, buildId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public List<BuildComment> getComments(int buildId) throws SQLException {
        String sql = """
                SELECT c.comment_id, c.comment, c.created_at, u.username
                FROM build_comments c LEFT JOIN users u ON u.user_id = c.author_id
                WHERE c.build_id = ? ORDER BY c.comment_id DESC
                """;
        List<BuildComment> comments = new ArrayList<>();
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, buildId);
            try (var result = statement.executeQuery()) {
                while (result.next()) comments.add(mapComment(result));
            }
        }
        return comments;
    }

    public BuildComment addComment(int buildId, int userId, String comment) throws SQLException {
        String sql = """
                WITH saved AS (
                    INSERT INTO build_comments (build_id, author_id, comment) VALUES (?, ?, ?)
                    RETURNING comment_id, author_id, comment, created_at
                )
                SELECT saved.*, u.username FROM saved LEFT JOIN users u ON u.user_id = saved.author_id
                """;
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, buildId);
            statement.setInt(2, userId);
            statement.setString(3, comment);
            try (var result = statement.executeQuery()) {
                if (result.next()) return mapComment(result);
                throw new SQLException("The saved build comment could not be retrieved");
            }
        }
    }

    public record LikedBuild(int buildId, String title) {}
    public record ActivityComment(int buildId, String title, String comment, OffsetDateTime createdAt) {}

    public List<LikedBuild> getLikedBuilds(int userId) throws SQLException {
        String sql = """
                SELECT b.build_id, b.title
                FROM build_likes l JOIN builds b ON b.build_id = l.build_id
                WHERE l.user_id = ? AND b.approved = TRUE
                ORDER BY lower(b.title), b.build_id
                """;
        List<LikedBuild> builds = new ArrayList<>();
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    builds.add(new LikedBuild(result.getInt("build_id"), result.getString("title")));
                }
            }
        }
        return builds;
    }

    public List<ActivityComment> getUserComments(int userId) throws SQLException {
        String sql = """
                SELECT b.build_id, b.title, c.comment, c.created_at
                FROM build_comments c JOIN builds b ON b.build_id = c.build_id
                WHERE c.author_id = ? AND b.approved = TRUE
                ORDER BY c.comment_id DESC
                """;
        List<ActivityComment> comments = new ArrayList<>();
        try (var connection = Database.connect();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    comments.add(new ActivityComment(result.getInt("build_id"), result.getString("title"),
                            result.getString("comment"), result.getObject("created_at", OffsetDateTime.class)));
                }
            }
        }
        return comments;
    }

    private BuildComment mapComment(ResultSet result) throws SQLException {
        return new BuildComment(result.getLong("comment_id"), result.getString("comment"),
                result.getString("username"), result.getObject("created_at", OffsetDateTime.class));
    }
}
