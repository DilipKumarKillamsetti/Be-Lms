package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.adapter.ManualInfoAdapter;
import com.mahindra.be_lms.adapter.MostViewedAdapter;
import com.mahindra.be_lms.constant.FileType;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.model.ManualInfoModel;
import com.mahindra.be_lms.model.MostView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostViewWebViewFragment extends Fragment implements NetworkMethod {
    private static final String TAG = "MostViewWebViewFragment";
    private static final String ACTION = "action";
    @BindView(R.id.rvMostViewedByMeList)
    RecyclerView rvMostViewedByMeList;
    private String action;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    private MostViewedAdapter mostViewedAdapter;
    private MainActivity mainActivity;
    private ArrayList<MostView> mostViews;
    private String download_url;
    private DownloadManager downloadManager;

    public MostViewWebViewFragment() {
        // Required empty public constructor
    }

    public static MostViewWebViewFragment newInstance(String action) {
        MostViewWebViewFragment fragment = new MostViewWebViewFragment();
        Bundle args = new Bundle();
        args.putString(ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            action = getArguments().getString(ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_view_web_view, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

        mainActivity.setDrawerEnabled(false);
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if (L.isNetworkAvailable(getActivity())) {

            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_mymostviewed&moodlewsrestformat=json");

        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
        mostViews = new ArrayList<>();

        rvMostViewedByMeList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMostViewedByMeList.setHasFixedSize(true);
        //rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mostViewedAdapter = new MostViewedAdapter(getActivity(),mostViews);
        //mostViewedAdapter.setMyCallback(getActivity());
        rvMostViewedByMeList.setAdapter(mostViewedAdapter);


    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void request(String url) { JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
        @Override
        public void onResponse(JSONArray response) {

            Log.e("MostVi API Response===",response.toString());
            try {
                JSONObject jsonObject = response.getJSONObject(0);
                if(jsonObject.getString("Status").equalsIgnoreCase("Success")){

                    JSONArray jsonArray = jsonObject.getJSONArray("mostviewed");

                    for(int i = 0 ; i<jsonArray.length();i++){

                        JSONObject modulesObject = (JSONObject) jsonArray.get(i);

                        //mostViews.add(manualInfoModel);
                    }
                    if (mostViews.size() > 0) {
                        mostViewedAdapter.setVievedList(mostViews);
                        mostViewedAdapter.notifyDataSetChanged();
                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                    }
                }else{
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            L.dismiss_pd();

//            String responseData = "{\"statusCode\":\"200\",\"result\":\"Success\",\"message\":\"Success\",\"data\":{\"id\":\"121\",\"username\":\"U000121\",\"firstname\":\"Vijay\",\"lastname\":\"Kumavat\",\"emailid\":\"v11.kumavat@yahoo.in\",\"mobileno\":\"+918149143550\",\"organization_code\":\"\",\"organization\":\"Krios\",\"picture\":\"\",\"location\":\"296\",\"designation\":\"4\",\"qualification\":\"25\",\"role\":\"Customer\",\"dob\":\"11-03-1999\",\"doj\":\"11-09-2017\",\"profiles\":\"Customer,Tester,Samriddhi\",\"groups\":\"MASL,Customer,Tester\",\"menurights\":{\"registration\":\"N\",\"search\":\"N\",\"powerolCare\":\"N\",\"mostViewed\":\"N\",\"myProfile\":\"N\",\"surveyFeedbacks\":\"N\",\"myTrainingPassport\":\"N\",\"learningTestQuizs\":\"N\",\"manualsBulletins\":\"Y\",\"trainingCalenderNomination\":\"N\",\"queriesResponse\":\"N\",\"technicalUploads\":\"N\",\"myFieldRecords\":\"N\",\"reports\":\"N\",\"manpowerEdition\":\"N\"}}}";
//            updateDisplay(responseData);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            L.dismiss_pd();
            L.l(getActivity(), "ERROR : " + error.getMessage());
            if (L.checkNull(error.getMessage())) {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText("Network problem please try again").show();
            }
        }
    }) ;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }



}
