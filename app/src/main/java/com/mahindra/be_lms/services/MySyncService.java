package com.mahindra.be_lms.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pravin on 12/3/16.
 */

public class MySyncService extends IntentService {
    private static final String TAG = MySyncService.class.getSimpleName();

    public MySyncService() {
        super("sync_hits");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.l("background service call");
        // callSyncDocumentHits();
    }

    private void callSyncDocumentHits() {
        Log.d(TAG, "callSyncDocumentHits: ");
        List<Document> documentHitList = new DBHelper().documentHitList();
        Log.d(TAG, "callSyncDocumentHits: HIT list size: " + documentHitList.size());
        if (documentHitList.size() > 0) {
            final JSONArray data = new JSONArray();
            for (Document document : documentHitList) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("document_tree_id", document.getDocumentTreeID());
                    jsonObject.put("document_name", document.getDocumentName());
                    jsonObject.put("document_hit", document.getDocumentHitCount());
                    data.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String url = Constants.LMS_URL;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.l(TAG, "RESPONSE : " + response);
                    try {
                        updateDisplay(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    L.l(TAG, "ERROR : " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("action", Constants.SAVE_DOCUMENT_HIT_ACTION);
                    param.put("userid", MyApplication.mySharedPreference.getUserId());
                    param.put("data", data.toString());
                    L.l(TAG, "PARAM : " + param.toString());
                    return VolleySingleton.checkRequestparam(param);
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
        }
    }

    private void updateDisplay(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
            MyApplication.mySharedPreference.setHitSyncDate(DateManagement.getCurrentDate());
        }
    }
}
