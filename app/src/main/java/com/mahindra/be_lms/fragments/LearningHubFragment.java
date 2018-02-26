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
 * Created by Dell on 2/1/2018.
 */

public class LearningHubFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ManualBulletinsFragment";
    @BindView(R.id.ll_tc)
    LinearLayout ll_tc;
    @BindView(R.id.ll_el)
    LinearLayout ll_el;
    @BindView(R.id.ll_tys)
    LinearLayout ll_tys;
    @BindView(R.id.ll_fss)
    LinearLayout ll_fss;
//    @BindView(R.id.ll_tr)
//    LinearLayout ll_tr;
    @BindView(R.id.ll_mlc)
    LinearLayout ll_mlc;
    private DashboardActivity dashboardActivity;
    public LearningHubFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learning_hub, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){

        ll_el.setOnClickListener(this);
        ll_fss.setOnClickListener(this);
        ll_tc.setOnClickListener(this);
        //ll_tr.setOnClickListener(this);
        ll_tys.setOnClickListener(this);
        ll_mlc.setOnClickListener(this);

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

            case R.id.ll_tc:

               replaceFrgament(new TrainingCalendarFragment());

                break;

            case R.id.ll_el:

                replaceFrgament(new CourseListFragmentELearning());

                break;

            case R.id.ll_tys:
                replaceFrgament(new CourseListFragment());
                break;

            case R.id.ll_fss:

                replaceFrgament(new SurveyFeedbackFragment());

                break;

//            case R.id.ll_tr:
//                break;

            case R.id.ll_mlc:

                replaceFrgament(new MyLearningCurve());

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
