package com.garudauav.forestrysurvey.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "species_master")
public class SpeciesMaster {




    @PrimaryKey
    @NonNull
    @SerializedName("SpeciesMasterId")
    private String speciesMasterID;

    @SerializedName("SpeciesName")
    private String speciesName;

    @SerializedName("Notes")
    private String notes;

    @SerializedName("ColorCode")
    private String colorCode;

    @SerializedName("IsActive")
    private boolean isActive;


/*
    public SpeciesMaster(int id, String speciesName, String description, int color) {
        this.speciesMasterID = id;
        this.speciesName = speciesName;
        this.notes = description;
        this.colorCode = color;
    }
*/

    public SpeciesMaster(String speciesMasterID, String speciesName, String notes, String colorCode, boolean isActive) {
        this.speciesMasterID = speciesMasterID;
        this.speciesName = speciesName;
        this.notes = notes;
        this.colorCode = colorCode;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getSpeciesMasterID() {
        return speciesMasterID;
    }

    public void setSpeciesMasterID(String speciesMasterID) {
        this.speciesMasterID = speciesMasterID;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return speciesName;
    }
}
