package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Notifications;
import com.mahindra.be_lms.db.Profile;
import com.mahindra.be_lms.db.Queries;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            Intent intent = null;
            DBHelper dbHelper = new DBHelper();
            for (String key : getIntent().getExtras().keySet()) {
                Log.d(TAG, "onCreate: KEY: " + key + " VALUE: " + getIntent().getExtras().get(key));
            }
            String notificationType = null;
//            if (getIntent().getExtras().containsKey("type")) {
//                if (getIntent().getExtras().getString("type").equals("query_replay")) {
//                    Log.d(TAG, "onCreate: query type");
//                    QueryResponse queryResponse = new QueryResponse();
//                    queryResponse.setQueryID(getIntent().getExtras().getString("query_id"));
//                    queryResponse.setMessage(getIntent().getExtras().getString("message"));
//                    queryResponse.setTitle(getIntent().getExtras().getString("title"));
//                    queryResponse.setMsg_type("response");
//                    queryResponse.setResposePerson(getIntent().getExtras().getString("responsible_person"));
//                    if (getIntent().getExtras().containsKey("attachmentLink")) {
//                        L.l(TAG, "Reply attachment present");
//                        queryResponse.setQueryReplyAttachment(getIntent().getExtras().getString("attachmentLink"));
//                    } else {
//                        L.l(TAG, "Reply attachment absent");
//                        queryResponse.setQueryReplyAttachment("");
//                    }
//                     /*  {query_id=69, type=query_replay, title=Reply for Query,
//              extra_link={"144":{"Radiator cleaner and antifreeze recommendation":"1310151038260R.pdf"}},
//              message=doc link, notificationType=3, responsible_person=MADMIN} */
//                    if (getIntent().getExtras().containsKey("notificationType")) {
//                        queryResponse.setQueryResponseType(getIntent().getExtras().getString("notificationType"));
//                    } else {
//                        queryResponse.setQueryResponseType("");
//                    }
//                    if (getIntent().getExtras().containsKey("extra_link")) {
//                        queryResponse.setQueryResponseExtraLink(getIntent().getExtras().getString("extra_link"));
//                    } else {
//                        queryResponse.setQueryResponseExtraLink("");
//                    }
//                    dbHelper.saveResponse(queryResponse);
//                    Notifications notifications = new Notifications();
//                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
//                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
//                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
//                    dbHelper.saveNotifications(notifications);
//                    notificationType = "query_notification";
//                } else if(getIntent().getExtras().getString("type").equals("Query_close")){
////                    Queries queries=dbHelper.getQueriesByInsertedID(getIntent().getExtras().getString("queryid"));
////                    queries.setQueryStatus(true);
////                    dbHelper.updateQuery(queries);
////                    Notifications notifications = new Notifications();
////                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
////                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
////                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
////                    dbHelper.saveNotifications(notifications);
////                    notificationType = "query_notification";
//
//                }else if (getIntent().getExtras().getString("type").equals("request_for_profile")) {
//                    Log.d(TAG, "onCreate: profile request");
//                    Notifications notifications = new Notifications();
//                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
//                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
//                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
//                    dbHelper.saveNotifications(notifications);
//                    Profile profile = dbHelper.getProfileByID(getIntent().getExtras().getString("profile_id"));
//                    profile.setProfile_approval(true);
//                    dbHelper.updateProfile(profile);
//                    notificationType = "request_for_profile_notification";
//                } else if (getIntent().getExtras().getString("type").equals("profile_update")) {
//                    Log.d(TAG, "onCreate: profile update");
//                    new DBHelper().deleteUser();
//                    User user = new Gson().fromJson((String) getIntent().getExtras().get("data"), User.class);
//                    L.l(TAG, "updated user: " + user.toString());
//                    new DBHelper().saveUser(user);
//                    MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());
//                    MyApplication.mySharedPreference.setUniqueID(user.getUsername());
//                    if (!TextUtils.isEmpty(user.getUserPicture())) {
//                        L.l(this, "Profile Img Exists");
//                        ///Bitmap bitmap=new ImageHelper().getImageBitmapFromUrl(Constants.PROFILE_UPLOAD_URL+ user.getUserPicture());
//                        String url = Constants.PROFILE_UPLOAD_URL + user.getUserPicture();
//                        L.l(this, "PROFILE IMAGE URL: " + url);
//                        getImageBitmap(url, user.getUserPicture());
//                    }
//                    Notifications notifications = new Notifications();
//                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
//                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
//                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
//                    dbHelper.saveNotifications(notifications);
//                    notificationType = "profile_update_notification";
//                } else if (getIntent().getExtras().getString("type").equals("General")) {
//                    //{type=General, title=412017, attachmentLink=uploads/notification_attachment/aboutpkm_Tulips.jpg, message=Testing, notificationType=2}
//               /* {type=General, title=Demo, attachmentLink=uploads/notification_attachment/aboutpkm_Desert.jpg, message=asfdsf, notificationType=1}*/
//                /*{type=General, title=demooooooo testing, attachmentLink=, message=Hellllllllloooooo, notificationType=1}*/
//            /*                {type=General, title=testing video attachments, attachmentLink=,
//extra_link={"141":{"LMS initial registration":"1305160840540R.pdf"}}, message=testing video attachments, notificationType=3}*/
//
//                    L.l(TAG, "General Notification: ");
//                    Notifications notifications = new Notifications();
//                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
//                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
//                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
//                    notifications.setNotificationattachLink(getIntent().getExtras().getString("attachmentLink"));
//                    notifications.setNotificationOtherType(getIntent().getExtras().getString("notificationType"));
//                    if (getIntent().getExtras().getString("notificationType").equals("3")) {
//                        notifications.setNotificationextraLink(getIntent().getExtras().getString("extra_link"));
//                    } else {
//                        if (getIntent().getExtras().containsKey("extra_link")) {
//                            notifications.setNotificationextraLink("");
//                        } else {
//                            notifications.setNotificationextraLink("");
//                        }
//                    }
//                    new DBHelper().saveNotifications(notifications);
//                    notificationType = "General";
//                    if (getIntent().getExtras().getString("notificationType").equals("2")) {
//                        logoutUser();
//                    }
//                    MyApplication.mySharedPreference.setNotificationType(getIntent().getExtras().getString("notificationType"));
//                } else {
//                    Log.d(TAG, "onCreate: other");
//                    Notifications notifications = new Notifications();
//                    notifications.setNotificationType(getIntent().getExtras().getString("type"));
//                    notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
//                    notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
//                    dbHelper.saveNotifications(notifications);
//                    notificationType = "other_type_notification";
//                }
//            }
/*else {
                Log.d(TAG, "onCreate: no type");
                Notifications notifications = new Notifications();
                notifications.setNotificationType("no_type");
                notifications.setNotificationTitle(getIntent().getExtras().getString("title"));
                notifications.setNotificationMsg(getIntent().getExtras().getString("message"));
                dbHelper.saveNotifications(notifications);
                notificationType = "no_type_available_notification";
            }*/
           /* if (MyApplication.mySharedPreference.checkUserLogin()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(notificationType, true);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();*/
            L.l(SplashActivity.this, "Before Run method");
            L.l(SplashActivity.this, "In Run method: ");
