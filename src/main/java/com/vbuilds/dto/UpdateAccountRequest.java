package com.vbuilds.dto;

public class UpdateAccountRequest {
    private String username;
    private String email;
    private String bio;
    private String currentPassword;
    private String newPassword;

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setUsername(String value) { username = value; }
    public void setEmail(String value) { email = value; }
    public void setBio(String value) { bio = value; }
    public void setCurrentPassword(String value) { currentPassword = value; }
    public void setNewPassword(String value) { newPassword = value; }
}
