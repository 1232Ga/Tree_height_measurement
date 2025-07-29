package com.garudauav.forestrysurvey.models;

import java.util.Date;
import java.util.List;

public class SyncData {
    private List<Integer> id;
    private Date date;
    private int imageCount;
    private boolean isChecked;

    public SyncData(List<Integer> id, Date date, int imageCount, boolean isChecked) {
        this.id = id;
        this.date = date;
        this.imageCount = imageCount;
        this.isChecked = isChecked;
    }

    public List<Integer> getId() {
        return id;
    }

    public void setId(List<Integer> id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
