package com.mahindra.be_lms.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Updated by Chaitali Chavan on 12/29/16.
 */

public class RegisterActivity extends BaseActivity implements NetworkMethod {
    @BindView(R.id.etRegisterFirstName)
    EditText etRegisterFirstName;
    @BindView(R.id.etRegisterLastName)
    EditText etRegisterLastName;
    @BindView(R.id.etRegisterMobileNo)
    EditText etRegisterMobileNo;
    @BindView(R.id.etRegisterUserName)
    EditText etRegisterUserName;
    @BindView(R.id.etRegisterPassword)
    EditText etRegisterPassword;
    @BindView(R.id.etRegisterEmail)
    EditText etRegisterEmail;
//    @BindView(R.id.etRegisterCompanyName)
//    EditText etRegisterCompanyName;
    @BindView(R.id.btnRegistrationSendOTP)
    Button btnRegistrationSendOTP;
    @BindView(R.id.activity_register)
    LinearLayout activity_register;
    @BindView(R.id.etRegisterCountryCode)
    EditText etRegisterCountryCode;
    String mobileno = "";
    private boolean confirmparam;
    private int retry_count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        TextView tvNoteRegistration = (TextView) findViewById(R.id.tv_note_registration);
        tvNoteRegistration.setText(Html.fromHtml(getString(R.string.note_mobile_no_validation)));
        etRegisterCountryCode.setText("+91");
        Selection.setSelection(etRegisterCountryCode.getText(), etRegisterCountryCode.getText().length());
        etRegisterCountryCode.addTextChangedListener(new TextWatcher() {
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
                    etRegisterCountryCode.setText("+");
                    Selection.setSelection(etRegisterCountryCode.getText(), etRegisterCountryCode.getText().length());
                }
            }
        });
