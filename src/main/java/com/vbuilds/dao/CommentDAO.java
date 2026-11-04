package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public List<Comment> getCommentsByBuildId(int buildId)
            throws SQLException {

        List<Comment> comments = new ArrayList<>();

        String sql = """
            SELECT c.*, u.username
            FROM comments c
            JOIN users u ON c.user_id = u.user_id
            WHERE c.build_id = ?
            ORDER BY c.created_at ASC, c.comment_id ASC
            """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, buildId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }
        }

        return comments;
    }

    public Comment addComment(int buildId, int userId,
                              String content, Integer parentCommentId)
            throws SQLException {

        String sql = """
            INSERT INTO comments
                (build_id, user_id, content, parent_comment_id)
            VALUES (?, ?, ?, ?)
            RETURNING comment_id, created_at
            """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, buildId);
            stmt.setInt(2, userId);
            stmt.setString(3, content);

            if (parentCommentId == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, parentCommentId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Comment comment = new Comment();

                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setBuildId(buildId);
                    comment.setUserId(userId);
                    comment.setContent(content);
                    comment.setParentCommentId(parentCommentId);
                    comment.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    return comment;
                }
            }
        }

        throw new SQLException("Failed to insert comment.");
    }

    public boolean deleteComment(int commentId)
            throws SQLException {

        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);

            return stmt.executeUpdate() > 0;
        }
    }

    private Comment mapComment(ResultSet rs)
            throws SQLException {

        Comment comment = new Comment();

        comment.setCommentId(rs.getInt("comment_id"));
        comment.setBuildId(rs.getInt("build_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setUsername(rs.getString("username"));
        comment.setContent(rs.getString("content"));

        int parentId = rs.getInt("parent_comment_id");

        comment.setParentCommentId(
                rs.wasNull() ? null : parentId
        );

        comment.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        return comment;
    }
}