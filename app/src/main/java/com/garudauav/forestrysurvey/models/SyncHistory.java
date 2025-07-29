package com.garudauav.forestrysurvey.models;

import com.google.gson.annotations.SerializedName;

public class SyncHistory {
    @SerializedName("NumberOfTree")
        private int NumberOfTree;

    @SerializedName("CaptureDate")
    private String DateCreated;
    @SerializedName("QCApproval")
    private int QcApproval;

    private boolean isFailed=false;

    private int treeId;



    public SyncHistory(int numberOfTree, String dateCreated,int flag) {
        NumberOfTree = numberOfTree;
        DateCreated = dateCreated;
        QcApproval =flag;
        isFailed=false;
        treeId=-1;
    }

    public SyncHistory(int numberOfTree, String dateCreated, boolean isFailed,int treeId) {
        NumberOfTree = numberOfTree;
        DateCreated = dateCreated;
        this.isFailed = isFailed;
        this.treeId=treeId;
    }

    public int getQcApproval() {
        return QcApproval;
    }

    public void setQcApproval(int qcApproval) {
        QcApproval = qcApproval;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public int getNumberOfTree() {
        return NumberOfTree;
    }

    public void setNumberOfTree(int numberOfTree) {
        NumberOfTree = numberOfTree;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }
}
