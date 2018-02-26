package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
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
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.db.Coordinators;
import com.mahindra.be_lms.db.Course;
import com.mahindra.be_lms.db.Event;
import com.mahindra.be_lms.db.Trainers;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramDetailFragment extends Fragment implements View.OnClickListener,NetworkMethod {
    private static final String TAG = ProgramDetailFragment.class.getSimpleName();
    @BindView(R.id.tbProgramDetailFragmentTitle)
    Toolbar tbProgramDetailFragmentTitle;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    // @BindView(R.id.tvProgramDetailFragmentDesc)
    //  TextView tvProgramDetailFragmentDesc;
    @BindView(R.id.tvProgramDetailFromDate)
    TextView tvProgramDetailFromDate;
    @BindView(R.id.tvProgramDetailTODate)
    TextView tvProgramDetailTODate;
    @BindView(R.id.tvProgramDetailProgramNo)
    TextView tvProgramDetailProgramNo;
    @BindView(R.id.tvProgramDetailVenue)
    TextView tvProgramDetailVenue;
    @BindView(R.id.tvProgramDetailCordinator)
    LinearLayout tvProgramDetailCordinator;
    @BindView(R.id.llProgramDetailTrainer)
    LinearLayout llProgramDetailTrainer;
    @BindView(R.id.llprogram_details_course_layout)
    LinearLayout llprogram_details_course_layout;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.btnNomination)
    Button btnNomination;
    private Event event;
    private MainActivity mainActivity;

    public ProgramDetailFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_program_detail, container, false);
        ButterKnife.bind(this, view);
        init(view);
        // Inflate the layout for this fragment
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            event = bundle.getParcelable(getString(R.string.event_parcelable_tag));
           // L.l(TAG, "CourseList: " + event.getCourseList().toString());

            btnNomination.setOnClickListener(ProgramDetailFragment.this);
            try {
                collapsingToolbar.setTitle(event.getEventName());
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(event.getEventFromdate())*1000L);
                String fromDate = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                tvProgramDetailFromDate.setText("From : " + fromDate);

                if(null == event.getEventTodate()){
                    tvProgramDetailTODate.setText("TO : --");
                }else{
                    cal.setTimeInMillis(Long.parseLong(event.getEventTodate())*1000L);
                    String toDate = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                    tvProgramDetailTODate.setText("TO : " + toDate);
                }
                if(null == event.getEventID()){
                    tvProgramDetailProgramNo.setText("Program no : --");
                }else{
                    tvProgramDetailProgramNo.setText("Program no : " + event.getEventID());
                }
                if(null == event.getEventVenue()){
                    tvProgramDetailVenue.setText("Venue : --");
                }else{
                    tvProgramDetailVenue.setText("Venue : " + event.getEventVenue());
                }
                if(event.getEventMonth().contains("<")){
                    if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
                        description.setText(Html.fromHtml(event.getEventMonth(), Html.FROM_HTML_MODE_COMPACT));

                    }else{
                        description.setText(Html.fromHtml(event.getEventMonth()));

                    }
                }else{
                    description.setText(event.getEventMonth());
                }
                List<Coordinators> coordinatorsList = event.getCoordinatorsList();
                if (!coordinatorsList.isEmpty()) {
                    view.findViewById(R.id.cvProgramDetailCordinator).setVisibility(View.VISIBLE);
                }
                setCordinatorLayout(coordinatorsList);
                List<Trainers> trainersList = event.getTrainersList();
                L.l("Trainer list size: " + trainersList.size());
                if (!trainersList.isEmpty()) {
                    view.findViewById(R.id.cvProgramDetailTrainer).setVisibility(View.VISIBLE);
                }
                setTrainerLayout(trainersList);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            setCourseLayout();
        }
    }

    private void setCordinatorLayout(List<Coordinators> coordinatorsList) {
        boolean isFirst = true;
        for (int i = 0; i < coordinatorsList.size(); i++) {
            TextView tv = new TextView(getActivity());
            tv.setText(getString(R.string.bullet) + " " + coordinatorsList.get(i).getCoordinatorsName());
            tv.setId(1 + i);
            if (isFirst) {
                tv.setPadding(24, 0, 16, 16);
                isFirst = false;
            } else {
                tv.setPadding(24, 16, 16, 16);
            }

            // tv.setOnClickListener(this);
            tvProgramDetailCordinator.addView(tv);
        }
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    private void setTrainerLayout(List<Trainers> trainersList) {

        boolean isFirst = true;
        for (int i = 0; i < trainersList.size(); i++) {
            L.l("Trainer Name: " + trainersList.get(i).getTrainersName());
            TextView tv1 = new TextView(getActivity());
            tv1.setText(getString(R.string.bullet) + " " + trainersList.get(i).getTrainersName());
            tv1.setId(3 + i);
            if (isFirst) {
                tv1.setPadding(24, 0, 16, 16);
                isFirst = false;
            } else {
                tv1.setPadding(24, 16, 16, 16);
            }
            //tv.setOnClickListener(this);
            L.l("add view trainer");
            llProgramDetailTrainer.addView(tv1);

        }
    }

    private void setCourseLayout() {
        List<Course> courseList = event.getCourseList();
       // L.l(TAG, "courselistSize: : " + courseList.size());
        boolean isFirst = true;
//        for (int i = 1; i <= courseList.size(); i++) {
//            TextView tv = new TextView(getActivity());
//            tv.setText(getString(R.string.bullet) + " " + courseList.get(i - 1).getCourseName());
//            tv.setId(i);
//            if (isFirst) {
//                tv.setPadding(24, 0, 16, 16);
//                isFirst = false;
//            } else {
//                tv.setPadding(24, 16, 16, 16);
//            }
//
//            //tv.setOnClickListener(this);
//            llprogram_details_course_layout.addView(tv);

//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNomination:

                if (L.isNetworkAvailable(getActivity())) {

                    L.pd(getString(R.string.dialog_please_wait), getActivity());
                    request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=core_nominate_me&eventid="+event.getEventID()+"&moodlewsrestformat=json");

                } else {


                }

//                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
//                        .setCustomImage(R.drawable.success_circle)
//                        .setContentText("To nominate this event goto portal.")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                                //clearPreviousUserNotification();
//                            }
//                        }).show();



                break;
            default:
                break;
        }
    }

    @Override
    public void request(String url) {JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
        @Override
        public void onResponse(JSONArray response) {

            try {
                JSONObject jsonObject = response.getJSONObject(0);
                if(jsonObject.length()>0){
                    if(jsonObject.getString("Status").equalsIgnoreCase("Success")){
                                        new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText("Event nomination done successfully.")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                //clearPreviousUserNotification();
                            }
                        }).show();

                    }else{
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Something went wrong.Please try again").show();
                    }

                }else{
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Something went wrong.Please try again").show();;
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

    //Course details display funtionality Currently not in use
    /*@Override
    public void onClick(View v) {
       // L.t("ID: "+v.getId());
        for (int i = 1; i <=courseList.size(); i++) {
            if (v.getId()==i-1){
                L.l("GOT COURSE :"+courseList.get(i-1).toString());
                View dialogView =LayoutInflater.from(getActivity()).inflate(R.layout.custom_course_detail_dialog_layout,null,false);
                TextView tvCustomCourseDetailsTitle=(TextView)dialogView.findViewById(R.id.tvCustomCourseDetailsTitle);
                tvCustomCourseDetailsTitle.setText(courseList.get(i-1).getCourseName());
                TextView tvCustomCourseDetailsDesc=(TextView)dialogView.findViewById(R.id.tvCustomCourseDetailsDesc);
               // tvCustomCourseDetailsDesc.setText(courseList.get(i).getCourse_desc());
                new AlertDialog.Builder(getActivity())
                        .setView(dialogView).show();
            }
        }
    }*/
}
