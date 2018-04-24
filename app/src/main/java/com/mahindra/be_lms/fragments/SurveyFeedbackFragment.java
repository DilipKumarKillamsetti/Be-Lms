package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.SurveyFeedbackAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.model.SurveyFeedback;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mahindra.be_lms.fragments.NewSurveyFeedbackFragmentList.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Created by Chaitali Chavan on 11/16/16.
 */
public class SurveyFeedbackFragment extends Fragment implements Callback, NetworkMethod {
    @BindView(R.id.rvSurveyFeedbackFragmentList)
    RecyclerView rvSurveyFeedbackFragmentList;
    @BindView(R.id.tvSurveyFeedbackFragmentRecordNotFound)
    TextView tvSurveyFeedbackFragmentRecordNotFound;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private SurveyFeedbackAdapter courseListAdapter;
    private DashboardActivity mainActivity;
    private ArrayList<SurveyFeedback> getSurveyFeedback;
    private ProgressDialog progressDialog;

    public SurveyFeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vinayak_fragment_survey_feedback_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        initViews(view);
        setSurveyFeedback();
        return view;
    }


    private void setSurveyFeedback() {
        getSurveyFeedback = new ArrayList<>();
        courseListAdapter = new SurveyFeedbackAdapter(getActivity(), getSurveyFeedback);
        courseListAdapter.setmCallback(this);
        rvSurveyFeedbackFragmentList.setAdapter(courseListAdapter);
    }

    private void initViews(View view) {
        rvSurveyFeedbackFragmentList = (RecyclerView) view.findViewById(R.id.rvSurveyFeedbackFragmentList);

        rvSurveyFeedbackFragmentList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvSurveyFeedbackFragmentList.setHasFixedSize(true);

        callApi();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    private void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvSurveyFeedbackFragmentList.setVisibility(View.VISIBLE);
            }
           progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_feedback_get_feedbacks_by_courses&courseids[0]=1&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvSurveyFeedbackFragmentList.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
    }


    @Override
    public void myCallback(int position) {
        replaceFrgament(new SurveyFeedbackFormFragment(), getSurveyFeedback.get(position).getId());
    }

    @Override
    public void myCallback(int position, String tag) {
        if (L.isNetworkAvailable(getActivity())) {

            progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
            requestFeedbackStatus(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_feedback_completed&feedbackid=" + getSurveyFeedback.get(position).getId() + "&userid=" + MyApplication.mySharedPreference.getUserId() + "&moodlewsrestformat=json", getSurveyFeedback.get(position).getId());

        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    private void requestFeedbackStatus(String s, final String id) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, s, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if (response.getString("Status").equalsIgnoreCase("Success")) {
                        if (response.getString("completed").equalsIgnoreCase("Completed")) {
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_feedback_connection))
                                    .setConfirmText("OK")
                                    .show();
                        } else {
                            replaceFrgament(new SurveyFeedbackFormFragment(), id);
                        }
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.err_failed_connection))
                                .setConfirmText("OK")
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                L.l(getActivity(), "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
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
    public void request(String url) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.e("Course API Response===", response.toString());
                updateDisplay(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                L.l(getActivity(), "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void updateDisplay(JSONObject response) {
        try {
            if (response.getString("Status").equalsIgnoreCase("success")) {
                rvSurveyFeedbackFragmentList.setVisibility(View.VISIBLE);
                JSONArray jsonArray = response.getJSONArray("feedbacks");
                for (int i = 0; i < jsonArray.length(); i++) {

                    SurveyFeedback surveyFeedback = new Gson().fromJson(String.valueOf(jsonArray.get(i)), SurveyFeedback.class);
                    getSurveyFeedback.add(surveyFeedback);
                }

                if (getSurveyFeedback.size() > 0) {
                    courseListAdapter.setSFeedbackList(getSurveyFeedback);
                    courseListAdapter.notifyDataSetChanged();
                } else {
                    rvSurveyFeedbackFragmentList.setVisibility(View.GONE);
                    tvSurveyFeedbackFragmentRecordNotFound.setVisibility(View.VISIBLE);
                }

            } else {
                rvSurveyFeedbackFragmentList.setVisibility(View.GONE);
                tvSurveyFeedbackFragmentRecordNotFound.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            rvSurveyFeedbackFragmentList.setVisibility(View.GONE);
            tvSurveyFeedbackFragmentRecordNotFound.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }


//    private void updateDisplay(String response) throws JSONException {
//        if (!TextUtils.isEmpty(response)) {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
//                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                L.l(TAG, "DATA JSONARRAY: " + jsonArray.toString());
//                if (jsonArray.length() > 0) {
//                    surveyList = new ArrayList<>();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Survey survey = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Survey.class);
//                        L.l(TAG, "Surevy OBject: " + survey.toString());
//                        if (survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
//                            L.l(TAG, "MCQ TYPE");
//                            for (int j = 0; j < survey.getOptionArrayList().size(); j++) {
//                                survey.getMcq_answer().put(j, "");
//                            }
//                        }
//                        surveyList.add(survey);
//                    }
//                    surveyAdapter.surveyList = surveyList;
//                    surveyAdapter.notifyDataSetChanged();
//                } else {
//                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                            .setContentText("No survey available.")
//                            .setConfirmText("OK")
//                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                                @Override
//                                public void onClick(PKBDialog customDialog) {
//                                    customDialog.dismiss();
//                                    getActivity().getSupportFragmentManager().popBackStack();
//                                }
//                            }).show();
//                }
//            } else {
//                //L.t_long(jsonObject.getString("message"));
//                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                        .setContentText(jsonObject.getString("message"))
//                        .setConfirmText("OK")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                            }
//                        }).show();
//            }
//        }
//    }

//    @Override
//    public void myCallback(int position) {
//        if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
//            int ansCount = 0;
//            JSONArray questionsArray = new JSONArray();
//            for (Survey survey : surveyAdapter.surveyList) {
//                if (!survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
//                    ansCount += TextUtils.isEmpty(survey.getUser_answer()) ? 0 : 1;
//                }
//                JSONObject question = new JSONObject();
//                try {
//                    question.put("question", survey.getSurvey_id());
//                    if (survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
//                        L.l(TAG, "MCQ final ANS ARRAY: " + survey.getMcq_answer().toString());
//                        JSONArray final_answer = new JSONArray();
//                        int j = 0;
//                        for (int i = 0; i < survey.getMcq_answer().length(); i++) {
//                            L.checkNull(survey.getMcq_answer().get(i).toString(), "");
//                            if (!TextUtils.isEmpty(survey.getMcq_answer().get(i).toString())) {
//                                L.l(TAG, "New MCQ ANS Position: " + j);
//                                final_answer.put(j, survey.getMcq_answer().get(i).toString());
//                                j = j + 1;
//                            }
//                        }
//                        L.l(TAG, "MCQ USER final ANS ARRAy: " + final_answer.toString());
//                        ansCount += final_answer.length() > 0 ? 1 : 0;
//                        question.put("answer", final_answer.toString());
//                    } else {
//                        question.put("answer", L.checkNull(survey.getUser_answer(), ""));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                questionsArray.put(question);
//            }
//            L.l("ANS COUNT " + ansCount);
//            if (ansCount > 0) {
//                requestSaveFeedback(Constants.LMS_URL, questionsArray.toString());
//            } else {
//                // L.t_long("Give at least one question answer");
//                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                        .setContentText(getString(R.string.err_msg_survey_give_atleast_one_question))
//                        .setConfirmText("OK")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                            }
//                        }).show();
//            }
//        } else {
//            //L.t_long(getString(R.string.err_network_connection));
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                        @Override
//                        public void onClick(PKBDialog customDialog) {
//                            customDialog.dismiss();
//                        }
//                    }).show();
//        }
//    }
//
//    @Override
//    public void myCallback(int position, String tag) {
//    }
//
//    public void requestSaveFeedback(String url, final String questions) {
//        L.pd("Save Feedback", "Please wait", getActivity());
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                L.dismiss_pd();
//                L.l(TAG, "SAVE Survey RESPONSE: " + response);
//                try {
//                    updateDisplaySaveFeedback(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                L.dismiss_pd();
//                L.l(TAG, "SAVE VOLLEY ERROR: " + error.getMessage());
//                if (L.checkNull(error.getMessage())) {
//                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                            .setContentText("Network problem please try again").show();
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                //action=get_survey_question
//                params.put("action", Constants.SAVE_FEEDBACK_ACTION);
//                params.put("userid", new MySharedPreference(getActivity()).getUserId());
//                params.put("question_ans", questions);
//                L.l(TAG, "SAVE Survey PARAM: " + params);
//                return VolleySingleton.checkRequestparam(params);
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
//    }
//
//    private void updateDisplaySaveFeedback(String response) throws JSONException {
//        if (!TextUtils.isEmpty(response)) {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
//                //  L.t_long(jsonObject.getString("message"));
//                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
//                        .setContentText(getString(R.string.otp_send_on_new_mobile))
//                        .setCustomImage(R.drawable.success_circle)
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                                mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                mainActivity.replaceFrgament(new HomeFragment());
//                            }
//                        }).show();
//
//            } else {
//                // L.t_long(jsonObject.getString("message"));
//                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                        .setContentText(jsonObject.getString("message"))
//                        .setConfirmText("OK")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                            }
//                        }).show();
//            }
//        }
//    }


    public void replaceFrgament(Fragment fragment, String id) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        Bundle args = new Bundle();
        args.putString("fId", id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}
