package com.garudauav.forestrysurvey.models.request_models;

import com.google.gson.annotations.SerializedName;

public class AddSpeciesValue {
    @SerializedName("species_name")
    private String title;
    @SerializedName("notes")
    private String comment;

    @SerializedName("color_code")
    private String colorCode;

    @SerializedName("user_id")
    private String UserId;

    @SerializedName("otp_code")
    private String otpCode;


    public AddSpeciesValue(String title, String comment, String colorCode, String userId, String otpCode) {
        this.title = title;
        this.comment = comment;
        this.colorCode = colorCode;
        UserId = userId;
        this.otpCode = otpCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
