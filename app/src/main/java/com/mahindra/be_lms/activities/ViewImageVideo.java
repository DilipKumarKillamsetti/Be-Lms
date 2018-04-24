package com.mahindra.be_lms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;




/**
 * Created by Dell on 1/19/2018.
 */

public class ViewImageVideo extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.iv_userImage)
    ImageView iv_userImage;
    @BindView(R.id.vv_userVideo)
    VideoView vv_userVideo;
    private ProgressDialog dialog;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.ll_comment)
    LinearLayout ll_comment;
    @BindView(R.id.cb_like)
    CheckBox cb_like;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_dasboard);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(null!=vv_userVideo) {
            vv_userVideo.stopPlayback();
            vv_userVideo.resume();
        }
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(null!=vv_userVideo) {
            vv_userVideo.stopPlayback();
            vv_userVideo.resume();
        }
        finish();
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");
        String type = getIntent().getStringExtra("type");
        boolean checkedStatus = getIntent().getBooleanExtra("checkedStatus",false);
        if(checkedStatus){
            cb_like.setChecked(checkedStatus);
        }
        cb_like.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        if(type.equalsIgnoreCase("video")){
            vv_userVideo.setVideoURI(null);
            iv_userImage.setVisibility(View.GONE);
            vv_userVideo.setVisibility(View.VISIBLE);
            vv_userVideo.setVideoURI(Uri.parse(url));
            MediaController mc = new MediaController(this);
            vv_userVideo.setMediaController(mc);
            vv_userVideo.start();
            progressbar.setVisibility(View.VISIBLE);
            vv_userVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                       int arg2) {
                            // TODO Auto-generated method stub
                            progressbar.setVisibility(View.GONE);
                            mp.start();
                        }
                    });
                }
            });
        }else{
            vv_userVideo.setVisibility(View.GONE);
            iv_userImage.setVisibility(View.VISIBLE);
            Picasso.with(ViewImageVideo.this)
                    .load(url)
                    .placeholder(getResources().getDrawable(R.drawable.user_new))
                    .into(iv_userImage);
        }


        try {
            getSupportActionBar().show();
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellow)));
            actionBar.setTitle(name);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null!=vv_userVideo) {
            vv_userVideo.stopPlayback();
            vv_userVideo.resume();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_comment:
                Intent callIntent = new Intent(ViewImageVideo.this,CommentsActivityDialog.class);
                callIntent.putExtra("postId",getIntent().getStringExtra("postId"));
                startActivity(callIntent);
                break;

            case R.id.cb_like:
                callLikesApi();
                break;
        }
    }

    public void callLikesApi() {
        if (L.isNetworkAvailable(ViewImageVideo.this)) {
            //request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_forum_get_forum_discussions_paginated&forumid=4&moodlewsrestformat=json");
            requestLikesAPI(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addlike&moodlewsrestformat=json");

        } else {
//            retryButtonLayout.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//            btnRetry.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    callApi();
//                }
//            });
        }

    }

    public void requestLikesAPI(String url) {
        progressDialog = new CustomProgressDialog(ViewImageVideo.this, "");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            MyApplication.mySharedPreference.putPref(MySharedPreference.POSITION,getIntent().getStringExtra("position"));
                            MyApplication.mySharedPreference.putPref(MySharedPreference.CHECKED_STATUS,cb_like.isChecked());
                            MyApplication.mySharedPreference.putPref(MySharedPreference.CHECKED_COUNT,jsonObject.getString("result"));

                        } else {
                            getIntent().getBooleanExtra("checkedStatus",false);

                        }

                    } else {

                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                L.l(ViewImageVideo.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(ViewImageVideo.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("postid", getIntent().getStringExtra("postId"));
                param.put("userid", MyApplication.mySharedPreference.getUserId());
                param.put("action", ""+cb_like.isChecked());
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);


    }
}
