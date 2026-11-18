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
        String sql = "SELECT feedback_id, comment, created_at FROM feedback_comments ORDER BY feedback_id DESC";
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

    public FeedbackComment addComment(String comment) throws SQLException {
        String sql = "INSERT INTO feedback_comments (comment) VALUES (?) RETURNING feedback_id, comment, created_at";

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, comment);

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
                result.getObject("created_at", OffsetDateTime.class));
    }
}
