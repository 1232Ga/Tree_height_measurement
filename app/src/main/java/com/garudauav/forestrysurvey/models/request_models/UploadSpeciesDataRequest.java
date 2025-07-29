package com.garudauav.forestrysurvey.models.request_models;

import com.google.gson.annotations.SerializedName;

public class UploadSpeciesDataRequest {

    @SerializedName("name_of_species")
    private String nameOfSpecies;

    @SerializedName("coordinates")
    private String coordinates;

    @SerializedName("height")
    private double height;

    @SerializedName("captured_date")
    private String capturedDate;

    @SerializedName("unit_height")
    private int unitHeight;

    @SerializedName("number_of_tree")
    private int numberOfTree;

    @SerializedName("girth")
    private double girth;

    @SerializedName("unit_girth")
    private int unitGirth;

    @SerializedName("name_of_RF")
    private String nameOfRF;

    @SerializedName("name_of_district")
    private String nameOfDistrict;

    public UploadSpeciesDataRequest(String nameOfSpecies, String coordinates, double height, String capturedDate, int unitHeight, int numberOfTree, double girth, int unitGirth, String nameOfRF, String nameOfDistrict) {
        this.nameOfSpecies = nameOfSpecies;
        this.coordinates = coordinates;
        this.height = height;
        this.capturedDate = capturedDate;
        this.unitHeight = unitHeight;
        this.numberOfTree = numberOfTree;
        this.girth = girth;
        this.unitGirth = unitGirth;
        this.nameOfRF = nameOfRF;
        this.nameOfDistrict = nameOfDistrict;
    }


}
