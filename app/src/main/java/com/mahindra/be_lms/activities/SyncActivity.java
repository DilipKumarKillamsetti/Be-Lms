package com.mahindra.be_lms.activities;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncActivity extends BaseActivity implements NetworkMethod {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSyncActivitySync)
    public void btnSyncActivitySync() {
        if (L.isNetworkAvailable(this)) {
            request(Constants.LMS_URL);
        } else {
            L.t(getString(R.string.err_network_connection));
        }
    }

    @Override
    public void request(String url) {
        L.pd(getString(R.string.dialog_please_wait), this);
        List<Document> documentHitList = new DBHelper().documentHitList();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(SyncActivity.this, "RESPONSE : " + response);
                try {
                    updateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(SyncActivity.this, "ERROR : " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", Constants.SAVE_DOCUMENT_HIT_ACTION);
                param.put("userid", new MySharedPreference(SyncActivity.this).getUserId());
                param.put("data", data.toString());

                L.l(SyncActivity.this, "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
            L.t_long("Document Hit save successfully!");
            finish();
        } else {
            L.t(jsonObject.getString("message"));
        }
    }
}
