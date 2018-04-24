package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.SurveyAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.Survey;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Created by Chaitali Chavan on 11/16/16.
 */
public class SurveyFeedbackFormFragment extends Fragment implements Callback,NetworkMethod {
    public static final String TAG = SurveyFeedbackFormFragment.class.getSimpleName();
    @BindView(R.id.tvSurveyFeedbackFragmentRecordNotFound)
    TextView tvSurveyFeedbackFragmentRecordNotFound;
        @BindView(R.id.rvSurveyFeedbackFragment)
        RecyclerView rvSurveyFeedbackFragment;
    private DashboardActivity mainActivity;
    private String html_url;
    private boolean isresultSubmit = false;
   private SurveyAdapter surveyAdapter;
    private ArrayList<Survey> surveyList;
    private  String feedbackId;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;

    public SurveyFeedbackFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_form_feedback, container, false);
        ButterKnife.bind(this, view);
        //mainActivity.setDrawerEnabled(false);
        mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            feedbackId = bundle.getString("fId");
        }
        L.l(TAG, "FINAL MAIN URL: " + html_url);
        try {
            surveyList = new ArrayList<>();
            rvSurveyFeedbackFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvSurveyFeedbackFragment.setHasFixedSize(true);
            rvSurveyFeedbackFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            surveyAdapter = new SurveyAdapter(getActivity(), surveyList);
            rvSurveyFeedbackFragment.setAdapter(surveyAdapter);
            surveyAdapter.setmCallback(this);
            callApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void callApi() {

        if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvSurveyFeedbackFragment.setVisibility(View.VISIBLE);
            }
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_get_feedback_item&feedbackid="+feedbackId+"&moodlewsrestformat=json");
        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvSurveyFeedbackFragment.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                        @Override
//                        public void onClick(PKBDialog customDialog) {
//                            customDialog.dismiss();
//                            getActivity().getSupportFragmentManager().popBackStack();
//                        }
//                    }).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    @Override
    public void request(String url) {
        L.pd("Fetching data", "Please wait", getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                L.dismiss_pd();
                L.l(TAG, "Survey RESPONSE: " + response);
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
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonArrayRequest);
    }
//
    private void updateDisplay(JSONArray response) throws JSONException {
        if (!TextUtils.isEmpty(response.toString())) {
            JSONObject jsonObject = (JSONObject) response.get(0);
            if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("feedbackitems");
                L.l(TAG, "DATA JSONARRAY: " + jsonArray.toString());
                if (jsonArray.length() > 0) {
                    surveyList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Survey survey = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Survey.class);
                        L.l(TAG, "Surevy OBject: " + survey.toString());
                        surveyList.add(survey);
                    }
                    surveyAdapter.surveyList = surveyList;
                    surveyAdapter.notifyDataSetChanged();
                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("No survey available.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }).show();
                }
            } else {
                //L.t_long(jsonObject.getString("message"));
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    @Override
    public void myCallback(int position) {

        if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
            requestSubmitFeedback(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_feedback_item_process_page&moodlewsrestformat=json");
        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            customDialog.dismiss();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }).show();
        }

    }

    private void requestSubmitFeedback(String s) {
        L.pd("Submitting", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(getActivity(), "REPLY QUERY RESPONSE: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Status").equalsIgnoreCase("Success")){

                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Survey submited successfully.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                }).show();

                    }else{
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Something went wrong. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                //L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*action = reply_on_queries
                userid = 14528
                replay_text = Query1 Reply
                        query_id = 1
*/
                params.put("feedbackid",feedbackId);
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("courseid","1");
                for (int i = 0 ; i<surveyList.size() ; i++){
                    Log.e(")))))))",surveyList.get(i).getUser_answer());
                    params.put("responses["+i+"][name]",surveyList.get(i).getSurvey_question_type()+"_"+surveyList.get(i).getSurvey_id());
                    params.put("responses["+i+"][value]",surveyList.get(i).getUser_answer());
                }
                Log.e("params--",params.toString());


                //L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public void myCallback(int position, String tag) {
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


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

    private void resetWebview(WebView webView) {
        //Clearing the WebView
        try {
            webView.stopLoading();
        } catch (Exception e) {
            L.l(TAG, "WebView Error: " + e.getMessage());
        }
        try {
            webView.clearView();
        } catch (Exception e) {
            L.l(TAG, "WebView Error: " + e.getMessage());
        }
        if (webView.canGoBack()) {
            webView.goBack();
        }
        webView.loadUrl("about:blank");
        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                .setContentText("Web page not available. Try again later.")
                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                    @Override
                    public void onClick(PKBDialog customDialog) {
                        customDialog.dismiss();
                        mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mainActivity.replaceFrgament(new HomeFragment());
                    }
                }).show();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            L.l(TAG, "NEW URL: " + url);
            L.l(TAG, "issubmittedSurvey: " + isresultSubmit);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php")) {
                resetWebview(view);
            } else if (url.equals(Constants.LMS_SURVEY_SUBMIT_LINK) && !isresultSubmit) {
                isresultSubmit = true;
                view.loadUrl(url);
              /*  new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText("Survey submited successfully.")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                *//*mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                mainActivity.replaceFrgament(new HomeFragment());*//*
                                view.loadUrl(url);
                            }
                        }).show();*/
            } else {
                if (url.contains(html_url)) {
                    isresultSubmit = false;
                }
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                L.l(TAG, "IN ON RECEIVED ERROR:  " + request.getUrl());
            }
            resetWebview(view);
        }
    }
}
