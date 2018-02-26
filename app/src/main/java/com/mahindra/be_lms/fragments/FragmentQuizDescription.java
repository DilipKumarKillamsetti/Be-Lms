package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.QuizListAdapter;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuizModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/22/2017.
 */

public class FragmentQuizDescription extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentQuizDescription.class.getSimpleName();
    @BindView(R.id.btnAttemptQuiz)
    Button btnAttemptQuiz;
    @BindView(R.id.btnLay)
    LinearLayout btnLay;
    @BindView(R.id.first_lay)
    LinearLayout first_lay;
    @BindView(R.id.password_lay)
    LinearLayout password_lay;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    private MainActivity mainActivity;


    public FragmentQuizDescription(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_description, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        btnAttemptQuiz.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: call");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {
                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
//                if (adapter != null) {
//                    Log.d(TAG, "onResume: adapternot null");
//                    queriesList = new DBHelper().getQueriesList();
//                    adapter = new QueriesAdapter(getActivity(), queriesList);
//                    adapter.setMyCallback(this);
//                    rvMyQueriesFragmentList.setAdapter(adapter);
//                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btnAttemptQuiz:

                new PKBDialog(getActivity(), PKBDialog.SUCCESS_TYPE)
                        .setContentText("The quiz has a time limit. Are you sure that you wish to start?")
                        .setCancelClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                btnLay.setVisibility(View.VISIBLE);
                                btnAttemptQuiz.setVisibility(View.GONE);
                                first_lay.setVisibility(View.GONE);
                                password_lay.setVisibility(View.VISIBLE);

//                                mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                mainActivity.replaceFrgament(new HomeFragment());
                            }
                        }).show();

                break;
            case R.id.btnContinue:
                //mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.replaceFrgament(new AttemtedAnswerFragment());
                break;
            default:
                break;

        }

    }
}
