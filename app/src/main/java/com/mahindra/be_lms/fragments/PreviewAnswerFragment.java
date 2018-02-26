package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.PreviewAdapter;
import com.mahindra.be_lms.adapter.QuizListAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.PreviewModel;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/13/2017.
 */

public class PreviewAnswerFragment extends Fragment implements Callback,NetworkMethod {
    private static final String TAG = PreviewAnswerFragment.class.getSimpleName();
    @BindView(R.id.rvQuestionPreviewList)
    RecyclerView rvQuestionPreviewList;
    private PreviewAdapter previewAdapter;
    private MainActivity mainActivity;
    private ArrayList<PreviewModel> previewModels;
    private String attemptId;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;

    @Override
    public void myCallback(int position) {

    }

    @Override
    public void myCallback(int position, String tag) {

    }

    @Override
    public void request(String url) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("Status").equalsIgnoreCase("Success")){
                                JSONArray jsonArrayQuestions = response.getJSONArray("questions");
                                for(int i = 0 ; i<jsonArrayQuestions.length();i++){
                                    PreviewModel previewModel = new Gson().fromJson(String.valueOf(jsonArrayQuestions.get(i)), PreviewModel.class);
                                    previewModels.add(previewModel);
                                }
                                if (previewModels.size() > 0) {
                                    previewAdapter.setPreviewList(previewModels);
                                    previewAdapter.notifyDataSetChanged();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);

    }

    public PreviewAnswerFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        try {
            view = inflater.inflate(R.layout.preview_fragment, container, false);
            ButterKnife.bind(this, view);
            setHasOptionsMenu(false);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        attemptId = getArguments().getString("id");
        if (L.isNetworkAvailable(getActivity())) {

            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_quiz_get_attempt_review&attemptid="+attemptId+"&page=-1&moodlewsrestformat=json");

        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
        previewModels = new ArrayList<>();

        rvQuestionPreviewList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvQuestionPreviewList.setHasFixedSize(true);
        previewAdapter = new PreviewAdapter(getActivity(),previewModels);
        previewAdapter.setmCallback(this);
        rvQuestionPreviewList.setAdapter(previewAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }
}
