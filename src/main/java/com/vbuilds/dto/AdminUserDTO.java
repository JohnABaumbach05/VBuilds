package com.vbuilds.dto;

import com.vbuilds.model.User;

public record AdminUserDTO(
        int id,
        String username,
        String email,
        String dateJoined,
        boolean admin
) {
    public static AdminUserDTO fromUser(User user) {
        return new AdminUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDateJoined(),
                user.isAdmin()
        );
    }
}
