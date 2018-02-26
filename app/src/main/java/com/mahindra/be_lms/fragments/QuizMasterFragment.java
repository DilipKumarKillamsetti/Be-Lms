package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.Quiz;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizMasterFragment extends Fragment implements MyCallback, NetworkMethod {

    public static final String TAG = QuizMasterFragment.class.getSimpleName();
    @BindView(R.id.lvQuizMasterFragment)
    ListView lvQuizMasterFragment;
    private MainActivity mainActivity;
    private Quiz quiz;

    public QuizMasterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_master, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            quiz = bundle.getParcelable(getString(R.string.quiz_parcelable_tag));
            assert quiz != null;
            try {
                request(Constants.LMS_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*if (quiz.getQuizQuestionsList().size() > 0) {
                final QuizQuestionAdapter adapter = new QuizQuestionAdapter(getActivity(), quiz.getQuizQuestionsList());
                lvQuizMasterFragment.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_quizmaster_btn_layout, null, false);
                lvQuizMasterFragment.addFooterView(footerView);
                Button btnSubmit = (Button) footerView.findViewById(R.id.btnQuizListMasterSubmit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int score=0;
                        for (QuizQuestion quizQuestion : adapter.quizQuestionList) {
                            if (quizQuestion.getAnswer()==quizQuestion.getSelected_answer()) {
                                //Increase score/send to server
                                score += 1;
                            }
                        }
                        L.t("Your score is: "+score);
                    }
                });
                adapter.setmCallback(this);
                try {
                    request(Constants.LMS_URL);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle(R.string.frg_quiz_title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void myCallback(int position) {

    }

    @Override
    public void request(String url) {
        /*action = get_quiz_data
        quizid = 42*/
        L.pd("Fetching data", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "QUIZ DATA RESPONSE: " + response);
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
                L.l(TAG, "VOLLEY ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.ACTION_GET_QUIZ_DATA);
                params.put("quizid", quiz.getId());
                L.l(TAG, "GET QUIZ DATA PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
       /* {
            "result": "Success",
                "quizdata": [
            {
                "slot": "171",
                    "QuestionID": "145",
                    "Question": "1'' is equal to (1 इंच के बराबर है)",
                    "QuestionType": "multichoice",
                    "QuestionName": "1'' is equal to (1 इंच के बराबर है)",
                    "attemptid": "640",
                    "maxmark": "1.0000000",
                    "rightanswer": "25.4 mm\n",
                    "behaviour": "deferredfeedback",
                    "options": [
                {
                    "optionid": "496",
                        "option": "2.54 mm"
                },
                {
                    "optionid": "497",
                        "option": "25.4 mm"
                },
                {
                    "optionid": "498",
                        "option": "254 mm"
                },
                {
                    "optionid": "499",
                        "option": "3.32 mm"
                }
                ]
            }
            ]
        }*/
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                JSONArray quiz_data_jsonArray = jsonObject.getJSONArray("quizdata");
                L.l(TAG, "QUIZ DATA JSONARRAY: " + quiz_data_jsonArray.toString());
                for (int i = 0; i < quiz_data_jsonArray.length(); i++) {
                    if (quiz_data_jsonArray.getJSONObject(i).getString("QuestionType").equalsIgnoreCase("multichoice")) {
                        JSONArray options_jsonArray = quiz_data_jsonArray.getJSONObject(i).getJSONArray("options");
                        L.l(TAG, "OPTIONS ARRAY: " + options_jsonArray.toString());
                        for (int j = 0; j < options_jsonArray.length(); j++) {
                            L.l(TAG, "OPTIONS: " + options_jsonArray.getJSONObject(i).toString());
                        }
                    }
                }
            } else {
                L.t_long(jsonObject.getString("result"));
            }
        }

    }
}
