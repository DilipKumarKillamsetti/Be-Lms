package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.lib.L;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/5/2018.
 */

public class MyLearningCurve extends Fragment implements View.OnClickListener {

    private DashboardActivity dashboardActivity;
    @BindView(R.id.ll_mytys)
    LinearLayout ll_mytys;
    @BindView(R.id.ll_myel)
    LinearLayout ll_myel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learning_curve, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void init(){
        ll_mytys.setOnClickListener(this);
        ll_myel.setOnClickListener(this);
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

            case R.id.ll_mytc:



                break;

            case R.id.ll_myel:
                replaceFrgament(new MostViewedByMeFragment());
                break;

            case R.id.ll_mytys:

                replaceFrgament(new MyTrainingPassportFragment());

                break;

            case R.id.ll_myfss:

                replaceFrgament(new SurveyFeedbackFragment());

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
