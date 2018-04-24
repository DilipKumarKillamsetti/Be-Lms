package com.mahindra.be_lms.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.SurveyAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.model.Survey;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizResultFeedback extends BaseActivity implements Callback, NetworkMethod {
    @BindView(R.id.tvSurveyFeedbackFragmentRecordNotFound)
    TextView tvSurveyFeedbackFragmentRecordNotFound;
    @BindView(R.id.rvSurveyFeedbackFragment)
    RecyclerView rvSurveyFeedbackFragment;
    private SurveyAdapter surveyAdapter;
    private List<Survey> surveyList;
    private String feedback_type = "";
    private String course_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_feedback);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        try {
            feedback_type = getIntent().getStringExtra("feedback_type");
            L.l(this, "Extrasz feedbcak_type: " + feedback_type);
            course_id = getIntent().getStringExtra("courseid");
            L.l(this, "Extraz course id: " + course_id);
            surveyList = new ArrayList<>();
            rvSurveyFeedbackFragment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvSurveyFeedbackFragment.setHasFixedSize(true);
            rvSurveyFeedbackFragment.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            surveyAdapter = new SurveyAdapter(this, surveyList);
            rvSurveyFeedbackFragment.setAdapter(surveyAdapter);
            surveyAdapter.setmCallback(this);
            if (NetworkUtil.getConnectivityStatus(this) != 0) {
                request(Constants.LMS_URL);
            } else {
                L.t_long(getString(R.string.err_network_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void myCallback(int position) {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            int ansCount = 0;
            JSONArray questionsArray = new JSONArray();
            for (Survey survey : surveyAdapter.surveyList) {
                if (!survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
                    ansCount += TextUtils.isEmpty(survey.getUser_answer()) ? 0 : 1;
                }
                JSONObject question = new JSONObject();
                try {
                    question.put("question", survey.getSurvey_id());
                    if (survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
                        L.l(this, "MCQ final ANS ARRAY: " + survey.getMcq_answer().toString());
                        JSONArray final_answer = new JSONArray();
                        int j = 0;
                        for (int i = 0; i < survey.getMcq_answer().length(); i++) {
                            L.checkNull(survey.getMcq_answer().get(i).toString(), "");
                            if (!TextUtils.isEmpty(survey.getMcq_answer().get(i).toString())) {
                                L.l(this, "New MCQ ANS Position: " + j);
                                final_answer.put(j, survey.getMcq_answer().get(i).toString());
                                j = j + 1;
                            }
                        }
                        L.l(this, "MCQ USER final ANS ARRAy: " + final_answer.toString());
                        ansCount += final_answer.length() > 0 ? 1 : 0;
                        question.put("answer", final_answer.toString());
                    } else {
                        question.put("answer", L.checkNull(survey.getUser_answer(), ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                questionsArray.put(question);
            }
            L.l("ANS COUNT " + ansCount);
            if (ansCount > 0) {
                requestSaveFeedback(Constants.LMS_URL, questionsArray.toString());
            } else {
                L.t_long("Give at least on question answer");
            }
        } else {
            L.t_long(getString(R.string.err_network_connection));
        }
    }

    @Override
    public void myCallback(int position, String tag) {
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    @Override
    public void request(String url) {
        L.pd("Fetching data", "Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(QuizResultFeedback.this, "Survey RESPONSE: " + response);
                try {
                    updateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(QuizResultFeedback.this, "VOLLEY ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //action=get_survey_question
                params.put("action", Constants.FETCH_QUIZ_RESULT_FEEDBACK_ACTION);
                params.put("type", feedback_type);
//                params.put("type", Constants.QUESTION_TYPE_POST_FEEDBACK);
                L.l(QuizResultFeedback.this, "FEEDBACK PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                L.l(this, "DATA JSONARRAY: " + jsonArray.toString());
                if (jsonArray.length() > 0) {
                    surveyList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Survey survey = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Survey.class);
                        L.l(this, "Surevy OBject: " + survey.toString());
                        if (survey.getSurvey_question_type().equalsIgnoreCase("MCQ")) {
                            L.l(this, "MCQ TYPE");
                            for (int j = 0; j < survey.getOptionArrayList().size(); j++) {
                                survey.getMcq_answer().put(j, "");
                            }
                        }
                        surveyList.add(survey);
                    }
                    surveyAdapter.surveyList = surveyList;
                    surveyAdapter.notifyDataSetChanged();
                }
            } else {
                L.t_long(jsonObject.getString("message"));
            }
        }
    }

    public void requestSaveFeedback(String url, final String questions) {
        L.pd("Save Feedback", "Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(QuizResultFeedback.this, "SAVE Survey RESPONSE: " + response);
                try {
                    updateDisplaySaveFeedback(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(QuizResultFeedback.this, "SAVE VOLLEY ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //action=get_survey_question
                params.put("action", Constants.SAVE_FEEDBACK_ACTION);
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("question_ans", questions);
                params.put("courseid", course_id);
                params.put("type", feedback_type);
//                params.put("type","quiz_result_post_feedback");
                L.l(QuizResultFeedback.this, "SAVE Survey PARAM: " + params);
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplaySaveFeedback(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                finish();
                L.t_long(jsonObject.getString("message"));
            } else {
                L.t_long(jsonObject.getString("message"));
            }
        }
    }
}
