package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.util.DBHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisclaimerActivity extends BaseActivity implements View.OnClickListener {
//    private CheckBox ch1;
//    private Button btn_next;
    private boolean fromMainActivity;
    @BindView(R.id.btnAccept)
    Button btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        ButterKnife.bind(this);
       // getSupportActionBar().hide();
        fromMainActivity = getIntent().getBooleanExtra("fromMainActivity", false);
        initComp();
//        if (MyApplication.mySharedPreference.getDisclaimerACCEPT()) {
//            ch1.setVisibility(View.GONE);
//            btn_next.setVisibility(View.GONE);
//        } else {
//            ch1.setVisibility(View.VISIBLE);
//            btn_next.setVisibility(View.VISIBLE);
//        }
        //TextView tv_disc_cont = (TextView) findViewById(R.id.tv_disc_cont);
        //tv_disc_cont.setText(Html.fromHtml(getResources().getString(
          //      R.string.stringtxt_dislaimer)));
    }

    public void initComp() {
        btnAccept.setOnClickListener(this);
      //  ch1 = (CheckBox) findViewById(R.id.cb_disclaimer);
        //btn_next = (Button) findViewById(R.id.btnDisclaimerNext);
     //  btn_next.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnAccept:

                Intent i = new Intent(DisclaimerActivity.this,DashboardActivity.class);
                startActivity(i);

                break;
            default:
                break;
        }

//        if (ch1.isChecked()) {
//
//            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, true);
//            Intent intent = new Intent(DisclaimerActivity.this,
//                    SafetyAndWarningActivity.class);
//            finish();
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(this, "Please accept the condition...",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onBackPressed() {
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            DisclaimerActivity.this.finish();
        } else {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearUserData();
            MyApplication.mySharedPreference.setUserLogin(false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
            finish();
            startActivity(new Intent(DisclaimerActivity.this, LoginActivity.class));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin() && fromMainActivity) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
}
