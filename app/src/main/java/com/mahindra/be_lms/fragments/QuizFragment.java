package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.QuizAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.Course;
import com.mahindra.be_lms.model.Quiz;
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

/**
 * A simple {@link Fragment} subclass.
 * Created By Chaitali Chavan
 * Updated By Chaitali Chavan on 15/11/2016
 */
public class QuizFragment extends Fragment implements Callback, NetworkMethod {
    public static final String TAG = QuizFragment.class.getSimpleName();
    @BindView(R.id.rv_quiz)
    RecyclerView rv_quiz;
    private MainActivity mainActivity;
    private List<Quiz> quizList;
    private Course course;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            course = bundle.getParcelable(getString(R.string.course_parcelable_tag));
            assert course != null;
            L.l("Course: " + course.toString());
        }
        // quizList= DataProvider.getDummyQuizList();
        rv_quiz.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_quiz.setHasFixedSize(false);
        rv_quiz.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        try {
            if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
                request(Constants.LMS_URL);
            } else {
                L.t_long(getString(R.string.err_network_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.quiz_parcelable_tag), quizList.get(position));
        QuizMasterFragment quizMasterFragment = new QuizMasterFragment();
        quizMasterFragment.setArguments(bundle);
        mainActivity.replaceFrgament(quizMasterFragment);
    }

    @Override
    public void myCallback(int position, String tag) {

    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }



    @Override
    public void request(String url) {
        L.pd("Fetching Quiz Details", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "GET TEST QUIZ RESPONSE: " + response);
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
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.ACTION_GET_QUIZ_DOC);
                params.put("courseid", course.getId());
                params.put("type", "quiz");
                L.l(TAG, "GET TEST QUIZ PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
          /*  {
  "courseid": "352",
  "message": "Success",
  "quiz": [
    {
      "quizid": "128",
      "name": "Champion Of Champion Entrance",
      "linkurl": "mod/quiz/view.php?id=128"
    },
    {
      "quizid": "129",
      "name": "Q1",
      "linkurl": "mod/quiz/view.php?id=129"
    }
  ]
}*/
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("message").equalsIgnoreCase("Success")) {
                L.l(TAG, "Course ID: " + jsonObject.getString("courseid"));
                JSONArray quiz_jsonArray = jsonObject.getJSONArray("quiz");
                L.l(TAG, "QUIZ JSONARRAY: " + quiz_jsonArray.toString());
                quizList = new ArrayList<>();
                for (int i = 0; i < quiz_jsonArray.length(); i++) {
                    Quiz quiz = new Quiz();
                    quiz.setId(quiz_jsonArray.getJSONObject(i).getString("quizid"));
                    quiz.setQuiz_title(quiz_jsonArray.getJSONObject(i).getString("name"));
                    quizList.add(quiz);
                }
                QuizAdapter quizAdapter = new QuizAdapter(getActivity(), quizList);
                rv_quiz.setAdapter(quizAdapter);
                quizAdapter.setmCallback(this);
            } else {
                L.t_long(jsonObject.getString("message"));
            }
        }
    }
}
