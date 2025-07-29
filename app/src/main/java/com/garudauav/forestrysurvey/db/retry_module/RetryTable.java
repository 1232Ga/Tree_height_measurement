package com.garudauav.forestrysurvey.db.retry_module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

@Entity(tableName = "retry_table")
public class RetryTable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public Double latitude;
    public Double longitude;
    public Float height;
    public int heightType;
    public Integer noOfTree;
    public Float girth;
    public int girthType;
    public String date;
    public String speciesId;
    public String districtId;
    public String blockId;
    public String batchId;
    public int batchCount;


    // Add a byte array field to store Bitmap data
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] photo1;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] photo2;


    public RetryTable() {

    }

    public RetryTable(Double latitude, Double longitude, Float height, int heightType, Integer noOfTree, Float girth, int girthType, String date, String speciesId, String districtId, String rfId, String batchId, int batchCount, Bitmap photo1, Bitmap photo2) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.heightType = heightType;
        this.noOfTree = noOfTree;
        this.girth = girth;
        this.girthType = girthType;
        this.batchCount = batchCount;
        this.date = date;
        this.speciesId = speciesId;
        this.districtId = districtId;
        this.blockId = rfId;
        this.batchId = batchId;

        setPhoto1Bitmap(photo1);
        setPhoto2Bitmap(photo2);
    }

    public int getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(int batchCount) {
        this.batchCount = batchCount;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public int getHeightType() {
        return heightType;
    }

    public void setHeightType(int heightType) {
        this.heightType = heightType;
    }

    public Integer getNoOfTree() {
        return noOfTree;
    }

    public void setNoOfTree(Integer noOfTree) {
        this.noOfTree = noOfTree;
    }

    public Float getGirth() {
        return girth;
    }

    public void setGirth(Float girth) {
        this.girth = girth;
    }

    public int getGirthType() {
        return girthType;
    }

    public void setGirthType(int girthType) {
        this.girthType = girthType;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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
