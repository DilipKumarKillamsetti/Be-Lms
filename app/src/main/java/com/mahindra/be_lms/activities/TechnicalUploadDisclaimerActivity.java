package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mahindra.be_lms.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TechnicalUploadDisclaimerActivity extends BaseActivity {
    //TextView tv_disc_cont;
    @BindView(R.id.sc_technicalDisclaimer)
    ScrollView scrollView;
    @BindView(R.id.tv_technical_disclaimer)
    TextView tv_technical_disclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer_techincal_upload);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        tv_technical_disclaimer.setText(Html.fromHtml(getResources().getString(
                R.string.stringtxt_dislaimer_tech_upload)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        TechnicalUploadDisclaimerActivity.this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
