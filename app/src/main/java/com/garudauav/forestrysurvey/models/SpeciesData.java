package com.garudauav.forestrysurvey.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "species_data")
public class SpeciesData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("SpeciesName")
    private String speciesName;

    @SerializedName("NumberOfTree")
    private int NumberOfTree;
    @SerializedName("ColorCode")
    private String colorCode;

    public SpeciesData(String speciesName, int NumberOfTree, String colorCode) {
        this.speciesName = speciesName;
        this.NumberOfTree = NumberOfTree;
        this.colorCode = colorCode;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public int getNumberOfTree() {
        return NumberOfTree;
    }

    public void setNumberOfTree(int numberOfTree) {
        this.NumberOfTree = numberOfTree;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
