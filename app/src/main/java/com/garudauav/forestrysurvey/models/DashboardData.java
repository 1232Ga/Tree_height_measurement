package com.garudauav.forestrysurvey.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardData {
    @SerializedName("TotalTreeCount")

    private int treeCount;
    @SerializedName("TotalSpecies")
    private int totalSpecies;
    @SerializedName("JsonResultSet")
    private List<SpeciesData> speciesData;

    public DashboardData(int treeCount, int totalSpecies, List<SpeciesData> speciesData) {
        this.treeCount = treeCount;
        this.totalSpecies = totalSpecies;
        this.speciesData = speciesData;
    }

    public int getTreeCount() {
        return treeCount;
    }

    public void setTreeCount(int treeCount) {
        this.treeCount = treeCount;
    }

    public int getTotalSpecies() {
        return totalSpecies;
    }

    public void setTotalSpecies(int totalSpecies) {
        this.totalSpecies = totalSpecies;
    }

    public List<SpeciesData> getSpeciesData() {
        return speciesData;
    }

    public void setSpeciesData(List<SpeciesData> speciesData) {
        this.speciesData = speciesData;
    }
}
