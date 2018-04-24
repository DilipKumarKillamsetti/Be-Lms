package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.PreviewAdapter;
import com.mahindra.be_lms.adapter.QuestionAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.PreviewModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/14/2017.
 */

public class QuestionActivity extends BaseActivity implements NetworkMethod,Callback {
    private Intent intent;
    private String attemptId;
    private String state;
    private QuestionAdapter questionAdapter;
    private ArrayList<PreviewModel> questionModels;
    @BindView(R.id.rvquestionResponse)
    RecyclerView rvquestionResponse;
    @BindView(R.id.prevText)
    TextView prevText;
    @BindView(R.id.nextText)
    TextView nextText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        intent = getIntent();
        attemptId = intent.getStringExtra("attemptId");
        state = intent.getStringExtra("state");
        nextText.setPaintFlags(nextText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        prevText.setPaintFlags(prevText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        if(state.equalsIgnoreCase("continue")){
            if (L.isNetworkAvailable(this)) {
                L.pd(getString(R.string.dialog_please_wait), this);
                request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_quiz_get_attempt_data&attemptid="+attemptId+"&page=0&moodlewsrestformat=json");
            } else {
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .show();
            }
        }else{
        }
        questionModels = new ArrayList<>();
        rvquestionResponse.setLayoutManager(new LinearLayoutManager(QuestionActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvquestionResponse.setHasFixedSize(true);
        questionAdapter = new QuestionAdapter(QuestionActivity.this,questionModels);
        questionAdapter.setmCallback(QuestionActivity.this);
        rvquestionResponse.setAdapter(questionAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(questionModels.size() == 0 || questionModels.size() == 0 ){
            nextText.setVisibility(View.GONE);
            prevText.setVisibility(View.GONE);
        }else{
            nextText.setVisibility(View.VISIBLE);
            prevText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void request(String url) {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("Status").equalsIgnoreCase("Success")){
                                JSONArray jsonArrayQuestions = response.getJSONArray("questions");
                                for(int i = 0 ; i<jsonArrayQuestions.length();i++){
                                    PreviewModel previewModel = new Gson().fromJson(String.valueOf(jsonArrayQuestions.get(i)), PreviewModel.class);
                                    questionModels.add(previewModel);
                                }
                                if (questionModels.size() > 0) {
                                    questionAdapter.setQuestionList(questionModels);
                                    questionAdapter.notifyDataSetChanged();
                                } else {
                                   // tvNoRecordFound.setVisibility(View.VISIBLE);
                                }

                            }else{
                                //tvNoRecordFound.setVisibility(View.VISIBLE);

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
                L.l(QuestionActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(QuestionActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);

    }

    @Override
    public void myCallback(int position) {

    }

    @Override
    public void myCallback(int position, String tag) {

    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }

}
