package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.adapter.FeedBackListAdapter;
import com.mahindra.be_lms.adapter.MyTrainingAdapter;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.NominatedEvent;
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

public class MytrainingFragment extends Fragment implements NetworkMethod {

    @BindView(R.id.rvTrainigFragment)
    RecyclerView rvTrainigFragment;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    private DashboardActivity mainActivity;
    ArrayList<NominatedEvent> nominatedEvents;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    public MyTrainingAdapter myTrainingAdapter;
    private ProgressDialog progressDialog;

    public MytrainingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_training, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {

        callApi();
        nominatedEvents = new ArrayList<>();
        rvTrainigFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvTrainigFragment.setHasFixedSize(true);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    public void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvTrainigFragment.setVisibility(View.VISIBLE);
            }
            progressDialog = new CustomProgressDialog(getActivity(), "");
            progressDialog.show();
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_mynomination&userid=" + MyApplication.mySharedPreference.getUserId() + "&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvTrainigFragment.setVisibility(View.GONE);
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
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    JSONObject jsonObject = response.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            tvNoRecordFound.setVisibility(View.GONE);
                            rvTrainigFragment.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("Nominated_events");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                NominatedEvent nominatedEvent = new Gson().fromJson(String.valueOf(jsonArray.get(i)), NominatedEvent.class);
                                nominatedEvents.add(nominatedEvent);
                            }
                            if (nominatedEvents.size() > 0) {
                                myTrainingAdapter = new MyTrainingAdapter(getActivity(), nominatedEvents);
                                rvTrainigFragment.setAdapter(myTrainingAdapter);
                            } else {
                                tvNoRecordFound.setVisibility(View.VISIBLE);
                                rvTrainigFragment.setVisibility(View.GONE);
                            }

                        } else {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            rvTrainigFragment.setVisibility(View.GONE);
                        }

                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        rvTrainigFragment.setVisibility(View.GONE);
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

        });

        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }
}
