package com.garudauav.forestrysurvey.models.response_models;

import com.google.gson.annotations.SerializedName;

public class LoginAuthDataResponse {
    @SerializedName("UserId")
    public String userId;
    @SerializedName("FirstName")
    public String firstName;
    @SerializedName("LastName")
    public String lastName;
    @SerializedName("UserEmail")
    public String userEmail;
    @SerializedName("DateCreated")
    public String dateCreated;
    @SerializedName("DateModified")
    public String dateModified;
    @SerializedName("IsUserSignedUp")
    public boolean isUserSignedUp;
    @SerializedName("IsActive")
    public boolean isActive;
    @SerializedName("OrganizationId")
    public String organizationId;
    @SerializedName("MobileNo")
    public String mobileNo;
    @SerializedName("Notes")
    public String notes;
    @SerializedName("Address")
    public String address;
    @SerializedName("Otp")
    public String otp;
    @SerializedName("IsAppUser")
    public boolean isAppUser;
}
