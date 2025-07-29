package com.garudauav.forestrysurvey.models;

import java.util.Date;
import java.util.List;

public class BatchData {
    private String batchId; // Unique batch ID
    private Date date;
    private List<Integer> treeDataIds; // Holds IDs of TreeData in the batch
    private int treeCount;
    private boolean flag;

    public BatchData(String batchId, Date date, List<Integer> treeDataIds, int treeCount, boolean flag) {
        this.batchId = batchId;
        this.date = date;
        this.treeDataIds = treeDataIds;
        this.treeCount = treeCount;
        this.flag = flag;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Integer> getTreeDataIds() {
        return treeDataIds;
    }

    public void setTreeDataIds(List<Integer> treeDataIds) {
        this.treeDataIds = treeDataIds;
    }

    public int getTreeCount() {
        return treeCount;
    }

    public void setTreeCount(int treeCount) {
        this.treeCount = treeCount;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
