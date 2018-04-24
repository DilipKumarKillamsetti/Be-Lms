package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.mahindra.be_lms.activities.CommentsActivityDialog;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.FeedBackListAdapter;
import com.mahindra.be_lms.adapter.QueryResponseAdapter;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.SurveyFeedback;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/28/2018.
 */

public class FeedbackStausFragment extends Fragment implements NetworkMethod {

    @BindView(R.id.rvFeedbackFragment)
    RecyclerView rvFeedbackFragment;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    private DashboardActivity mainActivity;
    ArrayList<SurveyFeedback> surveyFeedbacks;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private ProgressDialog progressDialog;
    public FeedBackListAdapter feedBackListAdapter;


    public  FeedbackStausFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback_status_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {

        callApi();
        surveyFeedbacks = new ArrayList<>();
        rvFeedbackFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvFeedbackFragment.setHasFixedSize(true);
        feedBackListAdapter = new FeedBackListAdapter(getActivity(),surveyFeedbacks);
        rvFeedbackFragment.setAdapter(feedBackListAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    public void callApi(){
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvFeedbackFragment.setVisibility(View.VISIBLE);
            }
            progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
            request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_feedbacklist&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvFeedbackFragment.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });

        }

    }

    @Override
    public void request(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //L.dismiss_pd();

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if(jsonObject.length()>0){
                        if(jsonObject.getString("Status").equalsIgnoreCase("Success")){
                            tvNoRecordFound.setVisibility(View.GONE);
                            rvFeedbackFragment.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i = 0 ; i<jsonArray.length();i++){
                                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);

                                SurveyFeedback surveyFeedback = new Gson().fromJson(String.valueOf(jsonArray.get(i)), SurveyFeedback.class);
//                                surveyFeedback.setId(jsonObject1.getString("id"));
//                                surveyFeedback.setSurveyName(jsonObject1.getString("name"));
//                                surveyFeedback.setAttemptdate(jsonObject1.getString("attemptdate"));
//                                surveyFeedback.setStatus(jsonObject1.getString("status"));
                                surveyFeedbacks.add(surveyFeedback);
                            }
                            if (surveyFeedbacks.size() > 0) {
                                feedBackListAdapter.setCourseList(surveyFeedbacks);
                                feedBackListAdapter.notifyDataSetChanged();
                            } else {
                                tvNoRecordFound.setVisibility(View.VISIBLE);
                                rvFeedbackFragment.setVisibility(View.GONE);
                            }

                        }else{
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            rvFeedbackFragment.setVisibility(View.GONE);
                        }

                    }else{
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        rvFeedbackFragment.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
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

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("action", "0");
                param.put("userid",MyApplication.mySharedPreference.getUserId());

                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);


    }
}
