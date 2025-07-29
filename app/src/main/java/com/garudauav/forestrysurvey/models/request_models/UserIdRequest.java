package com.garudauav.forestrysurvey.models.request_models;

public class UserIdRequest {

    private String user_id;

    public UserIdRequest(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
