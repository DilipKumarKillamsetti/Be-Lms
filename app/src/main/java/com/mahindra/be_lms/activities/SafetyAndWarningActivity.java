package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Course;
import com.mahindra.be_lms.db.MenuRights;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SafetyAndWarningActivity extends BaseActivity implements View.OnClickListener, NetworkMethod {
    public static final String TAG = SafetyAndWarningActivity.class.getSimpleName();
    private CheckBox ch1;
    private Button next;
    private boolean fromMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_and_warning);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        initComp();
        if (MyApplication.mySharedPreference.getSAFETYACCEPT()) {
            ch1.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        } else {
            ch1.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        TextView tv_safety_cont = (TextView) findViewById(R.id.tv_safety_cont);
        tv_safety_cont.setText(Html.fromHtml(getResources().getString(
                R.string.stringtxt_safty)));
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        SafetyAndWarningActivity.this.finish();
    }

    private void initComp() {
        ch1 = (CheckBox) findViewById(R.id.cb_safety);
        next = (Button) findViewById(R.id.btnSafetyNext);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ch1.isChecked()) {

            if (L.isNetworkAvailable(this)) {
                if (Utility.checkReadPhoneStateAndExtStoragePermission(this)) {
                    L.pd(getString(R.string.dialog_please_wait), this);
                    request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_user_get_users_by_field&field=username&values%5b0%5d=" + MyApplication.mySharedPreference.getUserName() + "&moodlewsrestformat=json");
                } else {
                    Utility.callReadPhoneStateAndExtStoragePermission(this);
                }
            } else {
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .show();
            }


        } else {
            Toast.makeText(this, "Please accept the condition to login", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            SafetyAndWarningActivity.this.finish();
        } else {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearUserData();
            MyApplication.mySharedPreference.setUserLogin(false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
            finish();
            startActivity(new Intent(SafetyAndWarningActivity.this, LoginActivity.class));

        }
    }

    private void clearPreviousUserNotification() {
        try {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin() && fromMainActivity) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    @Override
    public void request(String url) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = response.getJSONObject(0);
                    if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                        User user = null;
                        MenuRights menuRights;
                        try {
                            Log.e("%%%%%%%", jsonObject.toString());
                            user = new Gson().fromJson(String.valueOf(jsonObject), User.class);
                            new DBHelper().saveUser(user);
                            MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());

                            String menuData = "{\"menurights\":{\"registration\":\"N\",\"search\":\"N\",\"powerolCare\":\"N\",\"mostViewed\":\"N\",\"myProfile\":\"Y\",\"surveyFeedbacks\":\"Y\",\"myTrainingPassport\":\"Y\",\"learningTestQuizs\":\"Y\",\"manualsBulletins\":\"Y\",\"trainingCalenderNomination\":\"Y\",\"queriesResponse\":\"Y\",\"technicalUploads\":\"Y\",\"myFieldRecords\":\"Y\",\"reports\":\"Y\",\"manpowerEdition\":\"Y\"}}";
                            JSONObject menu = new JSONObject(menuData);
                            menuRights = new Gson().fromJson(String.valueOf(menu.getJSONObject("menurights")), MenuRights.class);
                            L.l(SafetyAndWarningActivity.this, "MenuRights: " + menuRights.toString());
                            new DBHelper().saveUserMenuRights(menuRights);
                            if (!TextUtils.isEmpty(user.getUserPicture())) {
                                L.l(SafetyAndWarningActivity.this, "Profile Img Exists");
                                String url = user.getUserPicture();
                                Log.e(")())(", url);
                                L.l(SafetyAndWarningActivity.this, "PROFILE IMAGE URL: " + url);
                                //"https://www.google.com/images/srpr/logo3w.png"
                                getImageBitmap(url, "");
                            }
                            if (L.isNetworkAvailable(SafetyAndWarningActivity.this)) {
                                if (Utility.checkReadPhoneStateAndExtStoragePermission(SafetyAndWarningActivity.this)) {
                                    // L.pd(getString(R.string.dialog_please_wait), SafetyAndWarningActivity.this);
                                    requestCourses(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=moodle_enrol_get_users_courses&userid=" + MyApplication.mySharedPreference.getUserId() + "&moodlewsrestformat=json");
                                } else {
                                    Utility.callReadPhoneStateAndExtStoragePermission(SafetyAndWarningActivity.this);
                                }
                            } else {
                                L.dismiss_pd();
                                new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.WARNING_TYPE)
                                        .setContentText(getString(R.string.err_network_connection))
                                        .setConfirmText("OK")
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            L.dismiss_pd();
                        }

//                    MyApplication.mySharedPreference.setUserLogin(true);
//                    MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, true);
//                    new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
//                            .setCustomImage(R.drawable.success_circle)
//                            .setContentText("Login successfully")
//                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                                @Override
//                                public void onClick(PKBDialog customDialog) {
//                                    customDialog.dismiss();
//                                    clearPreviousUserNotification();
//                                    Intent intent = new Intent(SafetyAndWarningActivity.this,
//                                            MainActivity.class);
//                                    finish();
//                                    startActivity(intent);
//
//                                }
//                            }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    L.dismiss_pd();
                }
                L.l(SafetyAndWarningActivity.this, "RESPONSE : " + response.toString());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(SafetyAndWarningActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void requestCourses(String s) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, s, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                try {
                    List<Course> courses = new ArrayList<>();
                    jsonObject = response.getJSONObject(0);
                    DBHelper dbHelper = new DBHelper();
                    List<Course> db_CourseList = new ArrayList<>();
                    db_CourseList = dbHelper.getCourseList();
                    if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                        Course course = null;
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("courses");
                            if (db_CourseList.size() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    course = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Course.class);
                                    courses.add(course);
                                }
                                boolean isPresent = false;
                                for (int i = 0; i < courses.size(); i++) {
                                    isPresent = false;
                                    //L.l(TAG, "In Update/Insert Designation:");
                                    for (Course course1 : db_CourseList) {
                                        if (courses.get(i).getCourseID().equals(course1.getCourseID())) {
                                            isPresent = true;
                                            //L.l(TAG, "In Update Designation STATE:" + isPresent);
                                        }
                                    }
                                    //  L.l(TAG, "Out of for each Designation State:" + isPresent);
                                    if (isPresent) {
                                        dbHelper.getAppDaoSession().getCourseDao().updateInTx(courses.get(i));
                                        // L.l(TAG, "In Update Designation:");
                                    } else {
                                        dbHelper.getAppDaoSession().getCourseDao().insertInTx(courses.get(i));
                                        // L.l(TAG, "In Insert Designation:");
                                    }
                                }
                            }else {
                                //L.l(TAG, "Designations available: ");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Course course1 = new Gson().fromJson(String.valueOf(jsonArray.getJSONObject(i)), Course.class);
                                    L.l("Designation: " + course1);
                                    courses.add(course1);
                                }
                                dbHelper.getAppDaoSession().getCourseDao().insertInTx(courses);
                            }

                            //dbHelper.getAppDaoSession().getCourseDao().insertInTx(courses);
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.dismiss_pd();
                        }

                        L.dismiss_pd();
                        MyApplication.mySharedPreference.setUserLogin(true);
                        MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, true);
                        new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                .setCustomImage(R.drawable.success_circle)
                                .setContentText("Login successfully")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        clearPreviousUserNotification();
                                        Intent intent = new Intent(SafetyAndWarningActivity.this,
                                                MainActivity.class);
                                        finish();
                                        startActivity(intent);

                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                L.l(SafetyAndWarningActivity.this, "RESPONSE : " + response.toString());
                L.dismiss_pd();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(SafetyAndWarningActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(TAG, "PROFIEL PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    L.l(TAG, "BITMAP not NULL: " + response.toString());
                    boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, MyApplication.mySharedPreference.getUserId() + ".png");
                    L.l(TAG, "" + imagcreated);
                    MyApplication.flagProfilePicSet = imagcreated;
                    L.l(TAG, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                } else {
                    L.dismiss_pd();
                    L.l(TAG, "Bitmap NULL");
                }
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        Log.e(TAG, "PROFILE Fetch volley error: " + error.getMessage());
                        L.l(TAG, "PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }
}
