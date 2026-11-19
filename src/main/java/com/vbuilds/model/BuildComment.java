package com.vbuilds.model;

import java.time.OffsetDateTime;

public record BuildComment(long commentId, String comment, String username, OffsetDateTime createdAt) {}
