package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;

import butterknife.ButterKnife;

import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

/**
 * Created by Dell on 1/31/2018.
 */

public class PostDataFragment  extends Fragment {
    private DashboardActivity dashboardActivity;

    public PostDataFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

    }

}
