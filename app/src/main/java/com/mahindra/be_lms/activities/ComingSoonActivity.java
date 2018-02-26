package com.mahindra.be_lms.activities;

import android.os.Bundle;

import com.mahindra.be_lms.R;

public class ComingSoonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
