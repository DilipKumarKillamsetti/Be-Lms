package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.CommentsActivityDialog;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.RegisterActivity;
import com.mahindra.be_lms.activities.ViewImageVideo;
import com.mahindra.be_lms.adapter.MyVideosAdapter;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.AllPostResponse;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.Datum;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.videosupport.AAH_CustomRecyclerView;
import com.mahindra.be_lms.videosupport.AAH_CustomViewHolder;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/16/2017.
 */

public class NewsAndUpdateFragment extends Fragment implements NetworkMethod, View.OnClickListener, Callback {


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    private List<Datum> modelList;
    private DashboardActivity mainActivity;
    private MyVideosAdapter mAdapter;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    @BindView(R.id.ll_woym)
    LinearLayout ll_woym;
    @BindView(R.id.header_imageView)
    CircularImageView header_imageView;
    private User user;
    private ProgressDialog progressDialog;

    public NewsAndUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.newupdate, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();

        return view;
    }

    public void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            // L.pd(getString(R.string.dialog_please_wait), getActivity());
            progressDialog = new CustomProgressDialog(getActivity(), "");
            progressDialog.show();
            //request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_forum_get_forum_discussions_paginated&forumid=4&moodlewsrestformat=json");
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_getallposts&moodlewsrestformat=json&userid=" + MyApplication.mySharedPreference.getUserId());
        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
        }

    }

    public void callLikesApi(String checkedStatus, String id, int position) {
        if (L.isNetworkAvailable(getActivity())) {
            //request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_forum_get_forum_discussions_paginated&forumid=4&moodlewsrestformat=json");
            requestLikesAPI(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addlike&moodlewsrestformat=json", checkedStatus, id, position);

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

    public void init() {
        modelList = new ArrayList<>();
        user = new DBHelper().getUser();
        if (!user.getUserPicture().isEmpty()) {
            Picasso.with(getActivity())
                    .load(user.getUserPicture() + "&token=" + MyApplication.mySharedPreference.getUserToken())
                    .resize(200, 200)
                    .placeholder(getResources().getDrawable(R.drawable.user_new))
                    .centerCrop()
                    .into(header_imageView);
        } else {
            header_imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
        }
        ll_woym.setOnClickListener(this);
        Picasso p = Picasso.with(getActivity());
        callApi();

        mAdapter = new MyVideosAdapter(modelList, p, getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter.setMyCallback(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //todo before setAdapter
        recyclerView.setActivity(getActivity());
        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(true); // false by default
        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        recyclerView.setCheckForMp4(false); //true by default
        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default
        recyclerView.setDownloadVideos(true); // false by default
        recyclerView.setVisiblePercent(100); // percentage of View that needs to be visible to start playing
        //extra - start downloading all videos in background before loading RecyclerView
//        List<String> urls = new ArrayList<>();
//        for (Datum object : modelList) {
//            if (object.getImageUrl() != null && object.getVideo_url().contains("http"))
//                urls.add(object.getVideo_url());
//        }
//        recyclerView.preDownload(urls);

        recyclerView.setAdapter(mAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
        MyApplication.mySharedPreference.putPref(MySharedPreference.COMMENTS, "");
        MyApplication.mySharedPreference.putPref(MySharedPreference.POSITION, "");
        MyApplication.mySharedPreference.putPref(MySharedPreference.CHECKED_COUNT,"");
        MyApplication.mySharedPreference.putPref(MySharedPreference.CHECKED_STATUS,"");
    }

    public void onResume() {
        super.onResume();
        //recyclerView.playAvailableVideos(0);
        if (!MyApplication.mySharedPreference.getComments().equals("")) {
            Datum myModel = modelList.get(Integer.parseInt(MyApplication.mySharedPreference.getPosition()));
            myModel.setCommentsCounts(MyApplication.mySharedPreference.getComments());
            mAdapter.notifyDataSetChanged();
        }
        if(!TextUtils.isEmpty(MyApplication.mySharedPreference.getPrefisString(MySharedPreference.CHECKED_COUNT))){
            Datum myModel = modelList.get(Integer.parseInt(MyApplication.mySharedPreference.getPosition()));
            myModel.setLikesCount(MyApplication.mySharedPreference.getPrefisString(MySharedPreference.CHECKED_COUNT));
            myModel.setLikedStatus(MyApplication.mySharedPreference.getPrefisBoolean(MySharedPreference.CHECKED_STATUS));
            mAdapter.notifyDataSetChanged();
        }

        if (!MyApplication.mySharedPreference.checkUserLogin()) {

            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } else {
            try {

                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");

            } catch (NullPointerException e) {

                e.printStackTrace();

            }
        }
    }

    @Override
    public void request(String url) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    AllPostResponse allPostResponse = new Gson().fromJson(String.valueOf(jsonArray1.getJSONObject(0)), AllPostResponse.class);
                    if (jsonObject.length() > 0) {
                        if (allPostResponse.getStatus().equalsIgnoreCase("Success")) {
                            tvNoRecordFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            modelList.addAll(allPostResponse.getData());
                            mAdapter.setData(modelList);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progressDialog.dismiss();
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
                L.l(getActivity(), "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
                tvNoRecordFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", "0");
                param.put("userid", MyApplication.mySharedPreference.getUserId());
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_woym:
                replaceFrgament(new PostDataFragment());
                break;
            default:
                break;

        }

    }

    public void replaceFrgament(Fragment fragment) {
        recyclerView.stopVideos();
        recyclerView.removeAllViews();
        recyclerView.stopDownloadInBackground();
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void myCallback(int position) {
        Intent i = new Intent(getActivity(), CommentsActivityDialog.class);
        startActivity(i);
    }

    @Override
    public void myCallback(int position, String tag) {

        Intent i = new Intent(getActivity(), CommentsActivityDialog.class);
        i.putExtra("comments", tag);
        i.putExtra("position", "" + position);
        startActivity(i);

    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

        if (action.equals("comments")) {
            if (L.isNetworkAvailable(getActivity())) {
                Intent i = new Intent(getActivity(), CommentsActivityDialog.class);
                i.putExtra("comments", tag);
                i.putExtra("postId", id);
                i.putExtra("position", "" + position);
                startActivity(i);
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }
        } else if (action.equals("view")) {
            if (L.isNetworkAvailable(getActivity())) {
                Intent intent = new Intent(getActivity(), ViewImageVideo.class);
                intent.putExtra("url", tag);
                intent.putExtra("type", id);
                intent.putExtra("name", modelList.get(position).getSharedByName());
                intent.putExtra("postId", modelList.get(position).getId());
                intent.putExtra("position",""+position);
                intent.putExtra("checkedStatus", modelList.get(position).getLikedStatus());
                startActivity(intent);
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }
        } else {
            callLikesApi(tag, id, position);
        }

    }


    public void requestLikesAPI(String url, final String status, final String postId, final int position) {
        progressDialog = new CustomProgressDialog(getActivity(), "");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {

                            Datum myModel = modelList.get(position);
                            myModel.setLikedStatus(Boolean.parseBoolean(status));
                            myModel.setLikesCount(jsonObject.getString("result"));
                            modelList.set(position, myModel);
                            mAdapter.notifyDataSetChanged();

                        } else {

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
                param.put("postid", postId);
                param.put("userid", MyApplication.mySharedPreference.getUserId());
                param.put("action", status);
                return VolleySingleton.checkRequestparam(param);
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        recyclerView.stopVideos();
        recyclerView.stopDownloadInBackground();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerView.stopVideos();
        recyclerView.stopDownloadInBackground();
    }

}
