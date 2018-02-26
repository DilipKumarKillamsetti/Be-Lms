package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.AboutUsActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.NotificationActivity;
import com.mahindra.be_lms.activities.SearchActivity;
import com.mahindra.be_lms.adapter.HomeMenuAdapter;
import com.mahindra.be_lms.db.Course;
import com.mahindra.be_lms.db.MenuRights;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.HomeMenu;
import com.mahindra.be_lms.model.SliderModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MyCallback, NetworkMethod, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.rvHomeFragmentMenuList)
    RecyclerView rvHomeFragmentMenuList;
    @BindView(R.id.btnHomeActivitySearch)
    Button edittextHomeSearch;
    @BindView(R.id.progressBar_home)
    ProgressBar progressBar;
    @BindView(R.id.home_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    SliderLayout mDemoSlider;

    @BindView(R.id.etSearchText)
    EditText etSearchText;
    private List<HomeMenu> homeMenuList;
    private List<String> modelList;
    private User user;
    private MainActivity mainActivity;
    private HomeMenuAdapter adapter;
    private ArrayAdapter<String> modelAdapter;
    private List<SliderModel> sliderModelList;
    private DownloadManager downloadManager;
    private List<Course> coursesList;
    private List<String> courses;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.homeSlider);
        user = new DBHelper().getUser();
        //getImageBitmap(user.getUserPicture(), user.getUserPicture());
        return view;
    }

    private void init() {
        user = new DBHelper().getUser();
        Log.d(TAG, "init: FCM TOKEN " + MyApplication.mySharedPreference.getFcmToken());
        int height = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
        mDemoSlider.setMinimumHeight(height / 3);
        //downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        sliderModelList = new ArrayList<>();
        homeMenuList = createMenuList();
        rvHomeFragmentMenuList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvHomeFragmentMenuList.setHasFixedSize(true);
        adapter = new HomeMenuAdapter(getActivity(), homeMenuList);
        adapter.setMyCallback(this);
        rvHomeFragmentMenuList.setAdapter(adapter);
        swipeRefreshLayout.setPadding(0, 0, 0, 0);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.colorPrimary);
//        try {
//            // L.pd("Loading please wait", getActivity());
//            if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
//                progressBar.setVisibility(View.VISIBLE);
//                request(Constants.LMS_URL);
//            }else{
//                if (swipeRefreshLayout.isRefreshing()){
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // modelList.add(new HomeMenu("Select Module",0,0));
        courses = new ArrayList<>();
        coursesList =  new DBHelper().getCourseList();
        courses.add("Select Course");
        for (int i = 0;i<coursesList.size();i++){
            courses.add(coursesList.get(i).getCourseName());
        }
//        modelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
//        spSearchModel.setAdapter(modelAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (L.isNetworkAvailable(getActivity())) {
                    //  request(Constants.LMS_URL);
                } else {
                    L.t(getString(R.string.err_network_connection));
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }


    private void setupSlider(List<SliderModel> sliderModelList) {
        Collections.sort(sliderModelList, new Comparator<SliderModel>() {
            @Override
            public int compare(SliderModel sliderModel1, SliderModel sliderModel2) {
                return sliderModel1.getSeq() > sliderModel2.getSeq() ? 1 : 0;
            }
        });
        Log.d(TAG, "setupSlider: execute");
        Log.d(TAG, "setupSlider: SLIDER LIST : " + sliderModelList.toString());
        //clear all slider images from slider
        mDemoSlider.removeAllSliders();
        for (SliderModel sliderModel : sliderModelList) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
//            Picasso picasso= Picasso.with(getActivity());
//            picasso.load(Constants.LMS_Common_URL+sliderModel.getImg_url())
//                    .placeholder(R.drawable.ic_launcher_transperent)
//                    .error(R.drawable.error_circle);

            textSliderView
                    .description(sliderModel.getTitle())
                    .image(Constants.LMS_Common_URL + sliderModel.getImg_url())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this)
                    .setPicasso(Picasso.with(getActivity()));

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", sliderModel.getTitle());
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setSliderTransformDuration(3000, null);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    private List<HomeMenu> createMenuList() {
        MenuRights menuRights = new DBHelper().getUserMenuRights();
        L.l(TAG, "From DB MenuRights: " + menuRights.toString());
        //  tvWelcomeUserHomeFragment.setText("Welcome "+user.getUserFirstName());
        List<HomeMenu> homeMenuArrayList = new ArrayList<>();
        modelList = new ArrayList<>();
        modelList.add("Select Section");
        //My Profile
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_powerol_passport), R.drawable.nav_powerol_passport_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_powerol_passport));
        //My Training Passport
