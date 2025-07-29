package com.garudauav.forestrysurvey.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "rf_master")
public class RFMaster {


    @PrimaryKey
    @NonNull
    @SerializedName("BlockId")
    public String blockId;
    @SerializedName("BlockName")
    public String blockName;
    @SerializedName("Notes")
    public String notes;
    @SerializedName("BlockKML")
    public String blockKML;
    @SerializedName("IsActive")
    public boolean isActive;
    @SerializedName("ReservForestMasterId")
    public String reservForestMasterID;
    @SerializedName("ReservForestMasterName")
    public String reservForestMasterName;


    public RFMaster(@NonNull String blockId, String blockName, String notes, String blockKML, boolean isActive, String reservForestMasterID, String reservForestMasterName) {
        this.blockId = blockId;
        this.blockName = blockName;
        this.notes = notes;
        this.blockKML = blockKML;
        this.isActive = isActive;
        this.reservForestMasterID = reservForestMasterID;
        this.reservForestMasterName = reservForestMasterName;

    }




 /*   public RFMaster(String  reservForestMasterID, String reservForestMasterName, String notes, boolean isActive) {
        this.reservForestMasterID = reservForestMasterID;
        this.reservForestMasterName = reservForestMasterName;
        this.notes = notes;
        this.isActive = isActive;
    }*/

    @NonNull
    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(@NonNull String blockId) {
        this.blockId = blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockKML() {
        return blockKML;
    }

    public void setBlockKML(String blockKML) {
        this.blockKML = blockKML;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getReservForestMasterID() {
        return reservForestMasterID;
    }

    public void setReservForestMasterID(String reservForestMasterID) {
        this.reservForestMasterID = reservForestMasterID;
    }

    public String getReservForestMasterName() {
        return reservForestMasterName;
    }

    public void setReservForestMasterName(String reservForestMasterName) {
        this.reservForestMasterName = reservForestMasterName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return blockName;
    }
}
