package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.DBHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/1/2018.
 */

public class MenuFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ManualBulletinsFragment";
    @BindView(R.id.ll_eu)
    LinearLayout ll_eu;
    @BindView(R.id.ll_lh)
    LinearLayout ll_lh;
    @BindView(R.id.ll_not)
    LinearLayout ll_not;
    @BindView(R.id.ll_ibms)
    LinearLayout ll_ibms;
//    @BindView(R.id.ll_tr)
//    LinearLayout ll_tr;
    @BindView(R.id.ll_help)
    LinearLayout ll_help;
    @BindView(R.id.ll_logout)
    LinearLayout ll_logout;
    private DashboardActivity dashboardActivity;
    public MenuFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){

        ll_eu.setOnClickListener(this);
        ll_lh.setOnClickListener(this);
        ll_not.setOnClickListener(this);
        //ll_tr.setOnClickListener(this);
        ll_ibms.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_logout.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ll_eu:

              // replaceFrgament(new NewsAndUpdateFragment());
                dashboardActivity.navigationTabBar.setModelIndex(0, true);

                break;

            case R.id.ll_lh:

             //   replaceFrgament(new LearningHubFragment());
              dashboardActivity.navigationTabBar.setModelIndex(1, true);

                break;

            case R.id.ll_not:
                //replaceFrgament(new CourseListFragment());
                dashboardActivity.navigationTabBar.setModelIndex(3, true);
                break;

            case R.id.ll_ibms:

               // replaceFrgament(new SurveyFeedbackFragment());

                break;

//            case R.id.ll_tr:
//                break;

            case R.id.ll_help:

//                replaceFrgament(new MyLearningCurve());

                break;

            case R.id.ll_logout:

                                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.dialog_logout_msg))
                        .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                DBHelper dbHelper = new DBHelper();
                                dbHelper.clearUserData();
                                MyApplication.mySharedPreference.setUserLogin(false);
                                MyApplication.mySharedPreference.setNotificationType("");
                                WebViewCachePref.newInstance(getActivity()).clearAllPref();
                                startActivity(new Intent(getActivity(), LoginActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            }
                        }).setNegativeButton(getString(R.string.dialog_no), null).show();


                break;

            default:
                break;

        }
    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

}