//        if (menuRights.getMyTrainingPassport().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_training_passport), R.drawable.nav_training_passport_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_training_passport));
//        }
        //Most Viewed
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_most_viewed), R.drawable.nav_most_view_white, R.drawable.home_red_selector));
        //Notification
        homeMenuArrayList.add(new HomeMenu(getString(R.string.notifications), R.drawable.nav_notification_white, R.drawable.home_red_selector));
        //Manuals & Inforamtion
        //  if (menuRights.getManualsBulletins().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_manual_and_bulletins), R.drawable.manuals_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_manual_and_bulletins));
        //}
        //Learning Quiz Test Paper
        // modelList.add(getString(R.string.nav_most_viewed));
        // if (menuRights.getLearningTestQuizs().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_learning_quiz), R.drawable.nav_learning_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_learning_quiz));
        //  }
        //My Field Record
        // if (menuRights.getMyFieldRecords().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        //    homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_my_field_records), R.drawable.nav_field_records_white, R.drawable.home_red_selector));
        //    modelList.add(getString(R.string.nav_my_field_records));
        //}
        //Survey and Feedback
        // if (menuRights.getSurveyFeedbacks().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_survey_feedback), R.drawable.feedback_survey_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_survey_feedback));
        // }
        //Training Calendar
        //if (menuRights.getTrainingCalenderNomination().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_training_calendar), R.drawable.nav_calendar_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_training_calendar));
        // }
//        Technical Uploads
        if (menuRights.getTechnicalUploads().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
            homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_technical_uploads), R.drawable.nav_technical_upload_white, R.drawable.home_red_selector));
            modelList.add(getString(R.string.nav_technical_uploads));
        }
        //Queries and Response
