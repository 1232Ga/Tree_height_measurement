package com.garudauav.forestrysurvey.models.response_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExportList {
    @SerializedName("JsonResultSet")
    private List<ExportData> exportDataList;

    public ExportList(List<ExportData> exportDataList) {
        this.exportDataList = exportDataList;
    }

    public List<ExportData> getExportDataList() {
        return exportDataList;
    }

    public void setExportDataList(List<ExportData> exportDataList) {
        this.exportDataList = exportDataList;
    }
}
