package com.mahindra.be_lms.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.MenuRights;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.ManualInfoModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.QuickTest;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by Chaitali on 9/23/16.
 * Updated by Chaitali Chavan on 11/17/16.
 */

public class LoginActivity extends BaseActivity implements NetworkMethod {
    public static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etLoginMobile)
    EditText etLoginMobile;
    @BindView(R.id.etLoginCountrycode)
    EditText etLoginCountrycode;
    @BindView(R.id.etLoginPassword)
    EditText etLoginPassword;
    @BindView(R.id.tvSignup)
    TextView tvSignup;
    private EditText etChangeMobileNoDialogMobileNo, etChangeMobileNoDialogNewMob, etChangeMobileNoDialogCountryCode;
    private int retry_count = 1;
    private String mobileno = "", changemobileno = "";
    private boolean isMobileNo = false;
    @BindView(R.id.cb_hide_show_pw)
    CheckBox cb_hide_show_pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        etLoginPassword.setText("Admin@123");
        etLoginMobile.setText("Testinguser");

        //requestXML("http://mahindramile.com/WindowAuth/Service.asmx/ValidateCredential?LoginID=kumavij-co&Password=TWFoaW5kcmFAMTIzNA==");
        try {
            getSupportActionBar().hide();
            TextView tvNoteLogin = (TextView) findViewById(R.id.note_login);
            tvNoteLogin.setText(Html.fromHtml(getString(R.string.note_login_mobile_no_validation)));
            etLoginCountrycode.setText("+91");

            Selection.setSelection(etLoginCountrycode.getText(), etLoginCountrycode.getText().length());
            etLoginCountrycode.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().contains("+")) {
                        etLoginCountrycode.setText("+");
                        Selection.setSelection(etLoginCountrycode.getText(), etLoginCountrycode.getText().length());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.l(this, "Location List: " + new DBHelper().getLocaitonList().toString());
        tvSignup.setText(Html.fromHtml("<font color='#e31837'><b><u>Sign up (new user)</u></b></font> "));
    }

    @OnCheckedChanged(R.id.cb_hide_show_pw)
    public void onChecked(boolean checked) {
        if (checked) {
            etLoginPassword.setTransformationMethod(null);
        } else {
            etLoginPassword.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        etLoginPassword.setSelection(etLoginPassword.getText().length());
    }

    /*
    On click method for all
     */
    @OnClick({R.id.btnLogin, R.id.tvLoginChangeMobileNumber, R.id.tvHelp, R.id.tvContactToAdmin})
    public void onClick(View v) {
        new CommonFunctions().hideSoftKeyboard(LoginActivity.this);
        switch (v.getId()) {
            case R.id.btnLogin:
                boolean isMobile = true;
                if (validateFields()) {
                    if (L.isNetworkAvailable(this)) {
                        if (Utility.checkReadPhoneStateAndExtStoragePermission(this)) {
                            L.pd(getString(R.string.dialog_please_wait), this);
                            request(Constants.BE_LMS_Common_URL + "login/token.php?username=" + etLoginMobile.getText().toString() + "&password=" + etLoginPassword.getText().toString() + "&fcmtokenid=" + MyApplication.mySharedPreference.getFcmToken() + "&service=moodle_mobile_app");
                        } else {
                            Utility.callReadPhoneStateAndExtStoragePermission(this);
                        }
                    } else {
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.err_network_connection))
                                .setConfirmText("OK")
                                .show();
                    }
                }
                break;
            case R.id.tvLoginChangeMobileNumber:
                showChangeMobileNumberDialog();
                break;
            case R.id.tvHelp:
                if (L.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, UserHelpActivity.class).putExtra("help_type", "registration"));
                } else {
                    L.t(getString(R.string.err_network_connection));
                }
                break;
            case R.id.tvContactToAdmin:
                if (L.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, UserHelpActivity.class).putExtra("help_type", "contact_admin"));
                } else {
                    L.t(getString(R.string.err_network_connection));
                }
                break;
        }
    }

    /*
    Show AlertDialog for Change mobile number
     */
    private void showChangeMobileNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_change_mobile_number_dialog_layout, null);
        builder.setView(dialogView)
                //.setTitle(getString(R.string.dialog_change_mobile_number_title))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .setPositiveButton(getString(R.string.dialog_submit), null);
        final AlertDialog d = builder.create();
        TextView tvNotChangePass = (TextView) dialogView.findViewById(R.id.note_change_pass);
        tvNotChangePass.setText(Html.fromHtml(getString(R.string.note_mobile_no_validation)));

        etChangeMobileNoDialogMobileNo = (EditText) dialogView.findViewById(R.id.etChangeMobileNoDialogUniqueID);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btn_positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setTransformationMethod(null);
                Button btn_negative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setTransformationMethod(null);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.l("Change mobile no new no: " + changemobileno);
                        if (MyValidator.isValidUserName(etChangeMobileNoDialogMobileNo)) {
                            if (L.isNetworkAvailable(LoginActivity.this)) {
                                d.dismiss();
                                forgotPasswordRequest(Constants.BE_LMS_FORGOTPASSWORD_URL + etChangeMobileNoDialogMobileNo.getText().toString() + "&moodlewsrestformat=json");
                            } else {
                                new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                                        .setContentText(getString(R.string.err_network_connection))
                                        .setConfirmText("OK")
                                        .show();
                            }
                        }

                    }
                });
            }
        });
        d.show();
    }

    private void forgotPasswordRequest(String lmsUrl) {
        L.pd("Please wait", this);
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, lmsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                        new PKBDialog(LoginActivity.this, PKBDialog.NORMAL_TYPE)
                                .setContentText(getString(R.string.sucess_pwd_send))
                                .setConfirmText("OK")
                                .show();
                    } else {
                        new PKBDialog(LoginActivity.this, PKBDialog.ERROR_TYPE)
                                .setContentText(getString(R.string.somthing_went_wrong))
                                .setConfirmText("OK")
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                L.dismiss_pd();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(LoginActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        int socketTimeout = 30000;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        L.l(this, "Permission callback called-------");
        switch (requestCode) {
            case Utility.PERMISSION_READ_PHONE_STATE: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e("Device------", MyApplication.mySharedPreference.getFcmToken());
                        if (L.isNetworkAvailable(this)) {
                            if (Utility.checkReadPhoneStateAndExtStoragePermission(this)) {
                                L.pd(getString(R.string.dialog_please_wait), this);
                                request(Constants.BE_LMS_Common_URL + "login/token.php?username=" + etLoginMobile.getText().toString() + "&password=" + etLoginPassword.getText().toString() + "&fcmtokenid=" + MyApplication.mySharedPreference.getFcmToken() + "&service=moodle_mobile_app");
                            } else {
                                Utility.callReadPhoneStateAndExtStoragePermission(this);
                            }
                        } else {
                            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_network_connection))
                                    .setConfirmText("OK")
                                    .show();
                        }
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        L.l(this, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            showDialogOK("Read Phone State and External Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Utility.callReadPhoneStateAndExtStoragePermission(LoginActivity.this);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });

                        } else {
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            L.l(this, "Go to settings and enable permissions");
                            //proceed with logic by disabling the related features or quit the app.
                            showDialogOK("Read Phone State and External Storage Permission required for login please enable from setting",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }

                    }
                } else {
                    L.l(this, "Permission NOT granted");
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                // .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    /*
    Validation method of login fields
     */
    private boolean validateFields() {
        boolean flag = true;
        if (!MyValidator.isValidField(etLoginMobile, getString(R.string.err_enter_username))) {
            flag = false;
        } else if (!MyValidator.isValidField(etLoginPassword, getString(R.string.err_enter_password))) {
            flag = false;
        }
        return flag;
    }

    /*
    OnTouch method to hide keyboard when touch outside
     */
    @OnTouch({R.id.llLoginLayout, R.id.tvSignup})
    public boolean onTouchLogin(View view) {
        if (view.getId() == R.id.llLoginLayout) {
            new CommonFunctions().hideSoftKeyboard(LoginActivity.this);
        } else if (view.getId() == R.id.tvSignup) {
            finish();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        }
        return false;
    }

    /*
    request for login
    */
    @Override
    public void request(String url) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                L.l(LoginActivity.this, "RESPONSE : " + response.toString());
                updateDisplay(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(LoginActivity.this, "ERROR : " + error.getMessage());

                if (L.checkNull(error.getMessage())) {
                    if (error.networkResponse.statusCode == 401) {

                        new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                                .setContentText("Invalid login details, please try again.").show();

                    } else {
                        new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                                .setContentText("Network problem please try again").show();
                    }
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }


    public void requestXML(String url) {
        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    Log.e("=====",response);
                L.l(LoginActivity.this, "RESPONSE : " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  L.dismiss_pd();
                L.l(LoginActivity.this, "ERROR : " + error.getMessage());

                if (L.checkNull(error.getMessage())) {
                    if (error.networkResponse.statusCode == 401) {

                        new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                                .setContentText("Invalid login details, please try again.").show();

                    } else {
                        new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                                .setContentText("Network problem please try again").show();
                    }
                }
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(req);
    }

    /*
    process on login response
    */
    private void updateDisplay(String response) {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                    MyApplication.mySharedPreference.putPref(MySharedPreference.USER_TOKEN, jsonObject.getString("token"));
                    MyApplication.mySharedPreference.putPref(MySharedPreference.USER_NAME, jsonObject.getString("UserName"));

                    if (L.isNetworkAvailable(this)) {
                        if (Utility.checkReadPhoneStateAndExtStoragePermission(this)) {
                            // L.pd(getString(R.string.dialog_please_wait), this);
                            requestDetails(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_user_get_users_by_field&field=username&values%5b0%5d=" + MyApplication.mySharedPreference.getUserName() + "&moodlewsrestformat=json");
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
                    if (jsonObject.getString("error").equalsIgnoreCase(getString(R.string.push_id_require_msg))) {
                        if (retry_count <= 3) {
                            L.l(LoginActivity.this, "Retry to Login WS: " + retry_count);
                            retry_count += 1;
                            L.l(LoginActivity.this, "Retry to Login WS Count plus: " + retry_count);
                            request(Constants.LMS_URL);
                        } else {
                            L.dismiss_pd();
                            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.login_failed_msg))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            retry_count = 1;
                                            etLoginMobile.setText("");
                                        }
                                    }).show();
                        }
                    } else {
                        L.dismiss_pd();
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(jsonObject.getString("error")).show();
                    }
                }
            } catch (JSONException e) {
                L.dismiss_pd();
                e.printStackTrace();
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.somthing_went_wrong)).show();
            }
        }
    }

    /*
    Change Mobile Number Webservice Call Method
     */
    private void changeMobileNumberRequest(String forgoturl) {
        L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forgoturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(LoginActivity.this, "Change mobile number RESPONSE : " + response.toString());
                try {
                    changeMobileNumberUpdateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(LoginActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", "change_mobile_no_logout");
                param.put("username", L.getText(etChangeMobileNoDialogMobileNo));
                param.put("mobileno", changemobileno);
                L.l(LoginActivity.this, "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    /*
    process on change mobile number response
    */
    private void changeMobileNumberUpdateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                MyApplication.mySharedPreference.setUniqueID(etChangeMobileNoDialogMobileNo.getText().toString());
                                Intent otpIntent = new Intent(LoginActivity.this, OTPActivity2.class);
                                otpIntent.putExtra("from", "changemobno");
                                otpIntent.putExtra("mobileno", changemobileno);
                                finish();
                                startActivity(otpIntent);

                            }
                        })
                        .show();
                // L.t(jsonObject.getString("message"));

            } else {
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .show();
                //  L.t(jsonObject.getString("message"));
            }
        }
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(TAG, "PROFIEL PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest("http://13.76.164.143/businessexcellence/webservice/pluginfile.php/92/user/icon/basis/f1?rev=951&token=eaaeaab35c2f7a7d7e1734340b1c376f774d14a5", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    L.l(TAG, "BITMAP not NULL: " + response.toString());
                    boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, filename);
                    L.l(TAG, "" + imagcreated);
                    MyApplication.flagProfilePicSet = imagcreated;
                    L.l(TAG, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                } else {
                    L.dismiss_pd();
                    L.l(TAG, "Bitmap NULL");
                }
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        L.l(TAG, "PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void requestDetails(String url) {
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
                            user = new Gson().fromJson(String.valueOf(jsonObject), User.class);
                            new DBHelper().saveUser(user);
                            MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());
                            String menuData = "{\"menurights\":{\"registration\":\"N\",\"search\":\"N\",\"powerolCare\":\"N\",\"mostViewed\":\"N\",\"myProfile\":\"Y\",\"surveyFeedbacks\":\"Y\",\"myTrainingPassport\":\"Y\",\"learningTestQuizs\":\"Y\",\"manualsBulletins\":\"Y\",\"trainingCalenderNomination\":\"Y\",\"queriesResponse\":\"Y\",\"technicalUploads\":\"Y\",\"myFieldRecords\":\"Y\",\"reports\":\"Y\",\"manpowerEdition\":\"Y\"}}";
                            JSONObject menu = new JSONObject(menuData);
                            menuRights = new Gson().fromJson(String.valueOf(menu.getJSONObject("menurights")), MenuRights.class);
                            L.l(LoginActivity.this, "MenuRights: " + menuRights.toString());
                            new DBHelper().saveUserMenuRights(menuRights);
                            if (!TextUtils.isEmpty(user.getUserPicture())) {
                                L.l(LoginActivity.this, "Profile Img Exists");
                                String url = user.getUserPicture();
                                L.l(LoginActivity.this, "PROFILE IMAGE URL: " + url);
                                getImageBitmap("http://13.76.164.143/businessexcellence/webservice/pluginfile.php/92/user/icon/basis/f1?rev=951&token=eaaeaab35c2f7a7d7e1734340b1c376f774d14a5", "");
                            }
                            L.dismiss_pd();
                            MyApplication.mySharedPreference.setUserLogin(true);
                            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, true);
                            new PKBDialog(LoginActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setCustomImage(R.drawable.success_circle)
                                    .setContentText("Login successfully")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            //clearPreviousUserNotification();
                                            Intent intent = new Intent(LoginActivity.this,
                                                    DisclaimerActivity.class);
                                            finish();
                                            startActivity(intent);
                                        }
                                    }).show();

//                            if (L.isNetworkAvailable(LoginActivity.this)) {
//                                if (Utility.checkReadPhoneStateAndExtStoragePermission(LoginActivity.this)) {
//                                    // L.pd(getString(R.string.dialog_please_wait), SafetyAndWarningActivity.this);
//                                    requestCourses(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=moodle_enrol_get_users_courses&userid=" + MyApplication.mySharedPreference.getUserId() + "&moodlewsrestformat=json");
//                                } else {
//                                    Utility.callReadPhoneStateAndExtStoragePermission(SafetyAndWarningActivity.this);
//                                }
//                            } else {
//                                L.dismiss_pd();
//                                new PKBDialog(SafetyAndWarningActivity.this, PKBDialog.WARNING_TYPE)
//                                        .setContentText(getString(R.string.err_network_connection))
//                                        .setConfirmText("OK")
//                                        .show();
//                            }

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
                L.l(LoginActivity.this, "RESPONSE : " + response.toString());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(LoginActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(LoginActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }
}
