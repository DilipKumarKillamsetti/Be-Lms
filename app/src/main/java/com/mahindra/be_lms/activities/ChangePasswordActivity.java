package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
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

public class ChangePasswordActivity extends BaseActivity implements NetworkMethod {
    @BindView(R.id.etChangePasswordNewPass)
    EditText etChangePasswordNewPass;

    @BindView(R.id.etChangePasswordOldPass)
    EditText etChangePasswordOldPass;

    @BindView(R.id.etChangePasswordConfirmPass)
    EditText etChangePasswordConfirmPassword;
    @BindView(R.id.activity_change_password)
    LinearLayout activity_change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(getResources().getDrawable(
//                R.drawable.header_drawable));
        actionBar.setTitle(getString(R.string.change_password_activity));
        actionBar.setDisplayHomeAsUpEnabled(true);
       // new CommonFunctions().ActionBarTitleGravity(ChangePasswordActivity.this, getSupportActionBar(), getString(R.string.change_password_activity));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick(R.id.btnChangePasswordSubmit)
    public void OnClickSubmit() {
        new CommonFunctions().hideSoftKeyboard(ChangePasswordActivity.this);
        if (validateFields()) {
           // String url = Constants.LMS_URL;
            request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=core_change_password&moodlewsrestformat=json");

        }
    }

    @OnTouch({R.id.activity_change_password})
    public boolean onTouchChangePwd(View view) {
        if (view.getId() == R.id.activity_change_password) {
            new CommonFunctions().hideSoftKeyboard(ChangePasswordActivity.this);
        }
        return false;
    }

    private boolean validateFields() {
        boolean flag = true;
        if (!MyValidator.isValidPassword(etChangePasswordOldPass)) {
            flag = false;
        } else if (!MyValidator.isValidPassword(etChangePasswordNewPass)) {
            flag = false;
        }else if (!MyValidator.isValidPasswordPattern(etChangePasswordNewPass)) {
            flag = false;
        }else {
            if (!MyValidator.isValidConfirmPassword(etChangePasswordNewPass, etChangePasswordConfirmPassword)) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public void request(String url) {
        L.pd("please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(ChangePasswordActivity.this, "RESPONSE : " + response.toString());
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
                L.l(ChangePasswordActivity.this, "ERROR : " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("old_password", L.getText(etChangePasswordOldPass));
                param.put("new_password", L.getText(etChangePasswordNewPass));
                L.l(ChangePasswordActivity.this, "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                L.t_long("Password changed successfully");
                new DBHelper().deleteUser();
                new MySharedPreference(this).setUserLogin(false);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                L.t(jsonObject.getString("message"));
            }
        }
    }
}