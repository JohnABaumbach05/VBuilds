package com.vbuilds.model;

import java.time.OffsetDateTime;

public record FeedbackComment(long id, String comment, String username, OffsetDateTime createdAt) {}
