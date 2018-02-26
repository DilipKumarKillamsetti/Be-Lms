package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.lib.L;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 1/30/2018.
 */

public class HomeMenuFragment extends Fragment {

    @BindView(R.id.menuBar_dashboard)
    BottomBar menuBar_dashboard;
    @BindView(R.id.contentContainer_home)
    FrameLayout contentContainer_home;
    private DashboardActivity mainActivity;

    public HomeMenuFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_menu, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
       // mainActivity.setDrawerEnabled(false);
        menuBar_dashboard.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.excellence_update:
                        //setTitle(getString(R.string.tab_technical_upload_new));
                        replaceFrgament(new NewsAndUpdateFragment());
                        break;
                    case R.id.tab_view_learning_hub:
                       // setTitle(getString(R.string.tab_technical_upload_by_me));
                        replaceFrgament(new LearningHubFragment());
                        break;
                    case R.id.tab_technical_upload_other:
                        //setTitle(getString(R.string.tab_technical_upload_by_other));
                        replaceFrgament(new TechnicalDocumentByOtherFragment());
                        break;
                }
            }
        });
        menuBar_dashboard.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.excellence_update:
                        //setTitle(getString(R.string.tab_technical_upload_new));
                        replaceFrgament(new NewsAndUpdateFragment());
                        break;
                    case R.id.tab_view_learning_hub:
                       // setTitle(getString(R.string.tab_technical_upload_by_me));
                        replaceFrgament(new LearningHubFragment());
                        break;
                    case R.id.tab_technical_upload_other:
                        //setTitle(getString(R.string.tab_technical_upload_by_other));
                        replaceFrgament(new TechnicalDocumentByOtherFragment());
                        break;
                }
            }
        });
    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