//        etRegisterCompanyName.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence cs, int start,
//                                               int end, Spanned spanned, int dStart, int dEnd) {
//                        // TODO Auto-generated method stub
//                        if (cs.equals("")) { // for backspace
//                            return cs;
//                        }
//                        if (cs.equals(".")) {
//                            return cs;
//                        }
//                        if (cs.toString().matches("[a-zA-Z ]+")) {
//                            return cs;
//                        }
//                        return "";
//                    }
//                }, new InputFilter.LengthFilter(50)
//
//        });
        etRegisterFirstName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        if (cs.toString().matches("[a-zA-Z]+")) {
                            return cs;
                        }
                        return "";
                    }
                }, new InputFilter.LengthFilter(20)
        });
        etRegisterLastName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        if (cs.toString().matches("[a-zA-Z]+")) {
                            return cs;
                        }
                        return "";
                    }
                }, new InputFilter.LengthFilter(20)
        });
        // int maxLength = 50;
        // etRegisterCompanyName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    /*
    On click method for send otp button
    */
    @OnClick(R.id.btnRegistrationSendOTP)
    public void onClick() {
        mobileno = etRegisterCountryCode.getText().toString().trim() + etRegisterMobileNo.getText().toString().trim();
        L.l(this, "Registration mobile no: " + mobileno);
        if (validateField()) {
            if (L.isNetworkAvailable(this)) {
                if (Utility.checkReadPhoneStateAndExtStoragePermission(this)) {

                    confirmparam = false;
                    request(Constants.BE_LMS_REGISTER_URL);
                } else {
                    Utility.callReadPhoneStateAndExtStoragePermission(this);
                }
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

    /*
    OnTouch method for all relative Layout to launch particular spinner
    */
    @OnTouch({R.id.sc_register, R.id.llRegisterMain})
    public boolean onRelativeLayoutTouch(View view) {
        switch (view.getId()) {
            case R.id.sc_register:
                new CommonFunctions().hideSoftKeyboard(RegisterActivity.this);
                break;
            case R.id.llRegisterMain:
                new CommonFunctions().hideSoftKeyboard(RegisterActivity.this);
                break;
        }
        return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.help_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_user_help) {
            if (L.isNetworkAvailable(this)) {
                startActivity(new Intent(this, UserHelpActivity.class).putExtra("help_type", "registration"));
            } else {
                L.t(getString(R.string.err_network_connection));
            }
        }
        return super.onOptionsItemSelected(item);
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
                        L.l(this, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        if (L.isNetworkAvailable(this)) {
                            //startActivity(new Intent(this,MainActivity.class));
                            L.pd(getString(R.string.dialog_please_wait), this);
                            request(Constants.LMS_URL);

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
                                                    Utility.callReadPhoneStateAndExtStoragePermission(RegisterActivity.this);
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
                            showDialogOK("Read Phone State and External Storage Permission required for register please enable from setting",
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
    Validation method of registration fields
     */
    private boolean validateField() {
        boolean flag = true;
        if (!MyValidator.isValidField(etRegisterFirstName, getString(R.string.err_enter_firstname))) {
            flag = false;
        } else if (!MyValidator.isValidField(etRegisterLastName, getString(R.string.err_enter_lastname))) {
            flag = false;
        } else if (!MyValidator.isValidField(etRegisterUserName, getString(R.string.err_enter_username))) {
            flag = false;
        }
        else if (!MyValidator.isValidField(etRegisterPassword, getString(R.string.err_enter_password))) {
            flag = false;
        }
        else if (!MyValidator.isValidPassword(etRegisterPassword)) {
            flag = false;
        }
        else if (!MyValidator.isValidPasswordPattern(etRegisterPassword)) {
            flag = false;
        }else if (!MyValidator.isValidIntenationalMobile(mobileno, etRegisterCountryCode, etRegisterMobileNo)) {
            flag = false;
        }else if (!MyValidator.isValidField(etRegisterEmail, getString(R.string.err_enter_email))) {
            flag = false;
        }else if (!MyValidator.isValidEmail(etRegisterEmail)) {
            flag = false;
        }
//        else if (!MyValidator.isValidField(etRegisterCompanyName, getString(R.string.err_enter_company))) {
//            flag = false;
//        }
        return flag;
    }

    /*
     request for registration
    */
    @Override
    public void request(String url) {
        L.pd("Please wait", this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //L.dismiss_pd();

                L.l(RegisterActivity.this, "RESPONSE : " + response);
                updateDisplay(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(RegisterActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(RegisterActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("firstname", L.getText(etRegisterFirstName).toString().trim());
                param.put("surname", L.getText(etRegisterLastName).toString().trim());
                param.put("mobile", L.getText(etRegisterMobileNo).toString().trim());
                param.put("username", L.getText(etRegisterUserName).toString().trim());
                param.put("password", L.getText(etRegisterPassword).toString().trim());
                param.put("email", L.getText(etRegisterEmail).toString().trim());
                param.put("fcmtokenid", MyApplication.mySharedPreference.getFcmToken());
                param.put("companyname","");
                param.put("department","");
                param.put("position","");
                param.put("location","");
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    /*
     process on registration response
    */
    private void updateDisplay(String response) {
        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = new JSONArray(response);
            jsonObject = (JSONObject) jsonArray.get(0);
            Log.e("_+_+_++_",jsonObject.toString());
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                //L.t_long("" + jsonObject.getString("result"));
                L.dismiss_pd();
                if (jsonObject.getString("message").equalsIgnoreCase("this username exist")) {
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.user_already_exists_msg))
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.cancel();
                                    finish();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }
                            }).show();
                } else {
                    if (jsonObject.has("status") && jsonObject.getString("Status").equals("AlreadyExist")) {
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(jsonObject.getString("message"))
                                .setCustomImage(R.drawable.success_circle)
                                .setCancelText("Yes")
                                .setCancelClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        finish();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                    }
                                })
                                .setConfirmText("No")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        if (L.isNetworkAvailable(RegisterActivity.this)) {
                                            customDialog.dismiss();
                                            confirmparam = true;
                                            request(Constants.LMS_URL);
                                        }
                                    }
                                }).show();
                    } else {
                        new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                .setContentText("User register successfully.")
                                .setCustomImage(R.drawable.success_circle)
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        Intent otpIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(otpIntent);
                                        //L.t_long(jsonObject.getString("message"));
                                        finish();
                                    }
                                }).show();
                    }

                }
            } else {
                if (jsonObject.getString("message").equalsIgnoreCase(getString(R.string.push_id_require_msg))) {
                    if (retry_count <= 3) {
                        L.l(RegisterActivity.this, "Retry to Login WS");
                        retry_count += 1;
                        request(Constants.LMS_URL);
                    } else {
                        L.dismiss_pd();
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.register_failed_msg))
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        retry_count = 1;
                                    }
                                }).show();
                    }
                }else if (jsonObject.getString("message").equalsIgnoreCase("this username exist")) {
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.user_already_exists_msg))
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.cancel();
                                    finish();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }
                            }).show();
                } else {
                    L.dismiss_pd();
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(jsonObject.getString("message")).show();
                }
            }
        } catch (JSONException e) {
            L.dismiss_pd();
            e.printStackTrace();
            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.somthing_went_wrong)).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
