package com.garudauav.forestrysurvey.models.response_models;

import com.garudauav.forestrysurvey.models.SyncHistory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncHistoryList {

    @SerializedName("JsonResultSet")
    private List<SyncHistory> syncHistoryList;

    public SyncHistoryList(List<SyncHistory> syncHistoryList) {
        this.syncHistoryList = syncHistoryList;
    }

    public List<SyncHistory> getSyncHistoryList() {
        return syncHistoryList;
    }

    public void setSyncHistoryList(List<SyncHistory> syncHistoryList) {
        this.syncHistoryList = syncHistoryList;
    }
}
