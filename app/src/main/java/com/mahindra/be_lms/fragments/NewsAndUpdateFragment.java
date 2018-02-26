package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.CommentsActivityDialog;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.MyVideosAdapter;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.videosupport.AAH_CustomRecyclerView;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/16/2017.
 */

public class NewsAndUpdateFragment extends Fragment implements NetworkMethod,View.OnClickListener,Callback {


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    private  List<MyModel> modelList;
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
            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=mod_forum_get_forum_discussions_paginated&forumid=4&moodlewsrestformat=json");

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

    public void init() {
        modelList = new ArrayList<>();
        user = new DBHelper().getUser();
        if (!user.getUserPicture().isEmpty()) {
            Picasso.with(getActivity())
                    .load(user.getUserPicture()+"&token="+ MyApplication.mySharedPreference.getUserToken())
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

        mAdapter = new MyVideosAdapter(modelList, p);
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

        recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

        //extra - start downloading all videos in background before loading RecyclerView
        List<String> urls = new ArrayList<>();
        for (MyModel object : modelList) {
            if (object.getVideo_url() != null && object.getVideo_url().contains("http"))
                urls.add(object.getVideo_url());
        }
        recyclerView.preDownload(urls);

        recyclerView.setAdapter(mAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }
    public void onResume() {
        super.onResume();

        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {
                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
//                if (adapter != null) {
//                    Log.d(TAG, "onResume: adapternot null");
//                    queriesList = new DBHelper().getQueriesList();
//                    adapter = new QueriesAdapter(getActivity(), queriesList);
//                    adapter.setMyCallback(this);
//                    rvMyQueriesFragmentList.setAdapter(adapter);
//                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void request(String url) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("Status").equalsIgnoreCase("Success")) {
                        tvNoRecordFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        String extension = "";
                        JSONArray discussionsArray = response.getJSONArray("discussions");
                        for (int i = 0; i < discussionsArray.length(); i++) {
                            JSONObject discussionsObject = (JSONObject) discussionsArray.get(i);
                            JSONArray jsonArray1 = null;
                            if (discussionsObject.has("attachments")) {
                                jsonArray1 = discussionsObject.getJSONArray("attachments");
                                extension = CommonFunctions.getExtension(jsonArray1.getJSONObject(0).getString("filename"));
                            }
                            if (discussionsObject.getString("attachment").equalsIgnoreCase("")) {
                                modelList.add(new MyModel(discussionsObject.getString("name"), "text", discussionsObject.getString("message"), discussionsObject.getString("userfullname"), timeStampToDate(discussionsObject.getString("created"))));
                            } else if (extension.contains("mp4") || extension.contains("mpeg")) {
                                modelList.add(new MyModel(jsonArray1.getJSONObject(0).getString("fileurl") + "?token=" + MyApplication.mySharedPreference.getUserToken(), "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1481795675/3_yqeudi.jpg", "video4", "video", discussionsObject.getString("userfullname"), discussionsObject.getString("name"), timeStampToDate(discussionsObject.getString("created"))));
                            } else {
                                modelList.add(new MyModel(jsonArray1.getJSONObject(0).getString("fileurl") + "?token=" + MyApplication.mySharedPreference.getUserToken(), "image13", "image", discussionsObject.getString("userfullname"), discussionsObject.getString("name"), timeStampToDate(discussionsObject.getString("created"))));
                            }
                        }
                        if (modelList.size() > 0) {
                            mAdapter.setData(modelList);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);

    }

    public String timeStampToDate(String date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(date) * 1000L);
        String date1 = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date1;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_woym:
                replaceFrgament(new PostDataFragment());

                break;
            default:
                break;

        }

    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
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

    }
}
