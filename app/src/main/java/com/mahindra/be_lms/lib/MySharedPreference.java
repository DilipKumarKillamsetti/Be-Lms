package com.mahindra.be_lms.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mahindra.be_lms.R;
import com.securepreferences.SecurePreferences;

/**
 * Created by Pravin .1-Oct-2016
 */
public class MySharedPreference {
    public static final String IS_LOGIN = "is_login";
    public static final String MASTER_SYNC_DATE = "master_sync_date";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String UNIQUEID = "uniqueid";
    public static final String USER_ROLE = "user_role";
    public static final String PRODUCT_HITS = "product-hits";
    public static final String MODEL_HITS = "model_hits";
    public static final String DOCUMENT_HITS = "document_hits";
    public static final String DOCUMENT_TYPE_HITS = "document_type_hits";
    public static final String FLAG_PROFILE_IMG_SAVE = "flag_profile_img_save";
    public static final String HIT_SYNC_DATE = "sync_date";
    public static final String DISCLAIMER_ACCEPT = "disclaimer_accept";
    public static final String SAFETY_ACCEPT = "safety_accept";
    public static final String NOTIFICATION_DIALOG_TITLE = "notification_title";
    public static final String NOTIFICATION_DIALOG_MSG = "notification_msg";
    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String QUERYID = "queryID";
    public static final String SLIDER_SYNC_DATE = "slider_sync_date";
    public static final String USER_TOKEN = "token";
    private final SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public MySharedPreference(Context context) {
        preferences = new SecurePreferences(context, context.getResources().getString(R.string.secure_password), context.getString(R.string.app_name));
        //preferences = context.getSharedPreferences(context.getString(R.string.app_name), 0);
        editor = preferences.edit();
    }

    /*
    put any type of value in sharePreference
    */
    public void putPref(String key, Object val) {
        SharedPreferences.Editor editor = preferences.edit();
        if (val instanceof Boolean)
            editor.putBoolean(key, (Boolean) val);
        else if (val instanceof Float)
            editor.putFloat(key, (Float) val);
        else if (val instanceof Integer)
            editor.putInt(key, (Integer) val);
        else if (val instanceof Long)
            editor.putLong(key, (Long) val);
        else if (val instanceof String)
            editor.putString(key, ((String) val));
        editor.apply();
    }

    /*
    get string type of value in sharePreference
    */
    public String getPrefisString(String key) {
        return preferences.getString(key, null);
    }

    /*
    get boolean type of value in sharePreference
    */
    public boolean getPrefisBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    /*
        get int type of value in sharePreference
        */
    public int getPrefisInt(String key) {
        return preferences.getInt(key, 0);
    }

    /*
    get float type of value in sharePreference
    */
    public float getPrefisFloat(String key) {
        return preferences.getFloat(key, 0.0f);
    }

    /*
    get long type of value in sharePreference
    */
    public long getPrefisLong(String key) {
        return preferences.getLong(key, 0);
    }

    public void clearSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * check key is exist or not in preference
     * key is exist return true
     * key is not exist return false
     */
    public boolean checkPrefs(String key) {
        return preferences.contains(key);
    }

    public boolean checkUserLogin() {
        return getPrefisBoolean(IS_LOGIN);
    }

    public void saveUser(String id, String name, String role) {
        putPref(USER_ID, id);
        putPref(USER_NAME, name);
        putPref(USER_ROLE, role);
    }

    public String getUserId() {
        return getPrefisString(USER_ID);
    }

    public String getUserRole() {
        return getPrefisString(USER_ROLE);
    }

    public String getUniqueID() {
        return getPrefisString(UNIQUEID);
    }

    public void setUniqueID(String uniqueID) {
        putPref(UNIQUEID, uniqueID);
    }

    public void setUserLogin(boolean isLogin) {
        putPref(IS_LOGIN, isLogin);
    }



    public void logOut() {
        removePref(USER_ID);
        removePref(USER_NAME);
        removePref(USER_ROLE);
    }

    public boolean getDisclaimerACCEPT() {
        L.l("Disclaimer Accepted: " + preferences.getBoolean(DISCLAIMER_ACCEPT, false));
        return getPrefisBoolean(DISCLAIMER_ACCEPT);
    }

    public String getUserToken() {
        return getPrefisString(USER_TOKEN);
    }

    public String getUserName() {
        return getPrefisString(USER_NAME);
    }
    public boolean getSAFETYACCEPT() {
        L.l("SAFETY Accept: " + preferences.getBoolean(SAFETY_ACCEPT, false));
        return getPrefisBoolean(SAFETY_ACCEPT);
    }

    public String getFcmToken() {
        L.l("FCM TOKEN: " + getPrefisString(FCM_TOKEN));
        return L.checkNull(getPrefisString(FCM_TOKEN)) ? FirebaseInstanceId.getInstance().getToken() : getPrefisString(FCM_TOKEN);
    }

    public void setFcmToken(String fcmToken) {
        putPref(FCM_TOKEN, fcmToken);
    }

    public void setProductHits() {
        putPref(PRODUCT_HITS, getPrefisInt(PRODUCT_HITS) + 1);
    }

    public void setModelHits() {
        putPref(MODEL_HITS, getPrefisInt(MODEL_HITS) + 1);
    }

    public void setDocumentHits() {
        putPref(DOCUMENT_HITS, getPrefisInt(DOCUMENT_HITS) + 1);
    }

    public void setDocumentTypeHits() {
        putPref(DOCUMENT_TYPE_HITS, getPrefisInt(DOCUMENT_TYPE_HITS) + 1);
    }

    public void removePref(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public boolean getFlagProfilePicSave() {
        return preferences.getBoolean(FLAG_PROFILE_IMG_SAVE, false);
    }

    public void setFlagProfilePicSave(boolean flag) {
        editor.putBoolean(FLAG_PROFILE_IMG_SAVE, flag);
        editor.apply();
        L.l("SharedPreference flag: " + preferences.getBoolean(FLAG_PROFILE_IMG_SAVE, false));
    }

    public String getHitSyncDate() {
        return getPrefisString(HIT_SYNC_DATE);
    }

    public void setHitSyncDate(String hitSyncDate) {
        if (!TextUtils.isEmpty(hitSyncDate)) {
            putPref(HIT_SYNC_DATE, hitSyncDate);
        }
    }

    public String getDialogTitle() {
        return preferences.getString(NOTIFICATION_DIALOG_TITLE, "");
    }

    public void setDialogTitle(String title) {
        editor.putString(NOTIFICATION_DIALOG_TITLE, title);
        editor.apply();
    }

    public String getDialogMessage() {
        return preferences.getString(NOTIFICATION_DIALOG_MSG, "");
    }

    public void setDialogMessage(String msg) {
        editor.putString(NOTIFICATION_DIALOG_MSG, msg);
        editor.apply();
    }

    public String getNotificationType() {
        return preferences.getString(NOTIFICATION_TYPE, "");
    }

    public void setNotificationType(String norificationType) {
        editor.putString(NOTIFICATION_TYPE, norificationType);
        editor.apply();
    }

    public String getQueryID() {
        return preferences.getString(QUERYID, "");
    }

    public void setQueryID(String queryId) {
        editor.putString(QUERYID, queryId);
        editor.apply();
    }

    public String getSliderSyncDate() {
        return preferences.getString(SLIDER_SYNC_DATE, "10-10-2010");
    }

    public void setSliderSyncDate(String sliderSyncDate) {
        editor.putString(SLIDER_SYNC_DATE, sliderSyncDate);
        editor.apply();
    }
}
