package com.garudauav.forestrysurvey.models.response_models;

import com.google.gson.annotations.SerializedName;

public class UploadTreeData {

    @SerializedName("SpeciesRecordId")
    private String speciesRecordId;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("ErrorType")
    private String errorType;

    public UploadTreeData(String speciesRecordId, String userId,String errorType) {
        this.speciesRecordId = speciesRecordId;
        this.userId = userId;
        this.errorType=errorType;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getSpeciesRecordId() {
        return speciesRecordId;
    }

    public void setSpeciesRecordId(String speciesRecordId) {
        this.speciesRecordId = speciesRecordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
