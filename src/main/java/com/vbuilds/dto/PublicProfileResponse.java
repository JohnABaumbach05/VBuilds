package com.vbuilds.dto;

import com.vbuilds.model.User;

public record PublicProfileResponse(int id, String username, String bio, String profilePicture, String dateJoined) {
    public static PublicProfileResponse fromUser(User user) {
        return new PublicProfileResponse(user.getId(), user.getUsername(), user.getBio(),
                user.getPFP(), user.getDateJoined());
    }
}
