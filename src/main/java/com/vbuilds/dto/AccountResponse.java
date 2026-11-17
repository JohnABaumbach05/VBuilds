package com.vbuilds.dto;

import com.vbuilds.model.User;

public record AccountResponse(int id, String username, String email,
                              String bio, String profilePicture, boolean admin,
                              String xUrl, String instagramUrl, String tiktokUrl, String snapchatUrl) {
    public static AccountResponse fromUser(User user) {
        return new AccountResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getBio(), user.getPFP(), user.isAdmin(),
                user.getXUrl(), user.getInstagramUrl(), user.getTiktokUrl(), user.getSnapchatUrl());
    }
}
