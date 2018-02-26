package com.mahindra.be_lms.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class OTPActivity2 extends BaseActivity implements NetworkMethod {

    @BindView(R.id.tvOTPResend)
    TextView tvOTPResend;
    @BindView(R.id.etOtp)
    EditText etEnterOTP;
    @BindView(R.id.activity_otp2)
    RelativeLayout activity_otp2;
    private String comeFrom, mobileno;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp2);
        ButterKnife.bind(this);
        comeFrom = getIntent().getStringExtra("from");
        mobileno = getIntent().getStringExtra("mobileno");
        L.l(this, "Intent mobile no: " + mobileno);
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            user = new DBHelper().getUser();
        }
        //new CommonFunctions().ActionBarTitleGravity(OTPActivity2.this, getSupportActionBar(), getString(R.string.otp_activity));
        getSupportActionBar().setTitle(getString(R.string.app_name));
        tvOTPResend.setText(Html.fromHtml("<font color='#757575'><small>Not received your OTP? </small></font><font color='#b11010'><b><u>Resend OTP</u></b></font>"));
    }

    /*
   OnTouch method to hide keyboard when touch outside
   */
    @OnTouch({R.id.activity_otp2, R.id.tvOTPResend})
    public boolean onTouchOTP(View view) {
        if (view.getId() == R.id.activity_otp2) {
            new CommonFunctions().hideSoftKeyboard(OTPActivity2.this);
        } else if (view.getId() == R.id.tvOTPResend) {
            if (comeFrom.equals("changemobno")) {
                if (L.isNetworkAvailable(this)) {
                    changemobilenoRequest(Constants.LMS_URL);
                } else {
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.dismiss();
                                }
                            }).show();
                }
            } else {
                if (L.isNetworkAvailable(this)) {
                    otpResendRequest(Constants.LMS_URL);
                } else {
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.dismiss();
                                }
                            }).show();
                }
            }
        }
        return false;
    }

    /*
    Onclick method for confirm OTP and resend OTP button
    */
    @OnClick(R.id.btnOTPConfirm)
    public void buttonClick(View v) {
        new CommonFunctions().hideSoftKeyboard(OTPActivity2.this);
        switch (v.getId()) {
            case R.id.btnOTPConfirm:
                if (MyValidator.isValidField(etEnterOTP, getString(R.string.err_enter_otp))) {
                    if (L.isNetworkAvailable(this)) {
                        if (comeFrom.equalsIgnoreCase("changemobno")) {
                            changemobilenoRequest(Constants.LMS_URL);
                        } else {
                            L.pd("Please wait", this);
                            request(Constants.LMS_URL);
                        }/*else if (comeFrom.equalsIgnoreCase("forgot")) {
                            forgotpasswordRequest(Constants.LMS_URL);
                        } */
                    } else {
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.err_network_connection))
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                        //L.t(getString(R.string.err_network_connection));
                    }
                }
                break;
        }
    }

    private void changemobilenoRequest(String url) {
        L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                try {
                    updateDisplayChangeMobileNo(response);
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(OTPActivity2.this, "Volley ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*action = check_otp_for_change_mobileno
                userid = 14528
                otp = WTUCB*/
                params.put("action", "check_otp_for_change_mobileno");
                if (MyApplication.mySharedPreference.checkUserLogin()) {
                    params.put("userid", MyApplication.mySharedPreference.getUserId());
                } else {
                    params.put("userid", MyApplication.mySharedPreference.getUniqueID());
                }
                params.put("otp", L.getText(etEnterOTP));
                L.l(OTPActivity2.this, "PARAMS Change MObile: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    //Method to change display as per response of change mobile number otp webservice call
    private void updateDisplayChangeMobileNo(String response) throws JSONException {
        JSONObject jsonObject;
        jsonObject = new JSONObject(response);
        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
            if (MyApplication.mySharedPreference.checkUserLogin()) {
                user.setUserMobileNo(mobileno);
                new DBHelper().updateUser(user);
                new PKBDialog(OTPActivity2.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(getString(R.string.msg_mobile_no_chnaged))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                OTPActivity2.this.finish();
                            }
                        }).show();
            } else {
                //L.t_long("Mobile number changed successfully..!!");
                new PKBDialog(OTPActivity2.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(getString(R.string.msg_mobile_no_chnaged))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                finish();
                                startActivity(new Intent(OTPActivity2.this, LoginActivity.class));

                            }
                        }).show();
            }
        } else {
            //L.t("" + jsonObject.getString("message"));
            new PKBDialog(OTPActivity2.this, PKBDialog.WARNING_TYPE)
                    .setContentText(jsonObject.getString("message")).show();
        }
    }

    /*
     request for OTP verification
    */
    @Override
    public void request(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                L.dismiss_pd();
                L.l(OTPActivity2.this, "RESPONSE : " + response);
                updateDisplay(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(OTPActivity2.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(OTPActivity2.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", Constants.CHECK_OTP_ACTION);
                param.put("mobileno", mobileno);
                param.put("otp", L.getText(etEnterOTP));
                param.put("flag", "getdata");
                L.l(OTPActivity2.this, "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    /*
     process on OTP verification response
    */
    private void updateDisplay(String response) {
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);

            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                //startActivity(new Intent(this, LoginActivity.class));
                //L.t_long("" + jsonObject.getString("message"));
                /*String message = null;
                if (comeFrom.equalsIgnoreCase("register")) {
                    message = "Registered successfully";
                } else if (comeFrom.equalsIgnoreCase("login")) {
                    message = "Login successfully";
                } else {
                    message = "Success";
                }*/
                User user = null;
                MenuRights menuRights;
                try {
                    user = new Gson().fromJson(String.valueOf(jsonObject.getJSONObject("data")), User.class);
                    //user.setPassword(new CommonFunctions().EncryptString(user.getPassword()));
                    new DBHelper().saveUser(user);
                    MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());
                    menuRights = new Gson().fromJson(String.valueOf(jsonObject.getJSONObject("data").getJSONObject("menurights")), MenuRights.class);
                    L.l(OTPActivity2.this, "MenuRights: " + menuRights.toString());
                    new DBHelper().saveUserMenuRights(menuRights);
                    if (!TextUtils.isEmpty(user.getUserPicture())) {
                        L.l(OTPActivity2.this, "Profile Img Exists");
                        ///Bitmap bitmap=new ImageHelper().getImageBitmapFromUrl(Constants.PROFILE_UPLOAD_URL+ user.getUserPicture());
                        String url = Constants.PROFILE_UPLOAD_URL + user.getUserPicture();
                        L.l(OTPActivity2.this, "PROFILE IMAGE URL: " + url);
                        getImageBitmap(url, user.getUserPicture());
                    }
                    finish();
                    MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
                    MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
                    startActivity(new Intent(OTPActivity2.this, DisclaimerActivity.class));
                    //L.t_long("Login successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

               /* new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText(message)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();


                            }
                        }).show();
*/
            } else {
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message")).show();
                //L.t("" + jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.somthing_went_wrong)).show();
        }
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(this, "PROFILE PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    L.l(OTPActivity2.this, "BITMAP not NULL: " + response.toString());
                    boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, MyApplication.mySharedPreference.getUserId() + ".png");
                    L.l(OTPActivity2.this, "" + imagcreated);
                    MyApplication.flagProfilePicSet = imagcreated;
                    L.l(OTPActivity2.this, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                } else {
                    L.dismiss_pd();
                    L.l(OTPActivity2.this, "Bitmap NULL");
                }
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        L.l(OTPActivity2.this, "PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }

    /*
     process on Resend OTP to user mobile number
    */
    private void otpResendRequest(String otpUrl) {
        L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, otpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("================",response);
                L.dismiss_pd();
                JSONObject jsonObject = null;
                try {
                    L.l(OTPActivity2.this, response);
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                        //   L.t_long(getString(R.string.otp_resend_msg));
                        new PKBDialog(OTPActivity2.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                .setContentText(getString(R.string.otp_resend_msg))
                                .setCustomImage(R.drawable.success_circle)
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                        // startActivity(new Intent(OTPActivity2.this, LoginActivity.class));
                        //finish();
                    } else {
                        //L.t_long("OTP Not Send");
                        new PKBDialog(OTPActivity2.this, PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.otpfrg_otp_send_failed_msg))
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(OTPActivity2.this, "Volley ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(OTPActivity2.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.RESEND_OTP_ACTION);
                params.put("mobileno", mobileno);
                L.l(OTPActivity2.this, "PARAM " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (comeFrom.equals("changemobno")) {
            if (MyApplication.mySharedPreference.checkUserLogin()) {
                finish();
            } else {
                finish();
                startActivity(new Intent(OTPActivity2.this, LoginActivity.class));
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.dialog_exit_msg))
                    .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = null;
                            if (comeFrom.equalsIgnoreCase("register")) {
                                intent = new Intent(OTPActivity2.this, RegisterActivity.class);
                            } else if (comeFrom.equalsIgnoreCase("login")) {
                                intent = new Intent(OTPActivity2.this, LoginActivity.class);
                            }
                            finish();
                            startActivity(intent);
                        }
                    }).setNegativeButton(getString(R.string.dialog_no), null).show();
        }
    }

   /* private void forgotpasswordRequest(String forgotUrl) {
        L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forgotUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(OTPActivity2.this, "RESPONSE: " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                        L.t_long("" + jsonObject.getString("message"));
                        MyApplication.mySharedPreference.setUserLogin(false);
                        startActivity(new Intent(OTPActivity2.this, LoginActivity.class));
                        finish();
                    } else {
                        L.t_long("" + jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(OTPActivity2.this, "Volley ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                *//*action = check_otp_for_change_mobileno
                userid = 14528
                otp = WTUCB*//*
                params.put("action", "check_otp_for_forgot_password");
                params.put("mobileno", mobileno);
                params.put("otp", L.getText(etEnterOTP));
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }
*/
}
