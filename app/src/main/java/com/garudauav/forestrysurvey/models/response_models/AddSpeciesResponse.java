package com.garudauav.forestrysurvey.models.response_models;

public class AddSpeciesResponse {
    private String message;

    public AddSpeciesResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
