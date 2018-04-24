package com.mahindra.be_lms.activities;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.NotificationsAdapter;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.model.ManualInfoModel;
import com.mahindra.be_lms.model.Notifications;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivity extends BaseActivity implements NetworkMethod {
    @BindView(R.id.rvNotificationActivityList)
    RecyclerView rvNotificationActivityList;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> notificationsList1;
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private String download_url;
    private long documentRefID;
    private boolean comeFromMainActivity;
    @BindView(R.id.tvNotificationActivityRNF)
    TextView tvNotificationActivityRNF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        comeFromMainActivity = getIntent().getBooleanExtra("comeFromMainActivity", false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Intent intent = getIntent();

        callApi();

        List<Notifications> notificationsList = new ArrayList<>();
        notificationsList1 = new ArrayList<>();
        setupList();

    }

    private void callApi() {
        if (L.isNetworkAvailable(NotificationActivity.this)) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvNotificationActivityList.setVisibility(View.VISIBLE);
            }

            L.pd(getString(R.string.dialog_please_wait), NotificationActivity.this);
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_LT_inbox&userid="+MyApplication.mySharedPreference.getUserId()+"&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvNotificationActivityList.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });

        }
    }

    private void setupList() {
        rvNotificationActivityList.setLayoutManager(new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false));
        rvNotificationActivityList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvNotificationActivityList.setHasFixedSize(true);
        notificationsAdapter = new NotificationsAdapter(this, notificationsList1);
        rvNotificationActivityList.setAdapter(notificationsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
//            List<Notifications> notificationsList = new ArrayList<>();
//            notificationsList = new DBHelper().getNotificationList();
//            notificationsList1 = new ArrayList<>();
//            notificationsList1 = notificationsList;
//            L.l(this, "Notification List: " + notificationsList.toString());
//            if (notificationsList.size() > 0) {
//                setupList(notificationsList);
//            } else {
//                findViewById(R.id.tvNotificationActivityRNF).setVisibility(View.VISIBLE);
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (comeFromMainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            finish();
        }
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_deleteall) {
//            if (findViewById(R.id.tvNotificationActivityRNF).getVisibility() == View.VISIBLE) {
//                PKBDialog pkbDialog = new PKBDialog(this, PKBDialog.WARNING_TYPE);
//                pkbDialog.setContentText(getString(R.string.notifications_not_available))
//                        .setConfirmText("OK")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                                NotificationActivity.this.finish();
//                            }
//                        }).show();
//                pkbDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                            dialog.dismiss();
//                        }
//                        return true;
//                    }
//                });
//            } else {
//                PKBDialog pkbDialog = new PKBDialog(this, PKBDialog.WARNING_TYPE);
//                pkbDialog.setContentText("Do you really want to delete all notifications")
//                        .setConfirmText("OK")
//                        .setCancelText("CANCEL")
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                new DBHelper().deleteNotifications();
//                                List<Notifications> notificationsList = new ArrayList<>();
//                                notificationsList = new DBHelper().getNotificationList();
//                                notificationsList1 = new ArrayList<Notifications>();
//                                notificationsList1 = notificationsAdapter.setupNotificationList(notificationsList);
//                                notificationsAdapter.notifyDataSetChanged();
//                                findViewById(R.id.tvNotificationActivityRNF).setVisibility(View.VISIBLE);
//                                customDialog.dismiss();
//                                NotificationActivity.this.finish();
//
//                            }
//                        }).setCancelClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                    @Override
//                    public void onClick(PKBDialog customDialog) {
//                        customDialog.dismiss();
//                    }
//                }).show();
//                pkbDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                            dialog.dismiss();
//                        }
//                        return true;
//                    }
//                });
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        if (comeFromMainActivity) {
            finishAffinity();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        if (comeFromMainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            startActivity(new Intent(this, MainActivity.class));
        } else {
            finish();
        }
    }


    @Override
    public void request(String url) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length()>0){
                    tvNotificationActivityRNF.setVisibility(View.GONE);
                    rvNotificationActivityList.setVisibility(View.VISIBLE);
                    for(int i = 0;i<response.length();i++){
                        try {
                            Notifications notifications = new Gson().fromJson(String.valueOf(response.get(i)), Notifications.class);
                            notificationsList1.add(notifications);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(notificationsList1.size()>0){
                        notificationsAdapter.setupNotificationList(notificationsList1);
                        notificationsAdapter.notifyDataSetChanged();
                    }else{

                    }
                }else{
                    tvNotificationActivityRNF.setVisibility(View.VISIBLE);
                    rvNotificationActivityList.setVisibility(View.GONE);
                }
                L.dismiss_pd();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(NotificationActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(NotificationActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        int socketTimeout = 30000;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);

    }
}
