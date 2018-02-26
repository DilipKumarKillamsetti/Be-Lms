package com.mahindra.be_lms.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Company;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.db.Location;
import com.mahindra.be_lms.db.Profile;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements NetworkMethod {
    private static final String DOB = "dob";
    private static final String DOJ = "doj";
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    private static final String TAG = "ProfileActivity";
    @BindView(R.id.etProfileFirstName)
    EditText etProfileFirstName;
    @BindView(R.id.etProfileLastName)
    EditText etProfileLastName;
    @BindView(R.id.etProfileEmail)
    EditText etProfileEmail;
    @BindView(R.id.spProfileCompanyName)
    EditText spProfileCompanyName;
    @BindView(R.id.tvProfileDOB)
    TextView tvProfileDOB;
    @BindView(R.id.autoTextProfileDesignation)
    AutoCompleteTextView autoTextProfileDesignation;
    @BindView(R.id.autoTextProfileLocation)
    AutoCompleteTextView autoTextProfileLocation;
    @BindView(R.id.companySpinner)
    Spinner companySpinner;
    @BindView(R.id.progressBarCompany)
    ProgressBar progressBarCompany;
    @BindView(R.id.designationLay)
    LinearLayout designationLay;
    @BindView(R.id.designationSpinner)
    Spinner designationSpinner;
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
    HashMap<Integer, String> spinnerMap;
    String companyName, departmentName = "";
    Timestamp ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_content_layout);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);

        L.l(TAG, "PROFIEL PIC FLAG: " + MyApplication.flagProfilePicSet);
        init();
    }

    private void init() {
        Log.d(TAG, "init: ");
        // mytoolbar.inflateMenu(R.menu.user_menu);
        //  mytoolbar.setOnMenuItemClickListener(this);

        if (L.isNetworkAvailable(this)) {
            L.pd(getString(R.string.dialog_please_wait), this);
            getCompanyList(Constants.BE_LMS_COMPANY_LIST_URL);
        } else {
            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }

        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                companyName = companySpinner.getSelectedItem().toString();
                String id1 = spinnerMap.get(companySpinner.getSelectedItemPosition());
                progressBarCompany.setVisibility(View.VISIBLE);
                designationLay.setVisibility(View.GONE);
                if (L.isNetworkAvailable(getApplicationContext())) {

                    // L.pd(getString(R.string.dialog_please_wait), ProfileActivity.this);
                    getDepartmentList(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_organisations&frameworkid=" + id1 + "&moodlewsrestformat=json");
                } else {
                    new PKBDialog(getApplicationContext(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                progressBarCompany.setVisibility(View.GONE);
            }
        });

        designationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departmentName = designationSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                progressBarCompany.setVisibility(View.GONE);
            }
        });

        spProfileCompanyName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.equals(".")) {
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }, new InputFilter.LengthFilter(50)
        });
        /*int maxLength = 50;
        spProfileCompanyName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});*/
        getData();
        PackageManager m = ProfileActivity.this.getPackageManager();
        String s = ProfileActivity.this.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            L.l(TAG, "app path: " + s);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    private void getCompanyList(String beLmsCompanyListUrl) {

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, beLmsCompanyListUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              //  L.dismiss_pd();
                L.l(ProfileActivity.this, "SYNC response: " + response.toString());
                updateSppiner(response.toString());
                if (L.isNetworkAvailable(ProfileActivity.this)) {

                    requestPosition(Constants.BE_LMS_POSITION_URL);
                } else {
                    new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(ProfileActivity.this, "Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    if (error.getMessage().equalsIgnoreCase("null")) {
                        L.t("Something went wrong try again or check network connection");
                    }
                } else {
                    L.t("Something went wrong try again or check network connection");
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }



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
        autoTextProfileLocation.setText(user.getUserLocationID());
//        for (int i = 0; i < locationList.size(); i++) {
//            if (locationList.get(i).getLocationID().equalsIgnoreCase(user.getUserLocationID())) {
//                L.l("Location Name : " + locationList.get(i).getLocationName());
//                selected_locationID = locationList.get(i).getLocationID();
//                autoTextProfileLocation.setText(locationList.get(i).getLocationName());
//                //spinnerPosition = i;
//            }
//        }

        // L.l("loaction position : " + spinnerPosition);
        //spProfileLocation.setSelection(spinnerPosition);
        spinnerPosition = 0;

      /*  L.l("Qualification position : " + spinnerPosition);
        spProfileQualification.setSelection(spinnerPosition);*/

        L.l(TAG, "Designation Size: " + designationList.size());
        spinnerPosition = 0;
        for (int i = 0; i < designationList.size(); i++) {
            if (designationList.get(i).getDesigName().equalsIgnoreCase(user.getUserDesignationID())) {
                L.l("Designation Name : " + designationList.get(i).getDesigName());
                selected_desigID = designationList.get(i).getDesigName();
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
                L.l(ProfileActivity.this, "IS FILEEXIST FLAG: " + isFilexists);
                Bitmap bitmap = new ImageHelper().decodeBitmapFromPath(Environment.getExternalStorageDirectory() + "/PKB/userProfile/" + user.getUserID() + ".png");
                L.l(TAG, "Bitmap " + bitmap);
                if (bitmap != null) {
                    L.l(ProfileActivity.this, "Set Bitmap to header");
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

    private void updateSppiner(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);

            String[] spinnerArray = new String[jsonArray.length()];
            spinnerMap = new HashMap<Integer, String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject companyList = (JSONObject) jsonArray.get(i);
                spinnerMap.put(i, companyList.getString("companyid"));
                spinnerArray[i] = companyList.getString("CompanyName");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            companySpinner.setAdapter(adapter);
            companySpinner.setSelection(adapter.getPosition(user.getUserOrg()));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        ProfileActivity.this.finish();
        return true;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        L.l(TAG, "OnResume");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            setData();
        }
       /* try {
            mainActivity.getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
    }

    private void getData() {
        Log.d(TAG, "getData: ");
        L.l(ProfileActivity.this, "[METHOD:getData] fetch data from sqlite companylist,designationlist,locationlist");
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
        ArrayAdapter designatinAdapter = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_1, designationList);
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
                selected_desigID = designation.getDesigName();
            }
        });
        ArrayAdapter locationAdapter = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_1, locationList);
        //locationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //spProfileLocation.setAdapter(locationAdapter);
//        autoTextProfileLocation.setAdapter(locationAdapter);
//        autoTextProfileLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                L.l(TAG, "location autocomplete Click: ");
//                Location location = (Location) parent.getAdapter().getItem(position);
//                L.l(TAG, "selected location: " + location.getLocationID());
//                selected_locationID = location.getLocationID();
//            }
//        });

    }

    @OnClick({R.id.btn_profile_update, R.id.tvProfileDOB})
    public void onClickEvent(View v) {
        Log.d(TAG, "onClickEvent: ");
        switch (v.getId()) {
            case R.id.btn_profile_update:
                if (NetworkUtil.getConnectivityStatus(ProfileActivity.this) != 0) {
                    if (validateFields()) {
                        user.setUserOrg(companyName);
                        user.setUserOrgCode(departmentName);
                        user.setUserFirstName(L.checkNull(L.getText(etProfileFirstName), ""));
                        user.setUserLastName(L.checkNull(L.getText(etProfileLastName), ""));
                        user.setUserEmailID(L.getText(etProfileEmail));
                        user.setUserDesignationID(selected_desigID);
                        if (!L.checkNull(L.getText(tvProfileDOB))) {
                            user.setUserDOB(DateManagement.getFormatedDate(L.getText(tvProfileDOB), DateManagement.DD_MMMM_YYYY, DateManagement.DD_MM_YYYY));
                        } else {
                            user.setUserDOB("");
                        }
                        user.setUserLocationID(autoTextProfileLocation.getText().toString());
                        L.l(TAG, "updated user: " + user.toString());
                        L.pd("Updating profile", "Please wait", ProfileActivity.this);
                        request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_user_update_users&moodlewsrestformat=json");
                    }
                } else {
                    new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
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
//        else if (!MyValidator.isValidField(spProfileCompanyName, getString(R.string.enter_company))) {
//            flag = false;
//        }
        if (!MyValidator.isValidField(etProfileFirstName, getResources().getString(R.string.err_enter_firstname))) {
            flag = false;
        } else if (!MyValidator.isValidField(etProfileLastName, getResources().getString(R.string.err_enter_lastname))) {
            flag = false;
        } else if (!MyValidator.isValidEmail(etProfileEmail)) {
            flag = false;
        } else if (!MyValidator.isValidField(autoTextProfileDesignation)) {
            L.t(getString(R.string.err_enter_valid_designation));
            flag = false;
        } else if (!MyValidator.isValidField(autoTextProfileLocation)) {
            L.t(getString(R.string.err_enter_valid_location));
            flag = false;
        } else if (!MyValidator.isValidField(tvProfileDOB, getString(R.string.err_enter_dob))) {
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
//        if (!L.checkNull(autoTextProfileLocation.getText().toString())) {
//            boolean isLocExist = false;
//            for (Location location : locationList) {
//                if (location.getLocationName().equals(autoTextProfileLocation.getText().toString())) {
//                    isLocExist = true;
//                }
//            }
//            if (!isLocExist) {
//                selected_locationID = "";
//                autoTextProfileLocation.setText("");
//                MyValidator.setError(autoTextProfileLocation, getString(R.string.err_enter_valid_location));
//                flag = false;
//            }
//        } else if (L.checkNull(autoTextProfileLocation.getText().toString())) {
//            selected_locationID = "";
//            autoTextProfileLocation.setError(null);
//        }
//        if (!L.checkNull(autoTextProfileQualification.getText().toString())) {
//            boolean isQualExist = false;
//            for (Qualification qualification : qualificationList) {
//                if (qualification.getQualificationName().equals(autoTextProfileQualification.getText().toString())) {
//                    isQualExist = true;
//                }
//            }
//            if (!isQualExist) {
//                selected_qualificationID = "";
//                autoTextProfileQualification.setText("");
//                MyValidator.setError(autoTextProfileQualification, getString(R.string.err_enter_valid_qualification));
//                flag = false;
//            }
//        } else if (L.checkNull(autoTextProfileQualification.getText().toString())) {
//            selected_qualificationID = "";
//            autoTextProfileQualification.setError(null);
//        }
        return flag;
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
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
                    new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                DateFormat df = new SimpleDateFormat("dd-MM-yy");
                try {
                    ts = new Timestamp(((Date) df.parse(user.getUserDOB())).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                params.put("users[0][id]", new MySharedPreference(ProfileActivity.this).getUserId());
                params.put("users[0][email]", user.getUserEmailID());
                params.put("users[0][departmentname]", user.getUserOrgCode());
                params.put("users[0][positionname]", user.getUserDesignationID());
                params.put("users[0][customfields][2][type]", "location");
                params.put("users[0][customfields][2][value]", user.getUserLocationID());
                params.put("users[0][customfields][3][type]", "DOB");
                params.put("users[0][customfields][3][value]", "" + ts);
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
            if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                // L.t("Profile updated successfully..!!");
                new DBHelper().updateUser(user);
                new PKBDialog(ProfileActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(getString(R.string.profile_userprofile_update_msg))
                        .setCustomImage(R.drawable.success_circle)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                ProfileActivity.this.finish();
                            }
                        }).show();

            } else {
                //  L.t(jsonObject.getString("message"));
                new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result=Utility.checkPermission(ProfileActivity.this);
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
                bm = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), data.getData());
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
            if (L.isNetworkAvailable(ProfileActivity.this)) {
                if (!TextUtils.isEmpty(strProfile)) {
                    updateProfileImage();
                } else {
                    //L.t("Error while capturing image. Please try again");
                    new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
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
                new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
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
        L.pd("Updating profile image", "Please wait", ProfileActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "PROFILE IMG UPDATE RESPONSE: " + response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                            new PKBDialog(ProfileActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setContentText(getString(R.string.profile_image_updated_msg))
                                    .setCustomImage(R.drawable.success_circle)
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                            user.setUserPicture(user.getUserID() + ".png");
                            new DBHelper().updateUser(user);
                            boolean fileSave = new ImageHelper().createDirectoryAndSaveFile(bitmapImg, user.getUserID() + ".png");
                            MyApplication.flagProfilePicSet = fileSave;
                            L.l(TAG, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                        } else {
                            new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
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
                    new PKBDialog(ProfileActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.UPDATE_PROFILE_IMAGE_ACTION);
                params.put("userid", new MySharedPreference(ProfileActivity.this).getUserId());
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
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        ProfileActivity.this.finish();
    }

    private void getDepartmentList(String beLmsCompanyListUrl) {

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, beLmsCompanyListUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //L.dismiss_pd();
                designationLay.setVisibility(View.VISIBLE);
                progressBarCompany.setVisibility(View.GONE);
                L.l(ProfileActivity.this, "SYNC response: " + response.toString());
                updateDesignationSppiner(response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //L.dismiss_pd();
                designationLay.setVisibility(View.GONE);
                progressBarCompany.setVisibility(View.GONE);
                L.l(ProfileActivity.this, "Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    if (error.getMessage().equalsIgnoreCase("null")) {
                        L.t("Something went wrong try again or check network connection");
                    }
                } else {
                    L.t("Something went wrong try again or check network connection");
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void updateDesignationSppiner(String s) {

        try {
            JSONArray jsonArray = new JSONArray(s);
            String[] spinnerArray1 = new String[jsonArray.length()];
            HashMap<Integer, String> desig = new HashMap<Integer, String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject desigList = (JSONObject) jsonArray.get(i);
                desig.put(i, desigList.getString("departmentid"));
                spinnerArray1[i] = desigList.getString("departmentname");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_item, spinnerArray1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            designationSpinner.setAdapter(adapter);
            designationSpinner.setSelection(adapter.getPosition(user.getUserOrgCode()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestPosition(String url) {
       // L.pd("Please Wait", this);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                L.dismiss_pd();
                try {
                    updateDisplayPosition(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(ProfileActivity.this, "Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    if (error.getMessage().equalsIgnoreCase("null")) {
                        L.t("Something went wrong try again or check network connection");
                    }
                } else {
                    L.t("Something went wrong try again or check network connection");
                }
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void updateDisplayPosition(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            JSONArray designationArray = jsonObject.getJSONArray("positions");
            List<Designation> designations = new ArrayList<>();
            List<Designation> db_DesignationList = new ArrayList<>();
            DBHelper dbHelper = new DBHelper();
            db_DesignationList = dbHelper.getDesignationList();
            MyApplication.mySharedPreference.putPref(MySharedPreference.MASTER_SYNC_DATE, DateManagement.getCurrentDate());
            if (db_DesignationList.size() > 0) {
                //L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                boolean isPresent = false;
                for (int i = 0; i < designations.size(); i++) {
                    isPresent = false;
                    //L.l(TAG, "In Update/Insert Designation:");
                    for (Designation designation : db_DesignationList) {
                        if (designations.get(i).getDesigID().equals(designation.getDesigID())) {
                            isPresent = true;
                            //L.l(TAG, "In Update Designation STATE:" + isPresent);
                        }
                    }
                    //  L.l(TAG, "Out of for each Designation State:" + isPresent);
                    if (isPresent) {
                        dbHelper.getAppDaoSession().getDesignationDao().updateInTx(designations.get(i));
                        // L.l(TAG, "In Update Designation:");
                    } else {
                        dbHelper.getAppDaoSession().getDesignationDao().insertInTx(designations.get(i));
                        // L.l(TAG, "In Insert Designation:");
                    }
                }
            } else {
                //L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                dbHelper.getAppDaoSession().getDesignationDao().insertInTx(designations);
            }

            for (int i = 0; i < designationList.size(); i++) {
                if (designationList.get(i).getDesigName().equalsIgnoreCase(user.getUserDesignationID())) {
                    L.l("Designation Name : " + designationList.get(i).getDesigName());
                    selected_desigID = designationList.get(i).getDesigName();
                    autoTextProfileDesignation.setText(designationList.get(i).getDesigName());
                }
            }
//            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
//                new MasterSyncTask().execute(response);
//            } else {
//                L.t("" + jsonObject.getString("message"));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
            L.t_long(getString(R.string.err_server_site));
        }
    }
}
