package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.FeedbackComment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    public List<FeedbackComment> getComments() throws SQLException {
        String sql = """
                SELECT feedback.feedback_id, feedback.comment, feedback.created_at, users.username
                FROM feedback_comments feedback
                LEFT JOIN users ON users.user_id = feedback.author_id
                ORDER BY feedback.feedback_id DESC
                """;
        List<FeedbackComment> comments = new ArrayList<>();

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                comments.add(mapComment(results));
            }
        }

        return comments;
    }

    public FeedbackComment addComment(int userId, String comment) throws SQLException {
        String sql = """
                WITH saved AS (
                    INSERT INTO feedback_comments (author_id, comment) VALUES (?, ?)
                    RETURNING feedback_id, comment, created_at, author_id
                )
                SELECT saved.feedback_id, saved.comment, saved.created_at, users.username
                FROM saved
                LEFT JOIN users ON users.user_id = saved.author_id
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, comment);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapComment(result);
                }
                throw new SQLException("The saved feedback comment could not be retrieved");
            }
        }
    }

    private FeedbackComment mapComment(ResultSet result) throws SQLException {
        return new FeedbackComment(result.getLong("feedback_id"),
                result.getString("comment"),
                result.getString("username"),
                result.getObject("created_at", OffsetDateTime.class));
    }
}
