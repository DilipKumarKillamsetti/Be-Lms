package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateFormat;
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
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.CommentsActivityDialog;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.DisclaimerActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.adapter.MyVideosAdapter;
import com.mahindra.be_lms.adapter.MyVideosAdapterByMe;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.videosupport.AAH_CustomRecyclerView;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MyExcellencePost extends Fragment implements NetworkMethod, View.OnClickListener, Callback {


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    private List<MyModel> modelList;
    private DashboardActivity mainActivity;
    private MyVideosAdapterByMe mAdapter;
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
    private int pos;
    private String postId;
    public ProgressDialog progressDialog;
    public MyExcellencePost() {
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
            progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
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
    public void callDeleteApi() {
        if (L.isNetworkAvailable(getActivity())) {
            progressDialog = new CustomProgressDialog(getActivity(),"");
            progressDialog.show();
            requestDeletePost(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_deletepost&moodlewsrestformat=json");
        } else {
            progressDialog.dismiss();
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }
    }

    public void callLikesApi(String checkedStatus, String id, int position) {
        if (L.isNetworkAvailable(getActivity())) {

            requestLikesAPI(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addlike&moodlewsrestformat=json", checkedStatus, id, position);

        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
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
        ll_woym.setVisibility(View.GONE);
        Picasso p = Picasso.with(getActivity());
        callApi();

        mAdapter = new MyVideosAdapterByMe(modelList, p, getActivity());
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

        recyclerView.setVisiblePercent(5); // percentage of View that needs to be visible to start playing

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
        MyApplication.mySharedPreference.putPref(MySharedPreference.COMMENTS, "");
        MyApplication.mySharedPreference.putPref(MySharedPreference.POSITION, "");
    }

    public void onResume() {
        super.onResume();
        recyclerView.playAvailableVideos(0);
        if (!MyApplication.mySharedPreference.getComments().equals("")) {

            MyModel myModel = modelList.get(Integer.parseInt(MyApplication.mySharedPreference.getPosition()));
            myModel.setCommentCount(MyApplication.mySharedPreference.getComments());
            mAdapter.notifyDataSetChanged();

        }

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


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            tvNoRecordFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            new BitMapAsyncTask().execute(jsonArray);

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
                   progressDialog.dismiss();
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
                tvNoRecordFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", "1");
                param.put("userid", MyApplication.mySharedPreference.getUserId());
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);


    }

    public void requestDeletePost(String url) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            progressDialog.dismiss();
                            new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setCustomImage(R.drawable.success_circle)
                                    .setContentText(jsonObject.getString("message"))
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            modelList.remove(pos);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }).show();


                        } else {
                            progressDialog.dismiss();
                        }
                    }
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
                return VolleySingleton.checkRequestparam(param);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);


    }

    public String timeStampToDate(String date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(date) * 1000L);
        String date1 = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date1;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_woym:
                replaceFrgament(new PostDataFragment());
                //addFrgament(new PostDataFragment());
                break;
            default:
                break;

        }

    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        recyclerView.stopVideos();
        recyclerView.removeAllViews();
        recyclerView.stopDownloadInBackground();
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    public void addFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        getFragmentManager().beginTransaction()
                .add(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
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
            Intent i = new Intent(getActivity(), CommentsActivityDialog.class);
            i.putExtra("comments", tag);
            i.putExtra("postId", id);
            i.putExtra("position", "" + position);
            startActivity(i);
        } else if(action.equals("post")){
            pos = position;
            postId = id;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete this post?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callDeleteApi();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }else {
            callLikesApi(tag, id, position);
        }

    }


    public void requestLikesAPI(String url, final String status, final String postId, final int position) {
        progressDialog = new CustomProgressDialog(getActivity(),"");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    JSONObject jsonObject = jsonArray1.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {

                            MyModel myModel = modelList.get(position);
                            myModel.setLikeStatus(Boolean.parseBoolean(status));
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

    class BitMapAsyncTask extends AsyncTask<JSONArray, Void, List<MyModel>> {

        Bitmap bitmap;

        @Override
        protected List<MyModel> doInBackground(JSONArray... voids) {
            try {
                String extension = "";
//                bitmap = CommonFunctions.retriveVideoFrameFromVideo(voids[0].getString("imageUrl"));
//                if (bitmap != null) {
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
//                }

                JSONArray jsonArray = voids[0];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                    if (jsonObject1.getString("attachment").equals("0")) {
                        modelList.add(new MyModel(jsonObject1.getString("postData"), "", jsonObject1.getString("description"), "text", jsonObject1.getString("sharedByName"), "", "", jsonObject1.getString("likesCount"), jsonObject1.getString("commentsCounts"), jsonObject1.getString("id"), jsonObject1.getString("location"), jsonObject1.getBoolean("likedStatus"), jsonObject1.getString("sharedById"), jsonObject1.getString("userpic"), jsonObject1.getString("comments"), bitmap));

                    } else {
                        //modelList.add(new MyModel(jsonObject1.getString("postData"), "",jsonObject1.getString("description"),"",jsonObject1.getString("sharedByName"),"","",jsonObject1.getString("likesCount"),jsonObject1.getString("commentsCounts"),jsonObject1.getString("id"),jsonObject1.getString("location"),true,jsonObject1.getString("sharedById")));

                        extension = CommonFunctions.getExtension(jsonObject1.getString("imageUrl"));

                        if (extension.contains("mp4") || extension.contains("mpeg")) {

                            try {
//                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                                            StrictMode.setThreadPolicy(policy);
                                bitmap = CommonFunctions.retriveVideoFrameFromVideo(jsonObject1.getString("imageUrl"));
                                if (bitmap != null) {
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                                }


                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            modelList.add(new MyModel(jsonObject1.getString("imageUrl"), "", jsonObject1.getString("description"), "video", jsonObject1.getString("sharedByName"), "", "", jsonObject1.getString("likesCount"), jsonObject1.getString("commentsCounts"), jsonObject1.getString("id"), jsonObject1.getString("location"), jsonObject1.getBoolean("likedStatus"), jsonObject1.getString("sharedById"), jsonObject1.getString("userpic"), jsonObject1.getString("comments"), bitmap));
                        } else {
                            modelList.add(new MyModel("", jsonObject1.getString("imageUrl"), jsonObject1.getString("description"), "image", jsonObject1.getString("sharedByName"), "", "", jsonObject1.getString("likesCount"), jsonObject1.getString("commentsCounts"), jsonObject1.getString("id"), jsonObject1.getString("location"), jsonObject1.getBoolean("likedStatus"), jsonObject1.getString("sharedById"), jsonObject1.getString("userpic"), jsonObject1.getString("comments"), bitmap));
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return modelList;
        }

        @Override
        protected void onPostExecute(List<MyModel> jsonObject1) {
            try {

                //   modelList.add(new MyModel(jsonObject1.getString("imageUrl"), "", jsonObject1.getString("description"), "video", jsonObject1.getString("sharedByName"), "", "", jsonObject1.getString("likesCount"), jsonObject1.getString("commentsCounts"), jsonObject1.getString("id"), jsonObject1.getString("location"), jsonObject1.getBoolean("likedStatus"), jsonObject1.getString("sharedById"), jsonObject1.getString("userpic"), jsonObject1.getString("comments"),bitmap));

                if (jsonObject1.size() > 0) {
                    mAdapter.setData(jsonObject1);
                    mAdapter.notifyDataSetChanged();
                } else {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                L.dismiss_pd();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
