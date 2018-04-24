package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfileActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.db.Location;
import com.mahindra.be_lms.db.Qualification;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.mahindra.be_lms.util.DateManagement;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileViewFragment extends Fragment {

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

    public ProfileViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        // init();
        return view;
    }

    private void init() {

        Log.d(TAG, "init: ");
        // mytoolbar.inflateMenu(R.menu.user_menu);
        //  mytoolbar.setOnMenuItemClickListener(this);
        getData();
        PackageManager m = getActivity().getPackageManager();
        String s = getActivity().getPackageName();
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f7b307")));
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            L.l(TAG, "app path: " + s);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    private void getData() {
        Log.d(TAG, "getData: ");
        L.l(getActivity(), "[METHOD:getData] fetch data from sqlite companylist,designationlist,locationlist");
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
        mytoolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.Black));
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
                Picasso.with(getActivity())
                        .load(user.getUserPicture()+"&token="+MyApplication.mySharedPreference.getUserToken())
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
                new CommonFunctions().hideSoftKeyboard(getActivity());
                break;
            case R.id.iv_Profile:
               /* if (Utility.checkExternalStoragePermission(getActivity())) {
                    selectImage();
                } else {
                    Utility.callExternalStoragePermission(getActivity());
                }*/
                startActivity(new Intent(getActivity(), ProfilePictureActivity.class));
                break;
          /*  case R.id.ivDesignation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileDesignation.performClick();
                break;
            case R.id.ivLocation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileLocation.performClick();
                break;*/
            case R.id.footertext:
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.replaceFrgament(new HomeFragment());
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        L.l(TAG, "OnResume");
        try {
            mainActivity.getSupportActionBar().hide();
            Log.d(TAG, "onResume: MEthod call");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                user = new DBHelper().getUser();
                init();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    @OnClick(R.id.floatingActionButtonEditProfile)
    public void OnClickEditButton() {
        L.l(TAG, "In click view");
        startActivity(new Intent(getActivity(), ProfileActivity.class));
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result=Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                callUpdateProfile(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callUpdateProfile(Bitmap bm) {
        try {
            bitmapImg = bm;
            strProfile = ImageHelper.encodeToBase64(bm, Bitmap.CompressFormat.PNG, 20);
            L.l(TAG, "Base64 IMAGE String: " + strProfile);
            if (L.isNetworkAvailable(getActivity())) {
                if (!TextUtils.isEmpty(strProfile)) {
                    updateProfileImage();
                } else {
                    //L.t("Error while capturing image. Please try again");
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_profile_image_capture_msg))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.dismiss();
                                }
                            }).show();
                }
            } else {
                //  L.t("Please check internet connectivity");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProfileImage() {
        L.pd("Updating profile image", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "PROFILE IMG UPDATE RESPONSE: " + response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                            iv_Profile.setImageBitmap(bitmapImg);
                            //L.t("Image updated successfully");
                            new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setContentText(getString(R.string.profile_image_updated_msg))
                                    .setCustomImage(R.drawable.success_circle)
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            //ChangeMobileNumberActivity.this.finish();
                                        }
                                    }).show();
                            user.setUserPicture(user.getUserID() + ".png");
                            new DBHelper().updateUser(user);
                            boolean fileSave = new ImageHelper().createDirectoryAndSaveFile(bitmapImg, user.getUserID() + ".png");
                            MyApplication.flagProfilePicSet = fileSave;
                            L.l(TAG, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                        } else {
                            //L.t(jsonObject.getString("message"));
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "Profile IMG VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.UPDATE_PROFILE_IMAGE_ACTION);
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("profile_pic", strProfile);
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        callUpdateProfile(thumbnail);
        // iv_Profile.setImageBitmap(thumbnail);
    }
}
