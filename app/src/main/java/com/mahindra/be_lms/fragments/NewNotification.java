package com.mahindra.be_lms.fragments;

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
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.PostNotificationAdapter;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.Notifications;
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

import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

/**
 * Created by Dell on 2/8/2018.
 */

public class NewNotification extends Fragment implements NetworkMethod{

    @BindView(R.id.rvPostNotificationList)
    RecyclerView rvPostNotificationList;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    private ProgressDialog progressDialog;
    private PostNotificationAdapter postNotificationAdapter;
    private ArrayList<Notifications> notificationses;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_notification_post_layout, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    public void init(){
        callApi();
        notificationses = new ArrayList<>();
        rvPostNotificationList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPostNotificationList.setHasFixedSize(true);
        postNotificationAdapter = new PostNotificationAdapter(getActivity(), notificationses);
        rvPostNotificationList.setAdapter(postNotificationAdapter);
    }

    private void callApi() {

        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvPostNotificationList.setVisibility(View.VISIBLE);
            }
            progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_notificationlist&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvPostNotificationList.setVisibility(View.GONE);
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
                            rvPostNotificationList.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i = 0 ; i<jsonArray.length();i++){
                                Notifications notifications = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Notifications.class);
                                notificationses.add(notifications);
                            }
                            if (notificationses.size() > 0) {
                                postNotificationAdapter.setNotificationList(notificationses);
                                postNotificationAdapter.notifyDataSetChanged();
                            } else {
                                tvNoRecordFound.setVisibility(View.VISIBLE);
                                rvPostNotificationList.setVisibility(View.GONE);
                            }

                        }else{
                           tvNoRecordFound.setVisibility(View.VISIBLE);
                            rvPostNotificationList.setVisibility(View.GONE);
                        }

                    }else{
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        rvPostNotificationList.setVisibility(View.GONE);
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
                }else{

                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    rvPostNotificationList.setVisibility(View.GONE);

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
