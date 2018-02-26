package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncMasterActivity extends BaseActivity {

    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.tvSyncMasterMsg)
    TextView tvSyncMasterMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_master);
        ButterKnife.bind(this);
        L.l(this, "SYNC DATE: " + MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE));
        L.l(this, "CURRENT DATE: " + DateManagement.getCurrentDate());
        if (MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE) == null || !MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE).equals(DateManagement.getCurrentDate())) {
            syncMaster();
        } else {
            checkUserLogin();
        }
    }

    private void syncMaster() {
        if (L.isNetworkAvailable(this)) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
            }
            request(Constants.BE_LMS_POSITION_URL);
        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncMaster();
                }
            });
        }
    }

    private void checkUserLogin() {
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            Intent i = new Intent(SyncMasterActivity.this, DashboardActivity.class);
            finish();
            startActivity(i);

        } else {
            finish();
            startActivity(new Intent(SyncMasterActivity.this, LoginActivity.class));

        }
    }

    @OnClick(R.id.btnSyncMasterActivitySynch)
    public void synch() {
        syncMaster();
    }

    public void request(String url) {
        L.pd("Please Wait", this);
        final String master_sync_date = MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE);
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                L.dismiss_pd();
                updateDisplay(response.toString());
                if (MyApplication.mySharedPreference.checkUserLogin()) {

                        Intent intent = new Intent(SyncMasterActivity.this, MainActivity.class);
                        //intent.putExtra(notificationType, true);
                        startActivity(intent);
                        finish();

                } else {
                    Intent intent = new Intent(SyncMasterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(SyncMasterActivity.this, "Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    if (error.getMessage().equalsIgnoreCase("null")) {
                        L.t("Something went wrong try again or check network connection");
                    }
                } else {
                    L.t("Something went wrong try again or check network connection");
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void updateDisplay(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            JSONArray designationArray = jsonObject.getJSONArray("positions");
            List<Designation> designations = new ArrayList<>();
            List<Designation> db_DesignationList = new ArrayList<>();
            DBHelper dbHelper = new DBHelper();
            db_DesignationList = dbHelper.getDesignationList();
            MyApplication.mySharedPreference.putPref(MySharedPreference.MASTER_SYNC_DATE, DateManagement.getCurrentDate());
            if (db_DesignationList.size() > 0) {
                //L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                boolean isPresent = false;
                for (int i = 0; i < designations.size(); i++) {
                    isPresent = false;
                    //L.l(TAG, "In Update/Insert Designation:");
                    for (Designation designation : db_DesignationList) {
                        if (designations.get(i).getDesigID().equals(designation.getDesigID())) {
                            isPresent = true;
                            //L.l(TAG, "In Update Designation STATE:" + isPresent);
                        }
                    }
                  //  L.l(TAG, "Out of for each Designation State:" + isPresent);
                    if (isPresent) {
                        dbHelper.getAppDaoSession().getDesignationDao().updateInTx(designations.get(i));
                       // L.l(TAG, "In Update Designation:");
                    } else {
                        dbHelper.getAppDaoSession().getDesignationDao().insertInTx(designations.get(i));
                       // L.l(TAG, "In Insert Designation:");
                    }
                }
            } else {
                //L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                dbHelper.getAppDaoSession().getDesignationDao().insertInTx(designations);
            }

//            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
//                new MasterSyncTask().execute(response);
//            } else {
//                L.t("" + jsonObject.getString("message"));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
            L.t_long(getString(R.string.err_server_site));
        }
    }

    private class MasterSyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            L.pd(getString(R.string.dialog_inseting_data), SyncMasterActivity.this);
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(params[0]);
                new DBHelper().sync_Master(jsonObject);
                MyApplication.mySharedPreference.putPref(MySharedPreference.MASTER_SYNC_DATE, DateManagement.getCurrentDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            L.dismiss_pd();
            tvSyncMasterMsg.setText("Data Sync Successfully");
            findViewById(R.id.btnSyncMasterActivitySynch).setVisibility(View.GONE);
            checkUserLogin();
        }
    }
}
