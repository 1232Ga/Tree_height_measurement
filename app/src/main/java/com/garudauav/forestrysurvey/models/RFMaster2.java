package com.garudauav.forestrysurvey.models;

import java.util.Objects;

public class RFMaster2 {
    private String reservForestMasterID;
    private String reservForestMasterName;

    public RFMaster2(String reservForestMasterID, String reservForestMasterName) {
        this.reservForestMasterID = reservForestMasterID;
        this.reservForestMasterName = reservForestMasterName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RFMaster2 rfMaster2 = (RFMaster2) o;
        return Objects.equals(reservForestMasterID, rfMaster2.reservForestMasterID) &&
                Objects.equals(reservForestMasterName, rfMaster2.reservForestMasterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservForestMasterID, reservForestMasterName);
    }

    @Override
    public String toString() {
        return reservForestMasterName;
    }
}
