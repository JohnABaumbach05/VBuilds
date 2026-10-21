package com.vbuilds.model;

public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String dateJoined;
    private String bio;
    private String profile_picture;
    private boolean isAdmin;


    public User(){}

    public User(int id, String username, String email, String password_hash,
                String date_joined, String bio, String profile_picture)
    {
        this.userId = id; this.username = username; this.email = email; this.passwordHash = password_hash;
        this.dateJoined = date_joined; this.bio = bio; this.profile_picture = profile_picture;
    }

    // getters


    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getId() { return userId; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public String getDateJoined() { return dateJoined; }

    public String getBio() { return bio; }

    public String getPFP() { return profile_picture; }

    // setters
    public void setUsername(String newUser) { this.username = newUser; }

    public void setEmail(String newEmail) { this.email = newEmail; }

    public void setPasswordHash(String newPassword) { this.passwordHash = newPassword; }

    public void setBio(String newBio) { this.bio = newBio; }

    public void setPFP(String newPFP) { this.profile_picture = newPFP; }

    public void setDateJoined(String dateJoined) { this.dateJoined = dateJoined; }

    public void setUserId(int id) { this.userId = id; }
}
