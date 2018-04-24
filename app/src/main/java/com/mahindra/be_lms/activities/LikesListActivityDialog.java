package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mahindra.be_lms.adapter.LikesListAdapter;
import com.mahindra.be_lms.adapter.QueryResponseAdapter;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.LikesListResponse;
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
 * Created by Dell on 2/22/2018.
 */

public class LikesListActivityDialog extends AppCompatActivity implements View.OnClickListener{

    private LikesListAdapter likesListAdapter;
        @BindView(R.id.rvLikesResponse)
        RecyclerView rvLikesResponse;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.sendButton)
    ImageView sendButton;

    Intent intent;
    @BindView(R.id.ll_rv)
    RelativeLayout ll_rv;
    @BindView(R.id.progressBarComments)
    ProgressBar progressBarComments;
    public  String count = "";
    private List<LikesListResponse> likesListResponses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_likes);
        ButterKnife.bind(this);
        init();
        callCommentsAPI();
    }

    private void callCommentsAPI() {

        if (L.isNetworkAvailable(LikesListActivityDialog.this)) {
            progressBarComments.setVisibility(View.VISIBLE);
            rvLikesResponse.setVisibility(View.GONE);
            requestCommentsAPI(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_getalllikes&moodlewsrestformat=json");

        } else {

            new PKBDialog(LikesListActivityDialog.this, PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
    }

    public void init(){
        intent = getIntent();
        iv_close.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        likesListResponses = new ArrayList<>();
        String comments = intent.getStringExtra("comments");
       // tvQueryResponseNotFound.setVisibility(View.GONE);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_close:
               onBackPressed();
                break;

            default:
                break;

        }

    }

    public void requestCommentsAPI(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //L.dismiss_pd();

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            progressBarComments.setVisibility(View.GONE);
                            rvLikesResponse.setVisibility(View.VISIBLE);
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray.length();i++){
                                    LikesListResponse likesListResponse = new Gson().fromJson(String.valueOf(jsonArray.get(i)), LikesListResponse.class);

                                    likesListResponses.add(likesListResponse);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            rvLikesResponse.setLayoutManager(new LinearLayoutManager(LikesListActivityDialog.this, LinearLayoutManager.VERTICAL, false));
                            rvLikesResponse.setHasFixedSize(true);
                            //rvcommentResponse.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
                            likesListAdapter = new LikesListAdapter(LikesListActivityDialog.this, likesListResponses);
                            rvLikesResponse.setAdapter(likesListAdapter);
                        } else {

                          //  iv_close.setVisibility(View.GONE);
                            progressBarComments.setVisibility(View.GONE);

                        }

                    } else {

                        iv_close.setVisibility(View.GONE);
                        progressBarComments.setVisibility(View.GONE);
                        rvLikesResponse.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBarComments.setVisibility(View.GONE);
                rvLikesResponse.setVisibility(View.GONE);
                L.l(LikesListActivityDialog.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(LikesListActivityDialog.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("postid", intent.getStringExtra("postId"));
                param.put("userid",MyApplication.mySharedPreference.getUserId());
                param.put("action", "0");
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);


    }



    private boolean validateFields() {
        boolean flag = true;

//        if (!MyValidator.isValidField(messageText, getString(R.string.err_enter_comment))) {
//            flag = false;
//        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.mySharedPreference.putPref(MySharedPreference.COMMENTS,count);
        MyApplication.mySharedPreference.putPref(MySharedPreference.POSITION,intent.getStringExtra("position"));
        finish();
    }
}
