package com.mahindra.be_lms.fragments;

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
import com.mahindra.be_lms.adapter.MultiViewTypeAdapter;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.model.Model;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by MEDHVI-CONT on 10/4/2017.
 */

public class SurveyFeedbackDetailFragment extends Fragment implements NetworkMethod {

    private MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vinayak_fragment_survey_feedback, container, false);
        ButterKnife.bind(this, view);
//        mainActivity.setDrawerEnabled(false);
//        mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ArrayList<Model> list = new ArrayList<>();
        list.add(new Model(Model.TEXT_TYPE, "Hello. This is the Text-only View Type. Nice to meet you", 0));
        list.add(new Model(Model.IMAGE_TYPE, "Hi. I display a cool image too besides the omnipresent TextView.", R.drawable.ic_launcher));
     //   list.add(new Model(Model.AUDIO_TYPE, "Hey. Pressing the FAB button will playback an audio file on loop.", R.raw.sound));


        MultiViewTypeAdapter adapter = new MultiViewTypeAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void request(String url) {

    }

}
