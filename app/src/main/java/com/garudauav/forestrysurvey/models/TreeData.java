package com.garudauav.forestrysurvey.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;
import java.util.Date;

@Entity(tableName = "tree_data")
public class TreeData {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public Double latitude;
    public Double longitude;
    public Float height;
    public int heightType;
    public Integer noOfTree;
    public Float girth;
    public int girthType;
    public String species;
    public String rf;
    public String district;
    public Date date;
    public String speciesId;
    public String districtId;
    public String blockId;
    public String userCode;


    public int speciesColor;


    // Add a byte array field to store Bitmap data
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] photo1;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] photo2;


    // Default constructor
    public TreeData() {
        // Default constructor with no parameters
    }

    // Parameterized constructor
    public TreeData(String userCode,Double latitude,Double longitude, Float height,int heightType, Integer noOfTree,
                    Float girth,int girthType, String species, String rf, String district,Date date,int speciesColor, Bitmap photo1, Bitmap photo2,String districtId,String speciesId,String rfId) {

        this.userCode =userCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.noOfTree = noOfTree;
        this.girth = girth;
        this.species = species;
        this.rf = rf;
        this.district = district;
        this.date=date;
        this.girthType=girthType;
        this.heightType=heightType;
        this.speciesColor=speciesColor;
        this.blockId =rfId;
        this.speciesId=speciesId;
        this.districtId=districtId;

        setPhoto1Bitmap(photo1);
        setPhoto2Bitmap(photo2);
    }


    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public int getHeightType() {
        return heightType;
    }

    public void setHeightType(int heightType) {
        this.heightType = heightType;
    }

    public int getGirthType() {
        return girthType;
    }

    public void setGirthType(int girthType) {
        this.girthType = girthType;
    }

    public int getSpeciesColor() {
        return speciesColor;
    }

    public void setSpeciesColor(int speciesColor) {
        this.speciesColor = speciesColor;
    }

    // Getter and setter methods for 'id'
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter methods for 'latitude'
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLatitude() {
        return latitude;
    }

    // Getter and setter methods for 'height'
    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    // Getter and setter methods for 'noOfTree'
    public Integer getNoOfTree() {
        return noOfTree;
    }

    public void setNoOfTree(Integer noOfTree) {
        this.noOfTree = noOfTree;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Getter and setter methods for 'girth'
    public Float getGirth() {
        return girth;
    }

    public void setGirth(Float girth) {
        this.girth = girth;
    }

    // Getter and setter methods for 'species'
    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    // Getter and setter methods for 'rf'
    public String getRf() {
        return rf;
    }

    public void setRf(String rf) {
        this.rf = rf;
    }

    // Getter and setter methods for 'district'
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    // Convert Bitmap to byte array before storing in the database
    public void setPhoto1Bitmap(Bitmap photoBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        photo1 = stream.toByteArray();
    }

    // Convert byte array to Bitmap after retrieving from the database
    public Bitmap getPhoto1Bitmap() {
        if (photo1 != null) {
            return BitmapFactory.decodeByteArray(photo1, 0, photo1.length);
        } else {
            return null;
        }
    }


    // Convert Bitmap to byte array before storing in the database
    public void setPhoto2Bitmap(Bitmap photoBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        photo2 = stream.toByteArray();
    }

    // Convert byte array to Bitmap after retrieving from the database
    public Bitmap getPhoto2Bitmap() {
        if (photo2 != null) {
            return BitmapFactory.decodeByteArray(photo2, 0, photo2.length);
        } else {
            return null;
        }
    }


}
