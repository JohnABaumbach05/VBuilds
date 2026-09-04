package com.vbuilds.model;

import org.apache.catalina.User;

public class Users {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String dateJoined;
    private String bio;
    private String profile_picture;


    public Users(){}

    public Users(int id, String username, String email, String password_hash,
                 String date_joined, String bio, String profile_picture)
    {
        this.userId = id; this.username = username; this.email = email; this.passwordHash = password_hash;
        this.dateJoined = date_joined; this.bio = bio; this.profile_picture = profile_picture;
    }

    // getters
    public int getId() { return userId; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getPassword() { return passwordHash; }

    public String getDateJoined() { return dateJoined; }

    public String getBio() { return bio; }

    public String getPFP() { return profile_picture; }

    // setters
    public void setUsername(String newUser) { this.username = newUser; }

    public void setEmail(String newEmail) { this.email = newEmail; }

    public void setPassword(String newPassword) { this.passwordHash = newPassword; }

    public void setBio(String newBio) { this.bio = newBio; }

    public void setPFP(String newPFP) { this.profile_picture = newPFP; }

    public void setDateJoined(String dateJoined) { this.dateJoined = dateJoined; }

    public void setUserId(int id) { this.userId = id; }
}
