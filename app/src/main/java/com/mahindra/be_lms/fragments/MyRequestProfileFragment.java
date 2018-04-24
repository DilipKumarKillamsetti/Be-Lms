package com.mahindra.be_lms.fragments;

/**
 * Created by training1 on 11/10/16.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.RequestProfileAdapter;
import com.mahindra.be_lms.db.Profile;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestProfileFragment extends Fragment implements Callback {
    private static final String TAG = MyRequestProfileFragment.class.getSimpleName();
    @BindView(R.id.rvMyRequestProfileFragmentList)
    RecyclerView rvMyRequestProfileFragmentList;
    @BindView(R.id.tvMyRequestProfileFragmentRecordNotFound)
    TextView tvMyRequestProfileFragmentRecordNotFound;
    private RequestProfileAdapter adapter;
    private List<Profile> profileList;
    private String pr_id;
    private MainActivity mainActivity;


    public MyRequestProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_request_profile, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    private void init() {
        DBHelper dbHelper = new DBHelper();
        profileList = new ArrayList<>();
        profileList = dbHelper.getRequestProfileList();
        L.l(TAG, "PROFILE: " + profileList.toString());
        //profileList.add(0, new Profile());
        rvMyRequestProfileFragmentList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMyRequestProfileFragmentList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rvMyRequestProfileFragmentList.setHasFixedSize(true);
        adapter = new RequestProfileAdapter(getActivity(), profileList);
        adapter.setMyCallback(this);
        rvMyRequestProfileFragmentList.setAdapter(adapter);
    }


    public void request(String url, final int position) {
        L.pd("please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(getActivity(), "RESPONSE : " + response.toString());
                try {
                    updateDisplay(response, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", Constants.REQUEST_PROFILE);
                param.put("cmb_user_id", new MySharedPreference(getActivity()).getUserId());
                param.put("cmb_profile_id", pr_id);
                /*param.put("old_password", L.getText(etChangePasswordOldPass));
                param.put("new_password", L.getText(etChangePasswordNewPass));*/

                L.l(getActivity(), "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response, int position) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                L.l(response);
                Profile profile = profileList.get(position);
                profile.setProfile_requested(true);
                new DBHelper().setRequestedProfile(profile);
                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText("Request sent successfully")
                        .show();
            } else {
                //  L.t(jsonObject.getString("message"));
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                            }
                        }).show();
            }
        }
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
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void myCallback(int position) {

    }

    @Override
    public void myCallback(final int position, final String tag) {
        final Profile profile = adapter.profileList.get(position);
        L.l("CALLBACK REQEST PROFILE " + profile.toString());

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.dialog_send_profile))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        String url = Constants.LMS_URL;
                        pr_id = tag;
                        Log.e("profile_id request : ", " fragment " + tag);
                        if (L.isNetworkAvailable(getActivity())) {
                            request(url, position);
                        } else {
                            //L.t(getString(R.string.err_network_connection));
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_network_connection))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                                        }
                                    }).show();
                        }


                    }
                }).setNegativeButton(getString(R.string.dialog_no), null).show();
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }

}

