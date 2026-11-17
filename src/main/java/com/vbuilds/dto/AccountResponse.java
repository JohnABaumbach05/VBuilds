package com.vbuilds.dto;

import com.vbuilds.model.User;

public record AccountResponse(int id, String username, String email,
                              String bio, String profilePicture, boolean admin) {
    public static AccountResponse fromUser(User user) {
        return new AccountResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getBio(), user.getPFP(), user.isAdmin());
    }
}
