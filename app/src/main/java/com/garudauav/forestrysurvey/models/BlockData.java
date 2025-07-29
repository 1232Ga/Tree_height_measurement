package com.garudauav.forestrysurvey.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockData {
    @SerializedName("JsonResultSet")
    private List<RFMaster> blockList;
    private int TotalRecord;

    // Getters and Setters
    public List<RFMaster> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<RFMaster> blockList) {
        this.blockList = blockList;
    }

    public int getTotalRecord() {
        return TotalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        TotalRecord = totalRecord;
    }

}
