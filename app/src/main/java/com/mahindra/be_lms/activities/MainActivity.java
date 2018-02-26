package com.mahindra.be_lms.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.fragments.DocumentTreeFragment;
import com.mahindra.be_lms.fragments.HomeFragment;
import com.mahindra.be_lms.fragments.HomeMenuFragment;
import com.mahindra.be_lms.fragments.LearnTestQuizFragment;
import com.mahindra.be_lms.fragments.MostViewedFragment;
import com.mahindra.be_lms.fragments.MyQueriesFragment;
import com.mahindra.be_lms.fragments.MyTrainingPassportWebViewFragment;
import com.mahindra.be_lms.fragments.NewsAndUpdateFragment;
import com.mahindra.be_lms.fragments.ProfileFragment;
import com.mahindra.be_lms.fragments.ProfileViewFragment;
import com.mahindra.be_lms.fragments.SurveyFeedbackFragment;
import com.mahindra.be_lms.fragments.TestPaperFragment;
import com.mahindra.be_lms.fragments.TrainingCalendarWebviewFragment;
import com.mahindra.be_lms.fragments.UploadToServerFragment;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private CircularImageView header_imageView;
    private TextView header_name, header_mob_no;
    private User user;
    private Fragment fragment;
    private EditText etChangeMobileNoDialogMobileNo;
    private int currentWeekOfYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        //exportDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.mobile_icon);
        TextView textView = (TextView) findViewById(R.id.tvToolBar);
        TextView textView1 = (TextView) findViewById(R.id.tvToolBar1);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        header_imageView = (CircularImageView) view.findViewById(R.id.header_imageView);
        header_name = (TextView) view.findViewById(R.id.header_name);
        //header_name.setTextSize(16);
        header_mob_no = (TextView) view.findViewById(R.id.header_mob_no);
       // header_mob_no.setTextSize(16);
        header_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileViewFragment profileViewFragment = new ProfileViewFragment();
                replaceFrgament(profileViewFragment);
                drawer.closeDrawers();
            }
        });
        Log.d(TAG, "onCreate: Check USer LOGIN and Load User profile");
        setUserProfile();
