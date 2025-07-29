package com.garudauav.forestrysurvey.models.response_models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    // Default constructor
    public LoginResponse() {
    }

    // Parameterized constructor
    public LoginResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

