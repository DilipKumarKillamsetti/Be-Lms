package com.mahindra.be_lms.fragments;

import android.app.Activity;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.CourseListAdapterEL;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/12/2018.
 */

public class CourseListFragmentELearning extends Fragment  implements Callback,NetworkMethod{

    private DashboardActivity dashboardActivity;
    private static final String TAG = CourseListFragmentELearning.class.getSimpleName();
    @BindView(R.id.rvLearnTestQuizFragment)
    RecyclerView rvLearnTestQuizFragment;
    private CourseListAdapterEL courseListAdapter;
    private MainActivity mainActivity;
    ArrayList<CourseModel> courseModels;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;

    public CourseListFragmentELearning(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_list_el, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    public  void init(){

        callApi();

        courseModels = new ArrayList<>();

        rvLearnTestQuizFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvLearnTestQuizFragment.setHasFixedSize(true);
        courseListAdapter = new CourseListAdapterEL(getActivity(),courseModels);
        courseListAdapter.setmCallback(this);
        rvLearnTestQuizFragment.setAdapter(courseListAdapter);

    }

    public void callApi(){
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvLearnTestQuizFragment.setVisibility(View.VISIBLE);
            }
            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=moodle_enrol_get_users_courses&userid="+MyApplication.mySharedPreference.getUserId()+"&moodlewsrestformat=json");

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: call");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {
//                mainActivity.getSupportActionBar().show();
//                mainActivity.getSupportActionBar().setTitle("");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void request(String url) { JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
        @Override
        public void onResponse(JSONArray response) {

            try {
                JSONObject jsonObject = response.getJSONObject(0);
                if(jsonObject.length()>0){
                    if(jsonObject.getString("Status").equalsIgnoreCase("Success")){
                        tvNoRecordFound.setVisibility(View.GONE);
                        rvLearnTestQuizFragment.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("courses");
                        for(int i = 0 ; i<jsonArray.length();i++){

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

                    }else{
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                        rvLearnTestQuizFragment.setVisibility(View.GONE);
                    }

                }else{
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    rvLearnTestQuizFragment.setVisibility(View.GONE);
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

    public void replaceFrgament(Fragment fragment,String id) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        Bundle args = new Bundle();
        args.putString("id",id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.myContainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void myCallback(int position) {
        dashboardActivity.replaceFrgament(new QuizListFragment());
    }

    @Override
    public void myCallback(int position, String tag) {
        //replaceFrgament(new LearnTestQuizFragment(),courseModels.get(position).getId());
        dashboardActivity.replaceFrgament(new ManualInfoFragment());
    }

}
