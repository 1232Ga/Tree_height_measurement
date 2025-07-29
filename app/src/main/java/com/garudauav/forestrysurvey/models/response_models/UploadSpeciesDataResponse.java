package com.garudauav.forestrysurvey.models.response_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UploadSpeciesDataResponse {

    @SerializedName("ApiVersion")
    private String apiVersion;

    @SerializedName("Success")
    private boolean success;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private  UploadTreeData data;

    @SerializedName("Errors")
    private List<String> errors;

    public UploadSpeciesDataResponse(String apiVersion, boolean success, String message, UploadTreeData data, List<String> errors) {
        this.apiVersion = apiVersion;
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UploadTreeData getData() {
        return data;
    }

    public void setData(UploadTreeData data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
