package com.garudauav.forestrysurvey.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Nectar_Forestry";
    private static final String IS_USER_LOGIN = "isUserLogin";
    private static final String TREE_CAPTURED_COUNT = "treeCapturedCount";
    private static final String TOTAL_SPECIES_COUNT = "totalSpeciesCount";
    private static final String USER_NAME = "userName";
    private static final String USER_ID = "userId";
    private static final String USER_CODE = "userCode";
    private static final String LOGIN_WITH = "loginWith";
    private static final String FRAGMENT_VISIBLE = "fragmentVisible";
    private static final String SPECIES_COUNT = "speciesCount";
    private static final String DESTRICT_COUNT = "destrictCount";
    private static final String RF_COUNT = "rfCount";
    private static final String AFTER6AM = "after6AM";
    private static final String AFTER6PM = "after6pm";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SPECIES_MASTER_SIZE = "speciesMasterSize";
    private static final String HEIGHT_SCREEN_FIRST_TIME = "heightScreenFirstTime";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_TERMS_AND_CONDITIONS_AGREED = "IsTermsAndConditionsAgreed";


    public PrefManager(Context context) {
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }



    public void clearPreferences() {


        int preservedValue = pref.getInt(SPECIES_MASTER_SIZE, 0); // Adjust the default value as needed
        boolean preserveFirstTime = pref.getBoolean(HEIGHT_SCREEN_FIRST_TIME, true);
        boolean preserveAppFirstTime = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
        boolean preserveTermsConditions = pref.getBoolean(IS_TERMS_AND_CONDITIONS_AGREED, true);

        editor.clear().commit(); // Clear all data

        // Put back the preserved value
        if (preservedValue != 0) {
            editor.putInt(SPECIES_MASTER_SIZE, preservedValue);
            editor.putBoolean(HEIGHT_SCREEN_FIRST_TIME, preserveFirstTime);
            editor.putBoolean(IS_TERMS_AND_CONDITIONS_AGREED, preserveTermsConditions);
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, preserveAppFirstTime);
            editor.commit();
        }
    }
    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {

        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);

        editor.commit();

    }  public boolean isTermsAndConditionsAgreed() {

        return pref.getBoolean(IS_TERMS_AND_CONDITIONS_AGREED, false);

    }

    public void setTermsAndConditionsAgreed(boolean isFirstTime) {

        editor.putBoolean(IS_TERMS_AND_CONDITIONS_AGREED, isFirstTime);

        editor.commit();

    }

    public boolean isUserLogin() {
        return pref.getBoolean(IS_USER_LOGIN, false);

    }

    public void setUserLogin(boolean isUserLogin) {
        editor.putBoolean(IS_USER_LOGIN, isUserLogin);
        editor.commit();
    }

    public boolean isHeightFirstTime() {
        return pref.getBoolean(HEIGHT_SCREEN_FIRST_TIME, true);

    }

    public void setHeightScreenFirstTime(boolean isHeightFirstTime) {
        editor.putBoolean(HEIGHT_SCREEN_FIRST_TIME, isHeightFirstTime);
        editor.commit();
    }

    public boolean isAfter6AM() {
        return pref.getBoolean(AFTER6AM, false);

    }

    public void setAfter6AM(boolean after6am) {
        editor.putBoolean(AFTER6AM, after6am);
        editor.commit();
    }

    public boolean isAfter6PM() {
        return pref.getBoolean(AFTER6PM, false);

    }

    public void setAfter6PM(boolean after6PM) {
        editor.putBoolean(AFTER6PM, after6PM);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");

    }

    public String getUserId() {
        return pref.getString(USER_ID, "00000000-0000-0000-0000-000000000000");

    }

    public String getLoginWith() {
        return pref.getString(LOGIN_WITH, "");

    }

    public void setLoginWith(String loginWith) {
        editor.putString(LOGIN_WITH, loginWith);
        editor.commit();
    }

    public String getLatiude() {
        return pref.getString(LATITUDE, "");

    }

    public void setLatitude(String latitude) {
        editor.putString(LATITUDE, latitude);
        editor.commit();
    }

    public String getLongitude() {
        return pref.getString(LONGITUDE, "");

    }

    public void setLongitude(String longitude) {
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }

    public String getFragmentVisible() {
        return pref.getString(FRAGMENT_VISIBLE, "All Data");

    }

    public void setFragmentVisible(String fragmentVisible) {
        editor.putString(FRAGMENT_VISIBLE, fragmentVisible);
        editor.commit();
    }


    public void setSpeciesMasterCount(int speciesMasterCount) {
        editor.putInt(SPECIES_MASTER_SIZE, speciesMasterCount);
        editor.commit();
    }

    public int getSpeciesMasterCount() {
        return pref.getInt(SPECIES_MASTER_SIZE, 0);

    }

    public void setTreeCapturedCount(int treeCapturedCount) {
        editor.putInt(TREE_CAPTURED_COUNT, treeCapturedCount);
        editor.commit();
    }

    public int getTreeCapturedCount() {
        return pref.getInt(TREE_CAPTURED_COUNT, 0);

    }

    public void setSpeciesCount(int speciesCount) {
        editor.putInt(SPECIES_COUNT, speciesCount);
        editor.commit();
    }

    public int getSpeciesCount() {
        return pref.getInt(SPECIES_COUNT, 0);

    }

    public void setDestrictCount(int destrictCount) {
        editor.putInt(DESTRICT_COUNT, destrictCount);
        editor.commit();
    }

    public int getDestrictCount() {
        return pref.getInt(DESTRICT_COUNT, 0);

    }

    public void setRfCount(int rfCount) {
        editor.putInt(RF_COUNT, rfCount);
        editor.commit();
    }

    public int getRfCount() {
        return pref.getInt(RF_COUNT, 0);

    }


    public void setTotalSpeciesCount(int speciesCount) {
        editor.putInt(TOTAL_SPECIES_COUNT, speciesCount);
        editor.commit();
    }

    public int getTotalSpeciesCount() {
        return pref.getInt(TOTAL_SPECIES_COUNT, 0);

    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getUserCode() {
        return pref.getString(USER_CODE, "");

    }

    public void setUserCode(String userCode) {
        editor.putString(USER_CODE, userCode);
        editor.commit();
    }

}
