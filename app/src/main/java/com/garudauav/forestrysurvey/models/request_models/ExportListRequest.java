package com.garudauav.forestrysurvey.models.request_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExportListRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("from_date")
    private String fromDate;

    @SerializedName("to_date")
    private String toDate;

    @SerializedName("species")
    private List<String> species;
    @SerializedName("reserve_forest")
    private String reserveForest;

    @SerializedName("district")
    private String district;

    public ExportListRequest(String userId, String fromDate, String toDate, List<String> species, String reserveForest, String district) {
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.species = species;
        this.reserveForest = reserveForest;
        this.district = district;
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

    public List<String> getSpecies() {
        return species;
    }

    public void setSpecies(List<String> species) {
        this.species = species;
    }

    public String getReserveForest() {
        return reserveForest;
    }

    public void setReserveForest(String reserveForest) {
        this.reserveForest = reserveForest;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
