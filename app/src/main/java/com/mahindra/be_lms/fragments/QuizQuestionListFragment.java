package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.QuestionMultiViewTypeAdapter;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuestionListModel;
import com.mahindra.be_lms.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 10/9/2017.
 */

public class QuizQuestionListFragment  extends Fragment {
    private static final String TAG = QuizQuestionListFragment.class.getSimpleName();
    @BindView(R.id.quiz_question_list)
    RecyclerView quiz_question_list;
    private MainActivity mainActivity;
    private QuestionMultiViewTypeAdapter questionMultiViewTypeAdapter;

    public QuizQuestionListFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.qiuz_question, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);


//        if (L.isNetworkAvailable(getActivity())) {
//
//            L.pd(getString(R.string.dialog_please_wait), getActivity());
//            request(Constants.BE_LMS_ROOT_URL+"fbd8f6920e108fced1866c0b7d509715bc1bbfc0&wsfunction=moodle_enrol_get_users_courses&userid=3&moodlewsrestformat=json");
//
//        } else {
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .show();
//        }

        ArrayList<QuestionListModel> list= new ArrayList<>();

        list.add(new QuestionListModel(QuestionListModel.T_F_TYPE,"Q. 1 "+"Answer True or false?",R.drawable.ic_launcher));
        list.add(new QuestionListModel(QuestionListModel.RADIO_TYPE,"Q. 2 "+"Hi again. Another cool image here. Which one is better?",R.drawable.ic_launcher));
        list.add(new QuestionListModel(QuestionListModel.CK_BOX_TYPE,"Q. 3 "+"Select multiple option Answer?",R.drawable.ic_launcher));
        list.add(new QuestionListModel(QuestionListModel.DESC_TYPE,"Q. 4 "+"Description type answer?",R.drawable.ic_launcher));
        QuestionMultiViewTypeAdapter adapter = new QuestionMultiViewTypeAdapter(list,getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        //RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        quiz_question_list.setLayoutManager(linearLayoutManager);
        quiz_question_list.setItemAnimator(new DefaultItemAnimator());
        quiz_question_list.setAdapter(adapter);

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

}