//            if (MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE) == null || !MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE).equals(DateManagement.getCurrentDate())) {
//                Intent i = new Intent(SplashActivity.this, SyncMasterActivity.class);
//                startActivity(i);
//                finish();
//
//            } else {
                if (MyApplication.mySharedPreference.checkUserLogin()) {
                    if (MyApplication.mySharedPreference.getNotificationType().equals("2")) {
                        L.l(TAG, "Notification Type: for logout user ");
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();

                    } else {
                        intent = new Intent(SplashActivity.this, DashboardActivity.class);
                        intent.putExtra(notificationType, true);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
          //  }
        } else {
            MyApplication.flagProfilePicSet = true;
            new CommonFunctions().hideSoftKeyboard(SplashActivity.this);
            L.l(SplashActivity.this, "Before Run method");
            L.l(SplashActivity.this, "In Run method: ");
            checkUserLogin();
//            if (MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE) == null || !MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE).equals(DateManagement.getCurrentDate())) {
//                Intent i = new Intent(SplashActivity.this, SyncMasterActivity.class);
//                startActivity(i);
//                finish();
//
//            } else {
//                L.l(SplashActivity.this, "In Run else method");
//                checkUserLogin();
//            }
        }
    }

    private void logoutUser() {
        try {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearUserData();
            MyApplication.mySharedPreference.setUserLogin(false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
            MyApplication.mySharedPreference.setNotificationType("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //new UserLogout().execute();
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(this, "PROFILE PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    L.l("BITMAP not NULL: " + response.toString());
                    boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, MyApplication.mySharedPreference.getUserId() + ".png");
                    //L.l(this, "" + imagcreated);
                    MyApplication.flagProfilePicSet = imagcreated;
                    L.l("PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                } else {
                    L.dismiss_pd();
                    L.l("Bitmap NULL");
                }
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        L.l("PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        imgRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }

    private void checkUserLogin() {
        L.l(SplashActivity.this, "In checkUserLogin method");
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            L.l(SplashActivity.this, "In checkUserLogin if method");
            Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        } else {
            L.l(SplashActivity.this, "In checkUserLogin else method");
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
        L.l(SplashActivity.this, "In checkUserLogin m END method");
    }

    private class UserLogout extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                DBHelper dbHelper = new DBHelper();
                dbHelper.clearUserData();
                MyApplication.mySharedPreference.setUserLogin(false);
                MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
                MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
                MyApplication.mySharedPreference.setNotificationType("");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            L.pd("Logging out", SplashActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            L.dismiss_pd();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();

        }
    }
}
