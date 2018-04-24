package com.mahindra.be_lms.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.MultiViewTypeAdapter;
import com.mahindra.be_lms.model.Model;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by medhvi-cont on 9/26/2017.
 */

public class NewSurveyFeedbackFragmentList  extends Fragment{

    public static final String TAG = SurveyFeedbackFragment.class.getSimpleName();
    @BindView(R.id.tvSurveyFeedbackFragmentRecordNotFound)
    TextView tvSurveyFeedbackFragmentRecordNotFound;
    //    @BindView(R.id.rvSurveyFeedbackFragment)
//    RecyclerView rvSurveyFeedbackFragment;
    @BindView(R.id.wvSurveyFeedback)
    WebView wvSurveyFeedback;
    @BindView(R.id.pbSurveyFeedback)
    ProgressBar pbSurveyFeedback;
    private MainActivity mainActivity;
    private String html_url;
    private boolean isresultSubmit = false;
    private ProgressDialog progressDialog;
    public NewSurveyFeedbackFragmentList(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.vinayak_fragment_survey_feedback, container, false);
        ButterKnife.bind(this, view);
        mainActivity.setDrawerEnabled(false);


        ArrayList<Model> list = new ArrayList<>();
        list.add(new Model(Model.TEXT_TYPE, "Hello. This is the Text-only View Type. Nice to meet you", 0));
        list.add(new Model(Model.IMAGE_TYPE, "Hi. I display a cool image too besides the omnipresent TextView.", R.drawable.ic_launcher));
//        list.add(new Model(Model.AUDIO_TYPE, "Hey. Pressing the FAB button will playback an audio file on loop.", R.raw.sound));
        list.add(new Model(Model.IMAGE_TYPE, "Hi again. Another cool image here. Which one is better?", R.drawable.arrow_right));

        MultiViewTypeAdapter adapter = new MultiViewTypeAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        return view;
    }

}
