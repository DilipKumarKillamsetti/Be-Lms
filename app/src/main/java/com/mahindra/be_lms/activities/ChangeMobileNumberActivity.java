package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ChangeMobileNumberActivity extends BaseActivity implements NetworkMethod {
    @BindView(R.id.etChangeMobileNoMobile)
    EditText etChangeMobileNoMobile;
    @BindView(R.id.etChangeMobileCountryCode)
    EditText etChangeMobileCountryCode;
    private String mobileno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile_number);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView tvNoteChangeMo = (TextView) findViewById(R.id.tv_note_change_mobile);
        tvNoteChangeMo.setText(Html.fromHtml(getString(R.string.note_mobile_no_validation)));
        etChangeMobileCountryCode.setText("+91");
        Selection.setSelection(etChangeMobileCountryCode.getText(), etChangeMobileCountryCode.getText().length());
        etChangeMobileCountryCode.addTextChangedListener(new TextWatcher() {
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
                    etChangeMobileCountryCode.setText("+");
                    Selection.setSelection(etChangeMobileCountryCode.getText(), etChangeMobileCountryCode.getText().length());
                }
            }
        });

    }

    @OnClick(R.id.btnChangeMobileNumber)
    public void btnOnClick() {
        new CommonFunctions().hideSoftKeyboard(ChangeMobileNumberActivity.this);
        mobileno = etChangeMobileCountryCode.getText().toString().trim() + etChangeMobileNoMobile.getText().toString().trim();
        L.l(this, "Change Mobile NO new number: " + mobileno);
        if (validateFields()) {
            if (L.isNetworkAvailable(this)) {
                request(Constants.LMS_URL);
            } else {
                L.t(getString(R.string.err_network_connection));
            }
        }
    }

    @OnTouch({R.id.activity_change_mobile_number})
    public boolean onTouchChangeMob(View view) {
        if (view.getId() == R.id.activity_change_mobile_number) {
            new CommonFunctions().hideSoftKeyboard(ChangeMobileNumberActivity.this);
        }
        return false;
    }

    private boolean validateFields() {
        boolean flag = true;
        if (!MyValidator.isValidIntenationalMobile(mobileno, etChangeMobileCountryCode, etChangeMobileNoMobile)) {
            flag = false;
        }
        return flag;
    }

    @Override
    public void request(String url) {
        L.pd("Updating mobile no", "Please wait", this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(ChangeMobileNumberActivity.this, "Change mobile no RESPONSE: " + response);
                try {
                    updateDisplay(response);
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(ChangeMobileNumberActivity.this, "Volley ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(ChangeMobileNumberActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*action = change_mobile_no
                userid = 14528
                mobileno = 7030765183
                password = P3VB8qH27O*/
                params.put("action", "change_mobile_no");
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("mobileno", "" + mobileno);
                //params.put("password",""+ L.getText(etChangeMobileNoPassword));
                L.l(ChangeMobileNumberActivity.this, "Change Mobile No PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(request);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                new PKBDialog(this).setContentText("OTP has been sent to your mobile no " + mobileno)
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                Intent otpIntent = new Intent(ChangeMobileNumberActivity.this, OTPActivity2.class);
                                otpIntent.putExtra("from", "changemobno");
                                otpIntent.putExtra("mobileno", mobileno);
                                startActivity(otpIntent);
                                ChangeMobileNumberActivity.this.finish();
                            }
                        }).show();
            } else {
                // L.t(jsonObject.getString("message"));
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message")).show();
            }
        }
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        ChangeMobileNumberActivity.this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
}
