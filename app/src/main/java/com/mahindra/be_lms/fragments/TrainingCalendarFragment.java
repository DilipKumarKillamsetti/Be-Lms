package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.gfranks.collapsible.calendar.CollapsibleCalendarView;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.EventsListAdapter;
import com.mahindra.be_lms.adapter.QuizListAdapter;
import com.mahindra.be_lms.db.Event;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.EventNew;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.mahindra.be_lms.util.DateManagement;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingCalendarFragment extends Fragment implements NetworkMethod, Callback,CollapsibleCalendarView.Listener<EventNew> {

//    @BindView(R.id.tlTrainingCalendar)
//    TableLayout tlTrainingCalendar;
    @BindView(R.id.tvTrainingCalendarFragmentNRF)
    TextView tvTrainingCalendarFragmentNRF;
    @BindView(R.id.rvEventListFragment)
    RecyclerView rvEventListFragment;
    @BindView(R.id.tvNoEventFound)
    TextView tvNoEventFound;
    private EventsListAdapter eventsListAdapter;
    private List<Event> eventList;
    private Calendar cal = Calendar.getInstance();
    private DashboardActivity mainActivity;
    private List<Event> eventList2;
    @BindView(R.id.calendar)
    CollapsibleCalendarView mCalendarView;
    private ProgressDialog progressDialog;

    public TrainingCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_calendar, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        eventList = new ArrayList<>();
        eventList2 = new ArrayList<>();
        if (L.isNetworkAvailable(getActivity())) {
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_calendar_get_calendar_events&moodlewsrestformat=json");
        } else {
            // L.t(getString(R.string.err_network_connection));
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

        //quizModels = new ArrayList<>();

        rvEventListFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvEventListFragment.setHasFixedSize(true);
        eventsListAdapter = new EventsListAdapter(getActivity(), eventList2);
        eventsListAdapter.setmCallback(this);
        rvEventListFragment.setAdapter(eventsListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCalendar() {
        eventList2.clear();
        for (Event event : eventList) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(event.getEventFromdate()) * 1000L);
            String date1 = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
            if (DateManagement.getStrToDate(date1, DateManagement.DD_MM_YYYY).getTime() == DateManagement.getStrToDate(DateFormat.format("dd-MM-yyyy hh:mm:ss", date).toString(), DateManagement.DD_MM_YYYY).getTime()) {
                eventList2.add(event);
            }
        }
        if (eventList2.size() > 0) {
            tvNoEventFound.setVisibility(View.GONE);
            rvEventListFragment.setVisibility(View.VISIBLE);
            eventsListAdapter.setEventList(eventList2);
            eventsListAdapter.notifyDataSetChanged();
        } else {
            eventList2.clear();
            tvNoEventFound.setVisibility(View.VISIBLE);
            rvEventListFragment.setVisibility(View.GONE);
        }


        CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {


eventList2.clear();
                for (Event event : eventList) {
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(event.getEventFromdate()) * 1000L);
                // String date1 = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                String date1 = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                if (DateManagement.getStrToDate(date1, DateManagement.DD_MM_YYYY).getTime() == date.getTime()) {
                    // L.t("Match Date ");
                    eventList2.add(event);

                }
            }
                if (eventList2.size() > 1) {
                tvNoEventFound.setVisibility(View.GONE);
                rvEventListFragment.setVisibility(View.VISIBLE);
                eventsListAdapter.setEventList(eventList2);
                eventsListAdapter.notifyDataSetChanged();
            } else if (eventList2.size() == 1) {
                tvNoEventFound.setVisibility(View.GONE);
                rvEventListFragment.setVisibility(View.VISIBLE);
                eventsListAdapter.setEventList(eventList2);
                eventsListAdapter.notifyDataSetChanged();
            } else {
                eventList2.clear();
                tvNoEventFound.setVisibility(View.VISIBLE);
                rvEventListFragment.setVisibility(View.GONE);
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText("No event on selected date.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }

        }
        };
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        caldroidFragment.setArguments(args);
        ColorDrawable clrNomination = new ColorDrawable(getResources().getColor(R.color.green));
        ColorDrawable clrNotNomination = new ColorDrawable(getResources().getColor(R.color.blue));
        int totalProgram = eventList.size();
        int nominatedProgram = 0;

        for (Event event : eventList) {
            L.l("Event Calendar Date " + event.getEventFromdate() + " Nomination : " /*+ event.isNomination()*/);
            nominatedProgram += event.getEventNomination().equalsIgnoreCase("yes") ? 1 : 0;
            ColorDrawable colorDrawable = event.getEventNomination().equalsIgnoreCase("yes") ? clrNomination : clrNotNomination;

            try {
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(event.getEventFromdate()) * 1000L);
                String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                Date programDate = DateManagement.getStrToDate(date, DateManagement.DD_MM_YYYY);
                caldroidFragment.setBackgroundDrawableForDate(colorDrawable, programDate);
                caldroidFragment.setTextColorForDate(R.color.white, programDate);
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        caldroidFragment.setCaldroidListener(caldroidListener);

        L.l("TOTAL PRO : " + totalProgram + " NOMITATED : " + nominatedProgram + " NOT NOMINATED : " + (totalProgram - nominatedProgram));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }


    @Override
    public void request(String url) {
        progressDialog = new CustomProgressDialog(getActivity(),"");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();

                try {
                    Log.e("=========",response.toString());
                    updateDisplay(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
           if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                JSONArray eventArray = jsonObject.getJSONArray("events");
                if (eventArray.length() > 0) {
                    for (int i = 0; i < eventArray.length(); i++) {
                        Event event = new Gson().fromJson(String.valueOf(eventArray.getJSONObject(i)), Event.class);
                        eventList.add(event);
                    }
                    //showCalendar();
                    mCalendarView.setListener(this);
                    mCalendarView.addEvents(getEvents());
                    mCalendarView.setShowInactiveDays(true);
                } else {
                    tvTrainingCalendarFragmentNRF.setVisibility(View.VISIBLE);
                    rvEventListFragment.setVisibility(View.GONE);
                   // llCalendarView.setVisibility(View.GONE);
                }
            } else {
                tvTrainingCalendarFragmentNRF.setVisibility(View.VISIBLE);
                rvEventListFragment.setVisibility(View.GONE);
               // llCalendarView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void myCallback(int position) {

    }
    private List<EventNew> getEvents() {

        List<EventNew> events = new ArrayList<>();
        for (int i = 0; i < eventList.size(); i++) {
            events.add(new EventNew("Event " + (1 + 1), Long.parseLong(eventList.get(i).getEventFromdate()) * 1000L));
        }
        return events;
    }


    @Override
    public void myCallback(int position, String tag) {
        if (eventList2.size() > 0) {
            EventDetailsFragment fragment = new EventDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.event_parcelable_tag), eventList2.get(position));
            fragment.setArguments(bundle);
            mainActivity.replaceFrgament(fragment);
        }
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    @Override
    public void onDateSelected(LocalDate date, List<EventNew> events) {

        eventList2.clear();
        for (Event event : eventList) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(event.getEventFromdate()) * 1000L);
            String date1 = DateFormat.format("yyyy-MM-dd", cal).toString();
            if (date1.equals(""+date)) {
                eventList2.add(event);
            }
        }
        if (eventList2.size() > 1) {
            tvNoEventFound.setVisibility(View.GONE);
            rvEventListFragment.setVisibility(View.VISIBLE);
            eventsListAdapter.setEventList(eventList2);
            eventsListAdapter.notifyDataSetChanged();
        } else if (eventList2.size() == 1) {
            tvNoEventFound.setVisibility(View.GONE);
            rvEventListFragment.setVisibility(View.VISIBLE);
            eventsListAdapter.setEventList(eventList2);
            eventsListAdapter.notifyDataSetChanged();
        } else {
            eventList2.clear();
            tvNoEventFound.setVisibility(View.VISIBLE);
            rvEventListFragment.setVisibility(View.GONE);
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText("No event on selected date.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            customDialog.dismiss();
                        }
                    }).show();
        }

    }

    @Override
    public void onMonthChanged(LocalDate date) {

    }

    @Override
    public void onHeaderClick() {

    }
}
