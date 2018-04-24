package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.QueriesAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/21/2017.
 */

public class CourseListFragment extends Fragment implements Callback, NetworkMethod {
    private static final String TAG = CourseListFragment.class.getSimpleName();
    @BindView(R.id.rvLearnTestQuizFragment)
    RecyclerView rvLearnTestQuizFragment;
    private CourseListAdapter courseListAdapter;
    private DashboardActivity mainActivity;
    ArrayList<CourseModel> courseModels;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    @BindView(R.id.totalCourse)
    TextView totalCourse;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    public ProgressDialog progressDialog;

    public CourseListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        //mainActivity.setDrawerEnabled(false);


        callApi();

        courseModels = new ArrayList<>();

        rvLearnTestQuizFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvLearnTestQuizFragment.setHasFixedSize(true);
        courseListAdapter = new CourseListAdapter(getActivity(), courseModels);
        courseListAdapter.setmCallback(this);
        rvLearnTestQuizFragment.setAdapter(courseListAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    public void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvLearnTestQuizFragment.setVisibility(View.VISIBLE);
            }
            // L.pd(getString(R.string.dialog_please_wait), getActivity());
            progressDialog = new CustomProgressDialog(getActivity(), "");
            progressDialog.show();
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=moodle_enrol_get_users_courses&userid=" + MyApplication.mySharedPreference.getUserId() + "&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvLearnTestQuizFragment.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });

        }

    }

    @Override
    public void myCallback(int position) {
        mainActivity.replaceFrgament(new QuizListFragment());
    }

    @Override
    public void myCallback(int position, String tag) {
        replaceFrgament(new LearnTestQuizFragment(), courseModels.get(position).getId());
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: call");
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
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                            tvNoRecordFound.setVisibility(View.GONE);
                            rvLearnTestQuizFragment.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("courses");
                            totalCourse.setText("TotalCourse: " + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                                CourseModel courseModel = new CourseModel();
                                courseModel.setId(jsonObject1.getString("id"));
                                courseModel.setCourseName(jsonObject1.getString("fullname"));
                                courseModel.setCourseDescription(jsonObject1.getString("summary"));
                                courseModels.add(courseModel);
                            }
                            if (courseModels.size() > 0) {
                                courseListAdapter.setCourseList(courseModels);
                                courseListAdapter.notifyDataSetChanged();
                            } else {
                                tvNoRecordFound.setVisibility(View.VISIBLE);
                                rvLearnTestQuizFragment.setVisibility(View.GONE);
                            }

                        } else {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            rvLearnTestQuizFragment.setVisibility(View.GONE);
                        }

                    } else {
                        progressDialog.dismiss();
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        rvLearnTestQuizFragment.setVisibility(View.GONE);
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
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    public void replaceFrgament(Fragment fragment, String id) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
        Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}