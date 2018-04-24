package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.db.Location;
import com.mahindra.be_lms.db.Qualification;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.fragments.HomeFragment;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.util.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by Dell on 2/26/2018.
 */

public class ViewProfileDetails extends AppCompatActivity {

    private static final String TAG = "ProfileViewFragment";
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    @BindView(R.id.profileToolBar)
    Toolbar mytoolbar;
    @BindView(R.id.tvFullname)
    TextView tvFullname;
    @BindView(R.id.tvMobileno)
    TextView tvMobileno;
    @BindView(R.id.tvEmailID)
    TextView tvEmailID;
    @BindView(R.id.tvCompany)
    TextView tvCompany;
    @BindView(R.id.tvDesignation)
    TextView tvDesignation;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvDOB)
    TextView tvDOB;
    @BindView(R.id.iv_Profile)
    CircularImageView iv_Profile;
    @BindView(R.id.tvuserProfilesRole)
    TextView tvuserProfilesRole;
    @BindView(R.id.tvuserProfilesGroup)
    TextView tvuserProfilesGroup;
    @BindView(R.id.floatingActionButtonEditProfile)
    FloatingActionButton fab;
    @BindView(R.id.tvCompanyDepartment)
    TextView tvCompanyDepartment;
    private DashboardActivity mainActivity;
    private List<Designation> designationList;
    private List<Location> locationList;
    private List<Qualification> qualificationList;
    private User user;
    private String userChoosenTask;
    private Bitmap bitmapImg;
    private String strProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        //init();
        ButterKnife.bind(this);
    }

    private void init() {

        Log.d(TAG, "init: ");
        // mytoolbar.inflateMenu(R.menu.user_menu);
        //  mytoolbar.setOnMenuItemClickListener(this);
        getData();
//        PackageManager m = getActivity().getPackageManager();
//        String s = getActivity().getPackageName();
//        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f7b307")));
//        try {
//            PackageInfo p = m.getPackageInfo(s, 0);
//            s = p.applicationInfo.dataDir;
//            L.l(TAG, "app path: " + s);
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.w("yourtag", "Error Package name not found ", e);
//        }
    }

    private void getData() {
        Log.d(TAG, "getData: ");
        DBHelper dbHelper = new DBHelper();
        designationList = new ArrayList<>();
        designationList = dbHelper.getDesignationList();
        L.l(TAG, "PROFILE GET designationList: " + designationList.toString());
        locationList = dbHelper.getLocaitonList();
        L.l(TAG, "PROFILE GET locationList: " + locationList.toString());
        qualificationList = dbHelper.getQualificationList();
        L.l(TAG, "PROFILE GET qualificationList: " + qualificationList.toString());
        setData();
    }

    private void setData() {
        Log.d(TAG, "setData: ");
        L.l("USER : " + user.toString());
        mytoolbar.setTitle(user.getUserFirstName());
        mytoolbar.setTitleTextColor(getResources().getColor(R.color.Black));
        tvFullname.setText(user.getFullname() + " [ UID: " + user.getUsername() + " ]");
        if (!TextUtils.isEmpty(user.getUserMobileNo())) {
            tvMobileno.setText(user.getUserMobileNo());
        } else {
            tvMobileno.setText(getString(R.string.profile_data_not_set));
        }
        if (!TextUtils.isEmpty(user.getUserEmailID())) {
            tvEmailID.setText(user.getUserEmailID());
        } else {
            tvEmailID.setText(getString(R.string.profile_data_not_set));
        }
        if (!TextUtils.isEmpty(user.getUserRole())) {
            tvuserProfilesRole.setText(user.getUserRole());
        } else {
            tvuserProfilesRole.setText(getString(R.string.profile_data_not_set));
        }
        if (!TextUtils.isEmpty(user.getUserGroups())) {
            tvuserProfilesGroup.setText(user.getUserGroups());
        } else {
            tvuserProfilesGroup.setText(getString(R.string.profile_data_not_set));
        }
        if (!TextUtils.isEmpty(user.getUserOrgCode())) {
            tvCompanyDepartment.setText(user.getUserOrgCode());
        } else {
            tvCompanyDepartment.setText(getString(R.string.profile_data_not_set));
        }

        if (!TextUtils.isEmpty(user.getUserOrg())) {
            tvCompany.setText(user.getUserOrg());
        } else {
            tvCompany.setText(getString(R.string.profile_data_not_set));
        }
        L.l(TAG, "Location Size: " + locationList.size() + "ID: " + user.getUserLocationID() + "boolean: " + TextUtils.isEmpty(user.getUserLocationID()));

        if (!TextUtils.isEmpty(user.getUserLocationID())) {
            tvLocation.setText(user.getUserLocationID());
        } else {
            tvLocation.setText(getString(R.string.profile_data_not_set));
        }
        L.l(TAG, "Designation Size: " + designationList.size() + "ID: " + user.getUserDesignationID() + "boolean: " + TextUtils.isEmpty(user.getUserDesignationID()));
        if (!TextUtils.isEmpty(user.getUserDesignationID())) {
            tvDesignation.setText(user.getUserDesignationID());
        } else {
            tvDesignation.setText(getString(R.string.profile_data_not_set));
        }
        if (!TextUtils.isEmpty(user.getUserDOB())) {
            if (!user.getUserDOB().equals("01-01-1970")) {
                tvDOB.setText(DateManagement.getFormatedDate(user.getUserDOB(), DateManagement.DD_MM_YYYY, DateManagement.DD_MMMM_YYYY));
            } else {
                tvDOB.setText(getString(R.string.profile_data_not_set));
            }
        } else {
            tvDOB.setText(getString(R.string.profile_data_not_set));
        }

        if (!TextUtils.isEmpty(user.getUserPicture())) {
            boolean isFilexists = new ImageHelper().isFileExists(user.getUserID() + ".png");
            if (!user.getUserPicture().isEmpty()) {
                Picasso.with(ViewProfileDetails.this)
                        .load(user.getUserPicture()+"&token="+ MyApplication.mySharedPreference.getUserToken())
                        .resize(100, 100)
                        .placeholder(getResources().getDrawable(R.drawable.user_new))
                        .centerCrop()
                        .into(iv_Profile);

            } else {
                L.l(TAG, "Set Dummy Bitmap to header");
                iv_Profile.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
            }
            L.l(TAG, "PROFIEL PIC FLAG SET TO HEADER : " + MyApplication.flagProfilePicSet);
        }
    }

    @OnTouch({R.id.coordinatorLayoutProfile, R.id.iv_Profile, R.id.footertext})
    public boolean onTouchProfile(View view) {
        Log.d(TAG, "onTouchProfile: ");
        switch (view.getId()) {
            case R.id.coordinatorLayoutProfile:
                new CommonFunctions().hideSoftKeyboard(ViewProfileDetails.this);
                break;
            case R.id.iv_Profile:
               /* if (Utility.checkExternalStoragePermission(getActivity())) {
                    selectImage();
                } else {
                    Utility.callExternalStoragePermission(getActivity());
                }*/
                startActivity(new Intent(ViewProfileDetails.this, ProfilePictureActivity.class));
                break;
          /*  case R.id.ivDesignation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileDesignation.performClick();
                break;
            case R.id.ivLocation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileLocation.performClick();
                break;*/

        }
        return false;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        L.l(TAG, "OnResume");
        try {
           // mainActivity.getSupportActionBar().hide();
            Log.d(TAG, "onResume: MEthod call");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(ViewProfileDetails.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                user = new DBHelper().getUser();
                init();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.floatingActionButtonEditProfile)
    public void OnClickEditButton() {
        L.l(TAG, "In click view");
        startActivity(new Intent(ViewProfileDetails.this, ProfileActivity.class));
    }

}
