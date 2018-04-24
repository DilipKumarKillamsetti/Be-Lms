package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.db.Event;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/2/2018.
 */

public class EventDetailsFragment extends Fragment implements View.OnClickListener,NetworkMethod{

    private DashboardActivity dashboardActivity;
    @BindView(R.id.tv_event_date)
    TextView tv_event_date;
    @BindView(R.id.tv_event_title)
    TextView tv_event_title;
    @BindView(R.id.tv_event_venue)
    TextView tv_event_venue;
    @BindView(R.id.tv_from)
    TextView tv_from;
    @BindView(R.id.tv_to)
    TextView tv_to;
    @BindView(R.id.btn_nominate)
    Button btn_nominate;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.btn_details)
    Button btn_details;
    private Event event;
    private ProgressDialog progressDialog;
    public EventDetailsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    public void init(){

        btn_details.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            event = bundle.getParcelable(getString(R.string.event_parcelable_tag));
        }
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(event.getEventFromdate())*1000L);
        String eventDate = DateFormat.format("dd MMM yyyy", cal).toString();
        tv_event_title.setText(event.getEventName());
        tv_event_date.setText(eventDate);
        int status = DateManagement.compareDates(DateFormat.format("dd-MM-yyyy", cal).toString(),DateManagement.getCurrentDate());
        if(status == 2){
            btn_nominate.setVisibility(View.INVISIBLE);
        }else{
            btn_nominate.setOnClickListener(this);
        }
        String fromDate = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

        tv_from.setText("From : " + fromDate);

        if(null == event.getEventTodate()){
            tv_to.setText("TO : --");
        }else{
            cal.setTimeInMillis(Long.parseLong(event.getEventTodate())*1000L);
            String toDate = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
            tv_to.setText("TO : " + toDate);
        }
        if(null == event.getEventVenue()){
            tv_event_venue.setText("Venue : --");
        }else{
            tv_event_venue.setText("Venue : " + event.getEventVenue());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_nominate:

                if (L.isNetworkAvailable(getActivity())) {

                    progressDialog = new CustomProgressDialog(getActivity(),"");
                    progressDialog.show();
                    request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=core_nominate_me&eventid="+event.getEventID()+"&moodlewsrestformat=json");

                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection)).show();

                }

                break;

            case R.id.btn_details:
                tv_description.setVisibility(View.VISIBLE);
                if(event.getEventMonth().isEmpty()){
                    tv_description.setText("No Description Available");
                }else{
                    tv_description.setText(Html.fromHtml(event.getEventMonth()));
                }

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
                                .setContentText(jsonObject.getString("Message"))
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
                progressDialog.dismiss();
            }
            progressDialog.dismiss();

//            String responseData = "{\"statusCode\":\"200\",\"result\":\"Success\",\"message\":\"Success\",\"data\":{\"id\":\"121\",\"username\":\"U000121\",\"firstname\":\"Vijay\",\"lastname\":\"Kumavat\",\"emailid\":\"v11.kumavat@yahoo.in\",\"mobileno\":\"+918149143550\",\"organization_code\":\"\",\"organization\":\"Krios\",\"picture\":\"\",\"location\":\"296\",\"designation\":\"4\",\"qualification\":\"25\",\"role\":\"Customer\",\"dob\":\"11-03-1999\",\"doj\":\"11-09-2017\",\"profiles\":\"Customer,Tester,Samriddhi\",\"groups\":\"MASL,Customer,Tester\",\"menurights\":{\"registration\":\"N\",\"search\":\"N\",\"powerolCare\":\"N\",\"mostViewed\":\"N\",\"myProfile\":\"N\",\"surveyFeedbacks\":\"N\",\"myTrainingPassport\":\"N\",\"learningTestQuizs\":\"N\",\"manualsBulletins\":\"Y\",\"trainingCalenderNomination\":\"N\",\"queriesResponse\":\"N\",\"technicalUploads\":\"N\",\"myFieldRecords\":\"N\",\"reports\":\"N\",\"manpowerEdition\":\"N\"}}}";
//            updateDisplay(responseData);
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
    }) ;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);

    }
}
