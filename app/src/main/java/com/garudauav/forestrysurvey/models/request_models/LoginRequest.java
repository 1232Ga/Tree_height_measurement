package com.garudauav.forestrysurvey.models.request_models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("user_name")
    private String userName;
    @SerializedName("password")
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Parameterized constructor
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getter and setter methods
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
