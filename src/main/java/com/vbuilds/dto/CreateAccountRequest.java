package com.vbuilds.dto;

public class CreateAccountRequest {

    private String username;
    private String email;
    private String password;

    public String getUsername() {return username;}

    public String getEmail() { return email;}

    public String getPassword() { return password;}

    public void setUsername(String newName) { this.username = newName;}

    public void setEmail(String newMail) { this.email = newMail;}

    public void setPassword(String newPassword) {this.password = newPassword;}


}