//        if (menuRights.getQueriesResponse().equalsIgnoreCase(getString(R.string.menu_right_yes))) {
//        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_query_management), R.drawable.query_icon, R.drawable.home_red_selector));
//        modelList.add(getString(R.string.nav_query_management));
//        }

        homeMenuArrayList.add(new HomeMenu(getString(R.string.nav_news_update), R.drawable.nav_learning_white, R.drawable.home_red_selector));
        modelList.add(getString(R.string.nav_news_update));
        return homeMenuArrayList;
    }

    @OnClick({R.id.btnHomeActivityAboutUs, R.id.btnHomeActivitySearch, R.id.footertext})
    public void btnHomeActivityAboutUs(View v) {
        if (v.getId() == R.id.btnHomeActivityAboutUs) {
            startActivity(new Intent(getActivity(), AboutUsActivity.class).putExtra("about", "about_powerolcare"));
        } else if (v.getId() == R.id.btnHomeActivitySearch) {
            new CommonFunctions().hideSoftKeyboard(getActivity());
            if (L.isNetworkAvailable(getActivity())) {
                if (validateFields()) {
                    String searchText = L.getText(etSearchText);
                    // String model = L.getText(spSearchModel);
                    // Log.d(TAG, "Search PARAM: TEXT: " + searchText + " MODEL: " + model);
                    // model = model.equals("Manual & Information") ? "Manual and Bulletins" : model;
                    startActivity(new Intent(getActivity(), SearchActivity.class)
                            .putExtra("searchTxt", searchText));
                }
            } else {

            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
            mainActivity.setDrawerEnabled(true);
            Log.d(TAG, "onResume: Method Call");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            // L.pd("Loading please wait", getActivity());
//            if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
//                progressBar.setVisibility(View.VISIBLE);
//                request(Constants.LMS_URL);
//            } else {
//                if (swipeRefreshLayout.isRefreshing()) {
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void myCallback(int position) {
        if(homeMenuList.size()>=position) {
            HomeMenu homeMenu = homeMenuList.get(position);
            L.l(TAG, "Clicked Menu name: " + homeMenu.getMenuName());
            switch (homeMenu.getMenuName()) {
                case "Most Viewed":
                    mainActivity.replaceFrgament(new MostViewedFragment());
                    break;
                case "Learning, Quiz & Test Papers":
                    if (user.getUserEmailID().equals("")) {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.email_update_check_dialog_msg))
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                        mainActivity.replaceFrgament(new ProfileViewFragment());
                                    }
                                }).show();
                    } else {
                        // BE
                        mainActivity.replaceFrgament(new CourseListFragment());

                        //m_ASTRA
                        //  mainActivity.replaceFrgament(new LearnTestQuizFragment());
                    }
                    break;
                case "My Profile":
                    //mainActivity.replaceFrgament(new ProfileFragment());
                    ProfileViewFragment profileViewFragment = new ProfileViewFragment();
                    //profileViewFragment.setDrawerCallback(this);
                    mainActivity.replaceFrgament(profileViewFragment);
                    break;
                case "My Training Passport":
                    mainActivity.replaceFrgament(new MyTrainingPassportFragment());
                    break;
                case "Manual & Information":
                    //mainActivity.replaceFrgament(new ProductFragment());
                    //mainActivity.replaceFrgament(new ManualBulletinsFragment());
                    mainActivity.replaceFrgament(new ManualInfoFragment());

                    break;
                case "Training Calendar":
                    mainActivity.replaceFrgament(new TrainingCalendarFragment());
                    //mainActivity.replaceFrgament(new CompactCalendarTab());
                    break;
                case "Surveys/Feedback":
                    mainActivity.replaceFrgament(new SurveyFeedbackFragment());
                    break;
                case "Queries & Response System":
                    mainActivity.replaceFrgament(new MyQueriesFragment());
                    break;
                case "Technical Uploads":
                    mainActivity.replaceFrgament(new TechnicalDocumentFragment());
                    break;
                case "My Field Records":
                    mainActivity.replaceFrgament(new MyFieldRecordsFragment());
                    break;
                case "Notifications":
                    startActivity(new Intent(getActivity(), NotificationActivity.class));
                    break;
                case "News & Update":
                    mainActivity.replaceFrgament(new NewsAndUpdateFragment());
                    break;
            }
        }else{
            if (L.isNetworkAvailable(getActivity())) {
                //   request(Constants.LMS_URL);
            }
        }
    }

    @Override
    public void request(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // L.dismiss_pd();
                Log.e("================OTP",response);
                progressBar.setVisibility(View.GONE);
                L.l(TAG, "Profile update response: " + response.toString());
                JSONObject jsonObject = null;
                try {
                    /*user = new Gson().fromJson();
                    L.l(TAG, "updated profile user: "+user.toString());
                    new DBHelper().updateUser(user);*/
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                        JSONArray sliderArray = jsonObject.getJSONObject("data").getJSONArray("sliderData");
                        Log.d(TAG, "onResponse: SLIDER ARRAY: " + sliderArray.toString());
                        User user = null;
                        MenuRights menuRights;
                        try {
                            new DBHelper().deleteUser();
                            user = new Gson().fromJson(String.valueOf(jsonObject.getJSONObject("data")), User.class);
                            L.l(TAG, "updated user: " + user.toString());
                            new DBHelper().saveUser(user);
                            new DBHelper().deleteUserMenuRights();
                            if (sliderArray.length() > 0) {
                                Log.d(TAG, "onResponse: SLIDER JSON ARRAY: " + sliderArray.toString());
                                //clear previous list
                                if (sliderModelList.size() > 0) {
                                    sliderModelList.clear();
                                }
                                for (int i = 0; i < sliderArray.length(); i++) {
                                    Log.d(TAG, "onResponse: SLIDER OBJECT: " + sliderArray.getJSONObject(i));
                                    SliderModel sliderModel = new Gson().fromJson(String.valueOf(sliderArray.getJSONObject(i)), SliderModel.class);
                                    sliderModelList.add(sliderModel);
                                }
                                //update slider sync date
                                Log.d(TAG, "onResponse: LAST SLIDER SYNC DATE: " + MyApplication.mySharedPreference.getSliderSyncDate());
                                MyApplication.mySharedPreference.setSliderSyncDate(DateManagement.getCurrentDate());
                                Log.d(TAG, "onResponse: CURRENT SLIDER SYNC DATE: " + MyApplication.mySharedPreference.getSliderSyncDate());
                                Log.d(TAG, "onResponse: SLider list size: " + sliderModelList.size());
                                setupSlider(sliderModelList);
                            }
                            menuRights = new Gson().fromJson(String.valueOf(jsonObject.getJSONObject("data").getJSONObject("menurights")), MenuRights.class);
                            L.l(TAG, "Response MenuRights: " + menuRights.toString());
                            new DBHelper().saveUserMenuRights(menuRights);
                            homeMenuList=createMenuList();
                            adapter.setHomeMenuList(homeMenuList);
                            adapter.notifyDataSetChanged();
//                            modelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
//                            spSearchModel.setAdapter(modelAdapter);
                            MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());
                            MyApplication.mySharedPreference.setUserLogin(true);
                            if (!TextUtils.isEmpty(user.getUserPicture())) {
                                L.l(getActivity(), "Profile Img Exists");
                                String url = Constants.PROFILE_UPLOAD_URL + user.getUserPicture();
                                L.l(getActivity(), "PROFILE IMAGE URL: " + url);
                                getImageBitmap(url, user.getUserPicture());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        L.l(TAG, "response is empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                L.l(TAG, "Update Profile ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", "update_profile_sync");
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("userdate", MyApplication.mySharedPreference.getSliderSyncDate());
                L.l(TAG, "Update profile PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(getActivity(), "PROFILE PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                setBitmap(response);
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        L.l(getActivity(), "PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        imgRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }

    private void setBitmap(Bitmap response) {
        try {
            if (response != null) {
                L.l(getActivity(), "BITMAP not NULL: " + response.toString());
                boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, user.getUserID() + ".png");
                Log.d(TAG, "setBitmap: " + imagcreated);
                MyApplication.flagProfilePicSet = imagcreated;
                L.l(getActivity(), "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
            } else {
                L.dismiss_pd();
                Log.d(TAG, "setBitmap: Bitmap Null");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "setBitmap: Bitmap Null");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        boolean flag = true;
        if (etSearchText.getText().length() == 0) {
            L.t("Enter keyword for search");
            flag = false;
        }
        Log.d(TAG, "validateFields: Search Valid Flag : " + flag);
        return flag;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        new CommonFunctions().hideSoftKeyboard(getActivity());
        Log.d(TAG, "onSliderClick: Description: " + slider.getDescription());
        Log.d(TAG, "onSliderClick: URL: " + slider.getUrl());
        Log.d(TAG, "onSliderClick: SLIDER OBJECT: " + sliderModelList.get(mDemoSlider.getCurrentPosition()));
        for (int i = 0; i < sliderModelList.size(); i++) {
            Log.d(TAG, "onSliderClick: TITLE: " + sliderModelList.get(i).getTitle());
            Log.d(TAG, "onSliderClick: SLIDER TITLE: " + slider.getDescription());

            if (sliderModelList.get(i).getTitle().equals(slider.getDescription())) {
                Log.d(TAG, "onSliderClick: SLIDER OBJECT : " + sliderModelList.get(i));
                Log.d(TAG, "onSliderClick: WEB LINK : " + sliderModelList.get(i).getWebLink());
                if (!TextUtils.isEmpty(sliderModelList.get(i).getWebLink())) {
                    sliderModelList.get(i).setWebLink((sliderModelList.get(i).getWebLink().startsWith("http://") || sliderModelList.get(i).getWebLink().startsWith("https://")) ? sliderModelList.get(i).getWebLink() : "http://" + sliderModelList.get(i).getWebLink());
                    HtmlViewFragment htmlViewFragment = HtmlViewFragment.newInstance(sliderModelList.get(i).getWebLink());
                    mainActivity.replaceFrgament(htmlViewFragment);
                } else if (!TextUtils.isEmpty(sliderModelList.get(i).getPdfdoc())) {
                    Log.d(TAG, "onSliderClick: PDF DOC: " + sliderModelList.get(i).getPdfdoc());
                    String filename = sliderModelList.get(i).getPdfdoc().substring(sliderModelList.get(i).getPdfdoc().lastIndexOf("/") + 1);
                    if (CommonFunctions.checkDocumentAllreaddyExist(filename)) {
                        Log.d(TAG, "onSliderClick: PDF DOC: File Exist");
                        File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), filename);
                        new CommonFunctions().openFile(getActivity(), file.getPath());
                    } else {
                        if (sliderModelList.get(i).getDownloadRef() != 0L) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            String filePath = null;
                            int status = 0;
                            query.setFilterById(sliderModelList.get(i).getDownloadRef());
                            Cursor c = downloadManager.query(query);
                            if (c.moveToFirst()) {
                                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                    L.l(TAG, "Receiver filename: " + filePath);
                                }
                            }
                            c.close();
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                new CommonFunctions().openFile(getActivity(), filePath);
                            } else {
                                new CommonFunctions().CheckStatus(downloadManager, sliderModelList.get(i).getDownloadRef());
                            }
                        } else {
                            final String download_url = Constants.LMS_Common_URL + sliderModelList.get(i).getPdfdoc();
                            final int position = i;
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("To view please download it.")
                                    .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if (L.isNetworkAvailable(getActivity())) {
                                                if (Utility.checkExternalStoragePermission(getActivity())) {

                                                    L.l(TAG, "Document url " + download_url);
                                                    Uri uri = Uri.parse(download_url);
                                                    String filename = uri.getLastPathSegment();
                                                    L.l(TAG, "file to download " + filename);
                                                    //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
                                                    long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, filename);
                                                    L.l(TAG, "Reference ID: " + referencedID);
                                                    sliderModelList.get(position).setDownloadRef(referencedID);

                                                } else {

                                                    Utility.callExternalStoragePermission(getActivity());

                                                }
                                            } else {
                                                //L.t(getString(R.string.err_network_connection));
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

                                        }
                                    }).setNegativeButton(getString(R.string.dialog_cancel), null)
                                    .show();
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
