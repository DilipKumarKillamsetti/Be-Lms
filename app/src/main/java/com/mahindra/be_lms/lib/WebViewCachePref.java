package com.mahindra.be_lms.lib;

import android.content.Context;
import android.content.SharedPreferences;

import com.mahindra.be_lms.R;

/**
 * Created by Android on 2/2/2017.
 */

public class WebViewCachePref {
    private static WebViewCachePref webViewCachePref;
    private final SharedPreferences preferences;

    private WebViewCachePref(Context context) {
        preferences = context.getSharedPreferences(context.getResources().getString(R.string.webpage_cache_pref), Context.MODE_PRIVATE);
    }

    public static WebViewCachePref newInstance(Context context) {
        if (webViewCachePref == null) {
            webViewCachePref = new WebViewCachePref(context);
        }
        return webViewCachePref;
    }

    public void putPref(String key, String val) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public String getPref(String key) {
        return preferences.getString(key, null);
    }

    public void clearAllPref() {
        preferences.edit().clear().apply();
    }
}
