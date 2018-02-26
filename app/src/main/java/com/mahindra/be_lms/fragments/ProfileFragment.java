package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.db.Company;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.db.Location;
import com.mahindra.be_lms.db.Qualification;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.mahindra.be_lms.util.DateManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements NetworkMethod {
    private static final String DOB = "dob";
    private static final String DOJ = "doj";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    /*@BindView(R.id.profileToolBar)
    Toolbar mytoolbar;
    @BindView(R.id.profile_collapsing_layout)
    CollapsingToolbarLayout collapse_layout;
    @BindView(R.id.iv_Profile)
    CircularImageView iv_Profile;*/
    @BindView(R.id.etProfileFirstName)
    EditText etProfileFirstName;
    @BindView(R.id.etProfileLastName)
    EditText etProfileLastName;
    @BindView(R.id.etProfileEmail)
    EditText etProfileEmail;
    /*@BindView(R.id.spProfileDesignation)
    Spinner spProfileDesignation;*/
    @BindView(R.id.spProfileCompanyName)
    EditText spProfileCompanyName;
    /*@BindView(R.id.spProfileLocation)
    Spinner spProfileLocation;
    @BindView(R.id.spProfileQualification)
    Spinner spProfileQualification;*/
    @BindView(R.id.tvProfileDOB)
    TextView tvProfileDOB;

    /*@BindView(R.id.coordinatorLayoutProfile)
    CoordinatorLayout coordinatorLayoutProfile;
    @BindView(R.id.llprofile_list_layout)
     LinearLayout llprofile_list_layout;
     @BindView(R.id.tvProfileCount)
     TextView tvProfileCount;*/
    @BindView(R.id.autoTextProfileDesignation)
    AutoCompleteTextView autoTextProfileDesignation;
    @BindView(R.id.autoTextProfileLocation)
    AutoCompleteTextView autoTextProfileLocation;
    private String strProfile;
    private MainActivity mainActivity;
    private List<Company> companyList;
    private List<Designation> designationList;
    private List<Location> locationList;
    private List<Qualification> qualificationList;
    private User user;
    private String userChoosenTask;
    private Bitmap bitmapImg;
    private String selected_desigID, selected_locationID, selected_qualificationID;

    public ProfileFragment() {
        Log.d(TAG, "ProfileFragment: ");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_content_layout, container, false);
        ButterKnife.bind(this, view);
        // setHasOptionsMenu(true);
        L.l(TAG, "PROFIEL PIC FLAG: " + MyApplication.flagProfilePicSet);
        init();
        return view;
    }

    private void init() {
        Log.d(TAG, "init: ");
        // mytoolbar.inflateMenu(R.menu.user_menu);
        //  mytoolbar.setOnMenuItemClickListener(this);
        getData();
        PackageManager m = getActivity().getPackageManager();
        String s = getActivity().getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            L.l(TAG, "app path: " + s);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    /* @OnTouch({R.id.coordinatorLayoutProfile, R.id.iv_Profile, R.id.footertext})
     public boolean onTouchProfile(View view) {
         Log.d(TAG, "onTouchProfile: ");
         switch (view.getId()) {
             case R.id.coordinatorLayoutProfile:
                 new CommonFunctions().hideSoftKeyboard(getActivity());
                 break;
             case R.id.iv_Profile:
                 if (Utility.checkExternalStoragePermission(getActivity())) {
                     selectImage();
                 } else {
                     Utility.callExternalStoragePermission(getActivity());
                 }
                 //startActivity(new Intent(getActivity(), ProfilePictureActivity.class));
                 break;
           *//*  case R.id.ivDesignation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileDesignation.performClick();
                break;
            case R.id.ivLocation:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                spProfileLocation.performClick();
                break;*//*
            case R.id.footertext:
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.replaceFrgament(new HomeFragment());
                break;
        }
        return false;
    }
*/
    private void setData() {
        Log.d(TAG, "setData: ");
        user = new DBHelper().getUser();
        L.l("USER : " + user.toString());
        // mytoolbar.setTitle(user.getFullname());
        etProfileFirstName.setText(user.getUserFirstName());
        etProfileLastName.setText(user.getUserLastName());
        etProfileEmail.setText(user.getUserEmailID());
        int spinnerPosition = 0;
        if (!TextUtils.isEmpty(user.getUserOrg())) {
            spProfileCompanyName.setText(user.getUserOrg());
        }

        L.l(TAG, "Location Size: " + locationList.size());
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getLocationID().equalsIgnoreCase(user.getUserLocationID())) {
                L.l("Location Name : " + locationList.get(i).getLocationName());
                selected_locationID = locationList.get(i).getLocationID();
                autoTextProfileLocation.setText(locationList.get(i).getLocationName());
                //spinnerPosition = i;
            }
        }
        // L.l("loaction position : " + spinnerPosition);
        //spProfileLocation.setSelection(spinnerPosition);
        spinnerPosition = 0;

      /*  L.l("Qualification position : " + spinnerPosition);
        spProfileQualification.setSelection(spinnerPosition);*/

        L.l(TAG, "Designation Size: " + designationList.size());
        spinnerPosition = 0;
        for (int i = 0; i < designationList.size(); i++) {
            if (designationList.get(i).getDesigID().equalsIgnoreCase(user.getUserDesignationID())) {
                L.l("Designation Name : " + designationList.get(i).getDesigName());
                selected_desigID = designationList.get(i).getDesigID();
                autoTextProfileDesignation.setText(designationList.get(i).getDesigName());
            }
        }
       /* L.l("designation position : " + spinnerPosition);
        spProfileDesignation.setSelection(spinnerPosition);*/

        if (!TextUtils.isEmpty(user.getUserDOB())) {
            if (!user.getUserDOB().equals("01-01-1970")) {
                tvProfileDOB.setText(DateManagement.getFormatedDate(user.getUserDOB(), DateManagement.DD_MM_YYYY, DateManagement.DD_MMMM_YYYY));
            }
        }

       /* if (!TextUtils.isEmpty(user.getUserPicture())) {
            boolean isFilexists = new ImageHelper().isFileExists(user.getUserID() + ".png");
            if (isFilexists) {
                L.l(getActivity(), "IS FILEEXIST FLAG: " + isFilexists);
                Bitmap bitmap = new ImageHelper().decodeBitmapFromPath(Environment.getExternalStorageDirectory() + "/PKB/userProfile/" + user.getUserID() + ".png");
                L.l(TAG, "Bitmap " + bitmap);
                if (bitmap != null) {
                    L.l(getActivity(), "Set Bitmap to header");
                    iv_Profile.setImageBitmap(bitmap);
                } else {
                    L.l(TAG, "Bitmap NULL");
                }
            } else {
                L.l(TAG, "Set Dummy Bitmap to header");
                iv_Profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
            }
            L.l(TAG, "PROFIEL PIC FLAG SET TO HEADER : " + MyApplication.flagProfilePicSet);
        }*/
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        L.l(TAG, "OnResume");
        setData();
        try {
            mainActivity.getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        Log.d(TAG, "getData: ");
        L.l(getActivity(), "[METHOD:getData] fetch data from sqlite companylist,designationlist,locationlist");
        DBHelper dbHelper = new DBHelper();
        designationList = new ArrayList<>();
        designationList = dbHelper.getDesignationList();
        L.l(TAG, "PROFILE GET designationList: " + designationList.toString());
        // designationList.add(0, new Designation("0", "--- Select Designation ---"));
        locationList = dbHelper.getLocaitonList();
        L.l(TAG, "PROFILE GET locationList: " + locationList.toString());
        // locationList.add(0, new Location("0", "--- Select Location ---"));
        qualificationList = dbHelper.getQualificationList();
        L.l(TAG, "PROFILE GET qualificationList: " + qualificationList.toString());
        // qualificationList.add(0, new Qualification(0l, "0", "--- Select Qualification ---", ""));
        ArrayAdapter designatinAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, designationList);
        // designatinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //spProfileDesignation.setAdapter(designatinAdapter);
        //spProfileDesignation.setOnItemSelectedListener(this);
        autoTextProfileDesignation.setAdapter(designatinAdapter);
        autoTextProfileDesignation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.l(TAG, "Desig autocomplete Click: ");
                Designation designation = (Designation) parent.getAdapter().getItem(position);
                L.l(TAG, "selected desig: " + designation.getDesigID());
                selected_desigID = designation.getDesigID();
            }
        });
        ArrayAdapter locationAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, locationList);
        //locationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //spProfileLocation.setAdapter(locationAdapter);
        autoTextProfileLocation.setAdapter(locationAdapter);
        autoTextProfileLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.l(TAG, "location autocomplete Click: ");
                Location location = (Location) parent.getAdapter().getItem(position);
                L.l(TAG, "selected location: " + location.getLocationID());
                selected_locationID = location.getLocationID();
            }
        });

    }

    @OnClick({R.id.btn_profile_update, R.id.tvProfileDOB})
    public void onClickEvent(View v) {
        Log.d(TAG, "onClickEvent: ");
        switch (v.getId()) {
            case R.id.btn_profile_update:
                if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
                    if (validateFields()) {
                        user.setUserOrg(spProfileCompanyName.getText().toString());
                        user.setUserFirstName(L.checkNull(L.getText(etProfileFirstName), ""));
                        user.setUserLastName(L.checkNull(L.getText(etProfileLastName), ""));
                        user.setUserEmailID(L.getText(etProfileEmail));
                        user.setUserDesignationID(TextUtils.isEmpty(selected_desigID) ? "" : selected_desigID);
                        user.setUserQualificationID(TextUtils.isEmpty(selected_qualificationID) ? "" : selected_qualificationID);
                        if (!L.checkNull(L.getText(tvProfileDOB))) {
                            user.setUserDOB(DateManagement.getFormatedDate(L.getText(tvProfileDOB), DateManagement.DD_MMMM_YYYY, DateManagement.DD_MM_YYYY));
                        } else {
                            user.setUserDOB("");
                        }


                        user.setUserLocationID(TextUtils.isEmpty(selected_locationID) ? "" : selected_locationID);
                        L.l(TAG, "updated user: " + user.toString());
                        L.pd("Updating profile", "Please wait", getActivity());
                        request(Constants.LMS_URL);
                    }
                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }
                break;
            case R.id.tvProfileDOB:
                showDatePickerDialog(DOB);
                break;

        }
    }

    private boolean validateFields() {
        Log.d(TAG, "validateFields: ");
        boolean flag = true;
        if (!MyValidator.isValidField(etProfileFirstName, getResources().getString(R.string.err_enter_firstname))) {
            flag = false;
        } else if (!MyValidator.isValidField(etProfileLastName, getResources().getString(R.string.err_enter_lastname))) {
            flag = false;
        } else if (!MyValidator.isValidEmail(etProfileEmail)) {
            flag = false;
        } else if (!MyValidator.isValidField(spProfileCompanyName, getString(R.string.enter_company))) {
            flag = false;
        }
        if (!L.checkNull(autoTextProfileDesignation.getText().toString())) {
            boolean isDesigExist = false;
            for (Designation designation : designationList) {
                if (designation.getDesigName().equals(autoTextProfileDesignation.getText().toString())) {
                    isDesigExist = true;
                }
            }
            if (!isDesigExist) {
                selected_desigID = "";
                autoTextProfileDesignation.setText("");
                MyValidator.setError(autoTextProfileDesignation, getString(R.string.err_enter_valid_designation));
                flag = false;
            }

        } else if (L.checkNull(autoTextProfileDesignation.getText().toString())) {
            selected_desigID = "";
            autoTextProfileDesignation.setError(null);
        }
        if (!L.checkNull(autoTextProfileLocation.getText().toString())) {
            boolean isLocExist = false;
            for (Location location : locationList) {
                if (location.getLocationName().equals(autoTextProfileLocation.getText().toString())) {
                    isLocExist = true;
                }
            }
            if (!isLocExist) {
                selected_locationID = "";
                autoTextProfileLocation.setText("");
                MyValidator.setError(autoTextProfileLocation, getString(R.string.err_enter_valid_location));
                flag = false;
            }
        } else if (L.checkNull(autoTextProfileLocation.getText().toString())) {
            selected_locationID = "";
            autoTextProfileLocation.setError(null);
        }

        return flag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

   /* @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: ");
        int id = item.getItemId();
        if (id == R.id.menu_request_profile) {
            mainActivity.replaceFrgament(new MyRequestProfileFragment());
            return true;
        } else if (id == R.id.menuMyQueries) {
            mainActivity.replaceFrgament(new MyQueriesFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void showDatePickerDialog(final String Title) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String date = null;
        switch (Title) {
            case DOB:
                if (!L.checkNull(L.getText(tvProfileDOB))) {
                    Date d = DateManagement.getStrToDate(L.getText(tvProfileDOB), DateManagement.DD_MMMM_YYYY);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DAY_OF_MONTH);
                }
                break;

        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        setDate(date, Title);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        Calendar cal = Calendar.getInstance();
        if (Title.equalsIgnoreCase(DOB)) {
            cal.add(Calendar.YEAR, -18);
            Date dateLong = cal.getTime();
            datePickerDialog.getDatePicker().setMaxDate(dateLong.getTime());
        } else if (Title.equalsIgnoreCase(DOJ)) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            try {
                Date d = DateManagement.getStrToDate(L.getText(tvProfileDOB), DateManagement.DD_MMMM_YYYY);
                datePickerDialog.getDatePicker().setMinDate(d.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDate(String date, String title) {
        switch (title) {
            case DOB:
                tvProfileDOB.setText(DateManagement.getFormatedDate(date, DateManagement.DD_MM_YYYY, DateManagement.DD_MMMM_YYYY));
                break;

        }
    }

    @Override
    public void request(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "RESAPONSE: " + response.toString());
                try {
                    updateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "Volley ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.UPDATE_PROFILE_ACTION);
                params.put("organization", user.getUserOrg());
                params.put("organization_code", L.checkNull(user.getUserOrgCode(), ""));
                params.put("firstname", user.getUserFirstName());
                params.put("lastname", user.getUserLastName());
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("emailid", user.getUserEmailID());
                params.put("designation", user.getUserDesignationID());
                params.put("qualification", user.getUserQualificationID());
                params.put("DOB", user.getUserDOB());
                params.put("DOJ", user.getUserDOJ());
                params.put("location", user.getUserLocationID());
                L.l(TAG, "PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                // L.t("Profile updated successfully..!!");
                new DBHelper().updateUser(user);
                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(getString(R.string.profile_userprofile_update_msg))
                        .setCustomImage(R.drawable.success_circle)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }).show();

            } else {
                //  L.t(jsonObject.getString("message"));
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
                            //iv_Profile.setImageBitmap(bitmapImg);
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

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }
    //Example usage of above functions
    /*String myBase64Image = encodeToBase64(myBitmap, Bitmap.CompressFormat.JPEG, 100);
    Bitmap myBitmapAgain = decodeBase64(myBase64Image);*/
}
