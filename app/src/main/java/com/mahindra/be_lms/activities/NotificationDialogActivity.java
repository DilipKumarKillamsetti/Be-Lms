package com.mahindra.be_lms.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.DBHelper;

public class NotificationDialogActivity extends Activity {
    private String notificationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.getBooleanExtra("query_notification", false)) {
                    L.l(this, "notification");
                    //   replaceFrgament(new MyQueriesFragment());
                    notificationType = "query_notification";
                } else if (intent.getBooleanExtra("profile_update_notification", false)) {
                    L.l(this, "profile_update_notification");
                    notificationType = "profile_update_notification";
                    // replaceFrgament(new ProfileViewFragment());
                } else if (intent.getBooleanExtra("General", false)) {
                    L.l(this, "General");
                    notificationType = "General";
                    // startActivity(new Intent(this, NotificationActivity.class));
                    if (MyApplication.mySharedPreference.getNotificationType().equals("2")) {
                        logoutUser();
                    }
                } else {
                    // replaceFrgament(new HomeFragment());
                    notificationType = "no_type";
                    L.l("Notification Type In dialog: " + notificationType);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(MyApplication.mySharedPreference.getDialogTitle());
        alertDialog.setMessage(MyApplication.mySharedPreference.getDialogMessage());
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();*/
        if (MyApplication.mySharedPreference.getNotificationType().equals("2")) {
            PKBDialog dialog = new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE);
            dialog.setCustomImage(R.drawable.bell).setTitleText("Notification")
                    .setContentText("Title: " + MyApplication.mySharedPreference.getDialogTitle() + "\n" + "Message: " + MyApplication.mySharedPreference.getDialogMessage())
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            customDialog.dismiss();
                            if (notificationType.equalsIgnoreCase("General")) {
                                L.l(NotificationDialogActivity.this, "notification");
                                if (MyApplication.mySharedPreference.getNotificationType().equals("2")) {
                                    MyApplication.mySharedPreference.setNotificationType("");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        finishAffinity();
                                    }
                                    startActivity(new Intent(NotificationDialogActivity.this, LoginActivity.class));

                                }
                            }
                        }
                    }).show();
           /* dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        if (MyApplication.mySharedPreference.getNotificationType().equals("2")){
                            MyApplication.mySharedPreference.setNotificationType("");
                            startActivity(new Intent(NotificationDialogActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            NotificationDialogActivity.this.finish();
                        }
                    }
                    return true;
                }
            });*/
        } else {
            PKBDialog dialog = new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE);
            dialog.setCustomImage(R.drawable.bell).setTitleText("Notification")
                    .setContentText("Title: " + MyApplication.mySharedPreference.getDialogTitle() + "\n" + "Message: " + MyApplication.mySharedPreference.getDialogMessage())
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            customDialog.dismiss();
                            if (notificationType.equalsIgnoreCase("General")) {
                                L.l(NotificationDialogActivity.this, "notification");
                                if (MyApplication.mySharedPreference.getNotificationType().equals("3")) {
                                    startActivity(new Intent(NotificationDialogActivity.this, DashboardActivity.class).putExtra(notificationType, true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    NotificationDialogActivity.this.finish();
                                } else {
                                    startActivity(new Intent(NotificationDialogActivity.this, NotificationActivity.class));
                                    NotificationDialogActivity.this.finish();
                                }
                                // replaceFrgament(new MyQueriesFragment());
                            } else {
                                if(MyApplication.mySharedPreference.checkUserLogin()){
                                    startActivity(new Intent(NotificationDialogActivity.this, DashboardActivity.class).putExtra(notificationType, true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    NotificationDialogActivity.this.finish();
                                }else{
                                    NotificationDialogActivity.this.finish();
                                }

                            }
                        }
                    }).show();
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        NotificationDialogActivity.this.finish();
                    }
                    return true;
                }
            });
        }
    }

    private void logoutUser() {
        try {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearUserData();
            MyApplication.mySharedPreference.setUserLogin(false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
            WebViewCachePref.newInstance(this).clearAllPref();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (MyApplication.mySharedPreference.getNotificationType().equals("2")) {
            startActivity(new Intent(NotificationDialogActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            NotificationDialogActivity.this.finish();
        } else {
            NotificationDialogActivity.this.finish();
        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            L.pd("Logging out", NotificationDialogActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            L.dismiss_pd();
            startActivity(new Intent(NotificationDialogActivity.this, LoginActivity.class));
            finish();
        }
    }
}
