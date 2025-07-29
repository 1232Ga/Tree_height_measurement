package com.garudauav.forestrysurvey.models;

import com.garudauav.forestrysurvey.models.response_models.LoginAuthDataResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginAuthRootResponse {

    @SerializedName("ApiVersion")
    public String apiVersion;
    @SerializedName("Success")
    public boolean success;
    @SerializedName("Data")
    public LoginAuthDataResponse data;
    @SerializedName("Errors")
    public ArrayList<Object> errors;
}