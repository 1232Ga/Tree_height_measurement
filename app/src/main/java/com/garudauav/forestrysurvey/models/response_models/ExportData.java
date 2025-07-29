package com.garudauav.forestrysurvey.models.response_models;

public class ExportData {
    private String SpeciesName;
    private String Coordinates;
    private float Height;
    private int UnitGirth;
    private int NumberOfTree;
    private String ReservForestMasterName;
    private String District;
    private String CapturedBy;
    private String CaptureDate;
    private int UnitHeight;
    private float Girth;
    private String BlockName;

    public ExportData(String speciesName, String coordinates, float height, int unitGirth, int numberOfTree, String reservForestMasterName, String district, String capturedBy, String captureDate, int unitHeight, float girth,String blockName) {
        SpeciesName = speciesName;
        Coordinates = coordinates;
        Height = height;
        UnitGirth = unitGirth;
        NumberOfTree = numberOfTree;
        ReservForestMasterName = reservForestMasterName;
        District = district;
        CapturedBy = capturedBy;
        CaptureDate = captureDate;
        UnitHeight = unitHeight;
        Girth = girth;
        BlockName=blockName;
    }

    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String blockName) {
        BlockName = blockName;
    }

    public String getSpeciesName() {
        return SpeciesName;
    }

    public void setSpeciesName(String speciesName) {
        SpeciesName = speciesName;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String coordinates) {
        Coordinates = coordinates;
    }

    public float getHeight() {
        return Height;
    }

    public void setHeight(float height) {
        Height = height;
    }

    public int getUnitGirth() {
        return UnitGirth;
    }

    public void setUnitGirth(int unitGirth) {
        UnitGirth = unitGirth;
    }

    public int getNumberOfTree() {
        return NumberOfTree;
    }

    public void setNumberOfTree(int numberOfTree) {
        NumberOfTree = numberOfTree;
    }

    public String getReservForestMasterName() {
        return ReservForestMasterName;
    }

    public void setReservForestMasterName(String reservForestMasterName) {
        ReservForestMasterName = reservForestMasterName;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getCapturedBy() {
        return CapturedBy;
    }

    public void setCapturedBy(String capturedBy) {
        CapturedBy = capturedBy;
    }

    public String getCaptureDate() {
        return CaptureDate;
    }

    public void setCaptureDate(String captureDate) {
        CaptureDate = captureDate;
    }

    public int getUnitHeight() {
        return UnitHeight;
    }

    public void setUnitHeight(int unitHeight) {
        UnitHeight = unitHeight;
    }

    public float getGirth() {
        return Girth;
    }

    public void setGirth(float girth) {
        Girth = girth;
    }
}
