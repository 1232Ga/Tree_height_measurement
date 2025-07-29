package com.garudauav.forestrysurvey.models.request_models;

import com.google.gson.annotations.SerializedName;

public class TreeDataRequest {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("from_date")

    private String fromDate;
    @SerializedName("to_date")
    private String toDate;

    public TreeDataRequest(String userId) {
        this.userId = userId;
    }

    public TreeDataRequest(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
