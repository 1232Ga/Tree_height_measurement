package com.garudauav.forestrysurvey.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "district_master")
public class DistrictMaster {
    @PrimaryKey
    @NonNull

    @SerializedName("LookupDataId")
    String districtMasterID;

    @SerializedName("DisplayText")
    String districtName;

    @SerializedName("LookupMasterId")
    String notes;

    @SerializedName("IsActive")
    boolean isActive;

  /*  public DistrictData(String id, String district, String description) {
        this.disrictMasterId = id;
        this.districtName = district;
        this.notes = description;
    }*/

    public DistrictMaster(String districtMasterID, String districtName, String notes, boolean isActive) {
        this.districtMasterID = districtMasterID;
        this.districtName = districtName;
        this.notes = notes;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDistrictMasterID() {
        return districtMasterID;
    }

    public void setDistrictMasterID(String districtMasterID) {
        this.districtMasterID = districtMasterID;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return districtName;
    }
}