//        textView.setText("UID: " + user.getUsername());
//        textView1.setText("Welcome " + user.getUserFirstName());
        textView.setText( user.getUserFirstName());
        textView1.setText("Welcome");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                new CommonFunctions().hideSoftKeyboard(MainActivity.this);
                if (!MyApplication.mySharedPreference.checkUserLogin()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    setUserProfile();
                }
            }
        };
        try {
            drawer.setDrawerListener(toggle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        try {
            navigationView.setNavigationItemSelectedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        if (intent != null) {
            L.l("Intent not Null");
            if (intent.getBooleanExtra("query_notification", false)) {
                L.l(this, "notification");
                replaceFrgament(new MyQueriesFragment());
            } else if (intent.getBooleanExtra("profile_update_notification", false)) {
                L.l(this, "profile_update_notification");
                replaceFrgament(new ProfileViewFragment());
            } else if (intent.getBooleanExtra("General", false)) {
                L.l(this, "General");
                startActivity(new Intent(this, NotificationActivity.class).putExtra("comeFromMainActivity", true));
            } else {
                replaceFrgament(new HomeFragment());
            }
        }
    }

    public void setDrawerEnabled(boolean enabled) {
        enabled = true;
        Log.d(TAG, "setDrawerEnabled: " + enabled);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);

    }

    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.kriosispl.lms" + "/databases/" + Constants.DB_NAME;
        String backupDBPath = Constants.DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        L.l(this, "Permission callback called-------");
        switch (requestCode) {
            case Utility.PERMISSION_EXTERNAL_STORAGE: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        L.l(this, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        L.l(this, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            showDialogOK("External Storage Permission required for select picture from gallery and capture image from camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Utility.callExternalStoragePermission(MainActivity.this);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });

                        } else {
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            L.l(this, "Go to settings and enable permissions");
                            //proceed with logic by disabling the related features or quit the app.
                            String message = "";
                            if (fragment instanceof ProfileFragment) {
                                message = Utility.PROFILE_PIC_MSG;
                            } else if (fragment instanceof TestPaperFragment) {
                                message = Utility.DOWNLOAD_FILE_MSG;
                            } else if (fragment instanceof DocumentTreeFragment) {
                                message = Utility.DOWNLOAD_FILE_MSG;
                            } else if (fragment instanceof UploadToServerFragment) {
                                message = Utility.UPLOAD_FILE_MSG;
                            }
                            showDialogOK("External Storage Permission required for " + message + " please enable from setting",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    L.l(this, "Permission NOT granted");
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                // .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    /**
     * onBackPressed handle fragment backstack
     * and remove fragment from backstack
     * show AlertDialog for exit app
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof ProfileViewFragment) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFrgament(new HomeFragment());

            } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof MyTrainingPassportWebViewFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof MostViewedFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof SurveyFeedbackFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof TrainingCalendarWebviewFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof UploadToServerFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof MyQueriesFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());

        } else if (getSupportFragmentManager().findFragmentById(R.id.myContainer) instanceof NewsAndUpdateFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            recreate();

        }else {
            L.l("fragment Count in backstack: " + getSupportFragmentManager().getBackStackEntryCount());
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();

            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.dialog_exit_msg))
                        .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                               /* Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);/*//***Change Here***
                                 startActivity(intent);*/
                                finish();
                                // System.exit(0);
                            }
                        }).setNegativeButton(getString(R.string.dialog_no), null).show();
            }
        }
    }

    /*
    navigation menu onclick
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            //replaceFrgament(new ProfileFragment());
            //showChangeMobileNumberDialog();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFrgament(new HomeFragment());
        } else if (id == R.id.nav_change_mobile_no) {
            //replaceFrgament(new MyTrainingPassportFragment());
            // showChangeMobileNumberDialog();
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.nav_my_queries) {
            replaceFrgament(new MyQueriesFragment());
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(this, NotificationActivity.class));
            //replaceFrgament(new ProductFragment());
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutUsActivity.class).putExtra("about", "about"));
            //replaceFrgament(new LearnTestQuizFragment());
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(this, UserHelpActivity.class)
                    .putExtra("help_type", "contact_admin")
                    .putExtra("contact_to_admin_fromHome", true));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(this, UserHelpActivity.class).putExtra("help_type", "faq"));
            // startActivity(new Intent(this, FAQActivity.class));
            //replaceFrgament(new TrainingCalendarFragment());
        } else if (id == R.id.nav_logout) {
            logOut();
        }
//        else if (id == R.id.nav_disclaimer) {
//            //replaceFrgament(new MyTrainingPassportFragment());
//            startActivity(new Intent(this, DisclaimerActivity.class).putExtra("fromMainActivity", true));
//        } else if (id == R.id.nav_safety) {
//            startActivity(new Intent(this, SafetyAndWarningActivity.class).putExtra("fromMainActivity", true));
//            //replaceFrgament(new MostViewedFragment());
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logOut() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.dialog_logout_msg))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        DBHelper dbHelper = new DBHelper();
                        dbHelper.clearUserData();
                        MyApplication.mySharedPreference.setUserLogin(false);
                        MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
                        MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
                        MyApplication.mySharedPreference.setNotificationType("");
                        WebViewCachePref.newInstance(MainActivity.this).clearAllPref();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }).setNegativeButton(getString(R.string.dialog_no), null).show();
    }

    /**
     * replace fragment on main activity
     * with animation slid in slid out using fragmentTransaction
     * fragment add in backstack
     */
    public void replaceFrgament(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        String fragmentTag = fragment.getClass().getSimpleName();
        //set fragment for store current fragment instance on mainActivity
        setFragment(fragment);

        L.l(this, "Fragment SIMPLE NAME : " + fragmentTag);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.myContainer, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();

    }

    private void setUserProfile() {
        user = new DBHelper().getUser();
        L.l(this, "USER OBJECT: " + user);
        L.l(this, "PROFIEL PIC FLAG: " + MyApplication.flagProfilePicSet);
        header_name.setText(user.getFullname());
        header_mob_no.setText(user.getUserMobileNo());
        //File file = new File(new File("/sdcard/PKB/userProfile/"), user.getUserID()+".png");

        boolean isFilexists = new ImageHelper().isFileExists(user.getUserID() + ".png");
        if (!user.getUserPicture().isEmpty()) {
            Picasso.with(MainActivity.this)
                    .load(user.getUserPicture()+"&token="+MyApplication.mySharedPreference.getUserToken())
                    .resize(200, 200)
                    .placeholder(getResources().getDrawable(R.drawable.ic_profile))
                    .centerCrop()
                    .into(header_imageView);
            Log.e("IS FILEEXIST FLAG: ","isFilexists");
        } else {
            Log.e("IS FILEEXIST FLAG: ","notFilexists");
            L.l(this, "Set Dummy Bitmap to header");
            header_imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
        }
        L.l(this, "PROFIEL PIC FLAG SET TO HEADER : " + MyApplication.flagProfilePicSet);
       /* if (!TextUtils.isEmpty(user.getUserPicture())) {
            String url = Constants.PROFILE_UPLOAD_URL + user.getUserPicture();
            L.l(MainActivity.this.getLocalClassName(), "IMAGE URL: " + url);
            Picasso.with(this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).skipMemoryCache().into(header_imageView);
        } else {
            header_imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
        }*/
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*  @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.uid_menu, menu);
          MenuItem menu_uid = menu.findItem(R.id.menu_uid);
          menu_uid.setTitle("Welcome "+user.getUserFirstName()+"\nUID: " + user.getUsername());//+"\nMo: "+user.getUserMobileNo());
          return super.onCreateOptionsMenu(menu);
      }
  */
    /*
    Show AlertDialog for Change mobile number
     */
    private void showChangeMobileNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_change_mobile_number_dialog_layout, null);
        builder.setView(dialogView)
                .setTitle(getString(R.string.dialog_change_mobile_number_title))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .setPositiveButton(getString(R.string.dialog_submit), null);
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btn_positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setTransformationMethod(null);
                Button btn_negative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setTransformationMethod(null);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etChangeMobileNoDialogMobileNo = (EditText) dialogView.findViewById(R.id.etChangeMobileNoDialogMobileNo);
                        if (MyValidator.isValidUniqueID(etChangeMobileNoDialogMobileNo)) {
                            if (L.isNetworkAvailable(MainActivity.this)) {
                                d.dismiss();
                                changeMobileNumberRequest(Constants.LMS_URL);
                            } else {
                                new PKBDialog(MainActivity.this, PKBDialog.WARNING_TYPE)
                                        .setContentText(getString(R.string.err_network_connection))
                                        .setConfirmText("OK")
                                        .show();
                            }
                        }
                    }
                });
            }
        });
        d.show();
    }

    private void changeMobileNumberRequest(String forgoturl) {
        L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forgoturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(MainActivity.this, "Change mobile number RESPONSE : " + response.toString());
                try {
                    changeMobileNumberUpdateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(MainActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(MainActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", "change_mobile_no");
                param.put("username", L.getText(etChangeMobileNoDialogMobileNo));
                L.l(MainActivity.this, "PARAM : " + param.toString());
                return VolleySingleton.checkRequestparam(param);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: MEthod call");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void changeMobileNumberUpdateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                // L.t(jsonObject.getString("message"));
                new PKBDialog(MainActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText(jsonObject.getString("message"))
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                new DBHelper().deleteUser();
                                MyApplication.mySharedPreference.setUserLogin(false);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).show();

//                Intent otpIntent = new Intent(LoginActivity.this, OTPActivity2.class);
//                otpIntent.putExtra("from", "forgot");
//                otpIntent.putExtra("mobileno", L.getText(etChangeMobileNoDialogMobileNo));
//                startActivity(otpIntent);
//                finish();
            } else {
                // L.t(jsonObject.getString("message"));
                new PKBDialog(MainActivity.this, PKBDialog.WARNING_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    /*private void launchMarket() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("weekOfYear", currentWeekOfYear);
        editor.commit();
        Uri uri = Uri.parse("market://details?id=" + "com.mahindra.be_lms");
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }*/
}
