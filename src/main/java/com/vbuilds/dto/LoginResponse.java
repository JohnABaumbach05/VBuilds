
package com.vbuilds.dto;

public class LoginResponse {
    private int id;
    private String username;
    private String email;
    private boolean admin;

    public LoginResponse(int id, String username,
                         String email, boolean admin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }
}
