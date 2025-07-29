package com.garudauav.forestrysurvey.models.request_models;

public class LoginAuthRequest {
    public String user_id;
    public String password;
    public String user_code;

    public LoginAuthRequest(String user_id, String password, String user_code) {
        this.user_id = user_id;
        this.password = password;
        this.user_code = user_code;
    }
}

