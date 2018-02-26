package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
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
import com.mahindra.be_lms.activities.DashboardActivity;

import com.mahindra.be_lms.adapter.ApprovedProfileAdapter;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.model.ApprovedProfile;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnTestQuizFragment extends Fragment implements MyCallback, NetworkMethod {
    public static final String TAG = LearnTestQuizFragment.class.getSimpleName();
    @BindView(R.id.rvLearnTestQuizFragment)
    RecyclerView rvLearnTestQuizFragment;
    @BindView(R.id.tvLearnTestQuizFragmentNoRecordFound)
    TextView tvLearnTestQuizFragmentNoRecordFound;
    @BindView(R.id.wvTestPaperFragment)
    WebView wvTestPaperFragment;
    @BindView(R.id.progressBarLearnTestPaper)
    ProgressBar progressBarLearnTestPaper;
    String courseId;
    private DashboardActivity mainActivity;
    // private List<Course> courseList;
    private List<ApprovedProfile> profileList;
    private WebViewCachePref webViewCachePref;
    private QuizDataModel quizDataModel;
    //http://192.168.1.22/powerol_lms/blocks/tfksettings/tfk_form_all_courses.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=47

    public LearnTestQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn_test_quiz, container, false);
        ButterKnife.bind(this, view);
        mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init();
        return view;
    }

    private void init() {
        webViewCachePref = WebViewCachePref.newInstance(getActivity());
        //mainActivity.setDrawerEnabled(false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            courseId = bundle.getString("id");
        }
        //courseList = DataProvider.getDummyCourseList();
        User user = new DBHelper().getUser();
        String userID = user.getUserID();
        L.l(TAG, "USERNAME: " + userID);
        //String pswrd = new CommonFunctions().DecryptString(user.getPassword());
        //L.l(TAG, "PASSWORD: "+pswrd);
        rvLearnTestQuizFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvLearnTestQuizFragment.setHasFixedSize(true);
        rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        profileList = new ArrayList<>();
        String html_url = Constants.LMS_QUIZ_URL + userID;//&tfkuserPWD=mpkbpowerol";
        //String html_url="http://192.168.1.22/powerol_lms/blocks/tfksettings/tfk_form_all_courses.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=47&tfkuserID=3";//+userID;
        L.l(TAG, "FINAL MAIN URL: " + html_url);
        wvTestPaperFragment.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvTestPaperFragment.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvTestPaperFragment.getSettings().setAllowFileAccess(true);
        wvTestPaperFragment.getSettings().setAppCacheEnabled(true);
        wvTestPaperFragment.getSettings().setJavaScriptEnabled(true);
        wvTestPaperFragment.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvTestPaperFragment.getSettings().setBuiltInZoomControls(true);
        wvTestPaperFragment.getSettings().setSupportZoom(true);
        wvTestPaperFragment.getSettings().setDisplayZoomControls(false);
        wvTestPaperFragment.setWebViewClient(new MyWebViewClient());
        wvTestPaperFragment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvTestPaperFragment.canGoBack()) {
                    wvTestPaperFragment.goBack();
                    return true;
                }
                return false;
            }
        });
        wvTestPaperFragment.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBarLearnTestPaper.getVisibility() == ProgressBar.GONE) {
                    progressBarLearnTestPaper.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarLearnTestPaper.setProgress(progress);
                if (progress == 100) {
                    progressBarLearnTestPaper.setVisibility(View.GONE);
                }
            }
        });
        progressBarLearnTestPaper.setVisibility(View.GONE);
        if (!L.isNetworkAvailable(getActivity())) {
            wvTestPaperFragment.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
      //  wvTestPaperFragment.loadUrl("http://139.59.56.87/nucleus/vinod/signup.html?userid="+MyApplication.mySharedPreference.getUserId()+"&courseid="+courseId+"&token="+MyApplication.mySharedPreference.getUserToken());
        //http://139.59.56.87/nucleus/vinod/bequiz.html?userid=4&courseid=2&token=ee99e097bdc9e276a034ed5b4cc3b1198c262ae6
        wvTestPaperFragment.loadUrl(Constants.BE_LMS_QUIZ_URL+MyApplication.mySharedPreference.getUserId()+"&courseid="+courseId+"&token="+MyApplication.mySharedPreference.getUserToken());
        /* else {

//            wvTestPaperFragment.setVisibility(View.GONE);
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                        @Override
//                        public void onClick(PKBDialog customDialog) {
//                            customDialog.dismiss();
//                            getActivity().getSupportFragmentManager().popBackStack();
//                        }
//                    }).show();
            //L.t(getString(R.string.err_network_connection));
        }*/
        wvTestPaperFragment.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                L.l(TAG, "File Name: " + URLUtil.guessFileName(url, contentDisposition, mimetype));
                if (new CommonFunctions().checkDocumentAllreaddyExist(URLUtil.guessFileName(url, contentDisposition, mimetype))) {
                    File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), URLUtil.guessFileName(url, contentDisposition, mimetype));
                    new CommonFunctions().openFile(getActivity(), file.getPath());
                } else {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                    DownloadManager dm = (DownloadManager) getActivity().getSystemService(getContext().DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getActivity(), "Downloading File", Toast.LENGTH_LONG).show();
                }
            }
        });
        /*if (NetworkUtil.getConnectivityStatus(getActivity())!=0){
            try {
                request(Constants.LMS_URL);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            L.t_long(getString(R.string.err_network_connection));
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    @Override
    public void myCallback(int position) {
        Bundle bundle = new Bundle();
        L.l(TAG, "Profile Parceable: " + profileList.get(position).toString());
        bundle.putParcelable(getString(R.string.profile_parceable_tag), profileList.get(position));
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setArguments(bundle);
        mainActivity.replaceFrgament(courseFragment);
    }

    @Override
    public void request(String url) {
        L.pd("Fetching profile data", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "Approve Profile Response: " + response);
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
                L.l(TAG, "Volley Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.ACTION_FETCH_APPROVED_PROFILE_DATA);
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                L.l(TAG, "APPROVED PROFILE PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
       /* {"result":"success","message":"Success",
       "data":{"courses":[{"courseid":"352","coursename":"SKILL TEST L0"}],
       "profile_name":"Dealer Technician - Telecom Service","profile_id":"24"}}*/
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
               /* JSONObject data_jsonObject = new JSONObject(jsonObject.getString("data"));
                L.l(TAG, "Data Object: "+data_jsonObject.toString());*/
                JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                L.l(TAG, "Data Object: " + dataJsonArray.toString());
                if (dataJsonArray.length() > 0) {
                    profileList = new ArrayList<>();
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        ApprovedProfile approvedProfile = new ApprovedProfile();
                        approvedProfile.setProfileID(dataJsonArray.getJSONObject(i).getString("profile_id"));
                        approvedProfile.setProfileName(dataJsonArray.getJSONObject(i).getString("profile_name"));
                        approvedProfile.setProfile_json(dataJsonArray.getJSONObject(i).getJSONArray("courses").toString());
                        L.l(TAG, "Approved Profile Object: " + approvedProfile.toString());
                        profileList.add(approvedProfile);
                    }
                    L.l(TAG, " final fetch profile list: " + profileList.toString());
                    ApprovedProfileAdapter adapter = new ApprovedProfileAdapter(getActivity(), profileList);
                    adapter.setmCallback(this);
                    rvLearnTestQuizFragment.setAdapter(adapter);
                } else {
                    tvLearnTestQuizFragmentNoRecordFound.setVisibility(View.VISIBLE);
                }

               /* List<Profile> profileList1 =new DBHelper().fetchProfileByID(data_jsonObject.getString("profile_id"));
                for (Profile profile: profileList1) {
                    L.l("profileList size : "+profileList1.size()) ;
                    profile.setProfile_json(data_jsonObject.getJSONArray("courses").toString());
                    profile.setProfile_approval(true);
                    new DBHelper().updateProfile(profile);
                }
                profileList = new ArrayList<>();
                profileList = new DBHelper().getApprovedProfileList();
                L.l(TAG,"Final List Size: "+profileList.size()) ;*/

                /*if (profileList.isEmpty()){
                    tvLearnTestQuizFragmentNoRecordFound.setVisibility(View.VISIBLE);
                }else {
                    tvLearnTestQuizFragmentNoRecordFound.setVisibility(View.GONE);
                }*/
            } else {
                // L.t_long(jsonObject.getString("message"));
                tvLearnTestQuizFragmentNoRecordFound.setVisibility(View.VISIBLE);
                tvLearnTestQuizFragmentNoRecordFound.setText(jsonObject.getString("message"));
            }
        }
    }

    private void resetWebview(WebView webView) {
        //Clearing the WebView
        try {
            webView.stopLoading();
        } catch (Exception e) {
            L.l(TAG, "WebView Error: " + e.getMessage());
        }
        try {
            webView.clearView();
        } catch (Exception e) {
            L.l(TAG, "WebView Error: " + e.getMessage());
        }
        if (webView.canGoBack()) {
            webView.goBack();
        }
        webView.loadUrl("about:blank");
        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                .setContentText("Web page not available. Try again later.")
                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                    @Override
                    public void onClick(PKBDialog customDialog) {
                        customDialog.dismiss();
                        mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mainActivity.replaceFrgament(new HomeFragment());
                    }
                }).show();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php") || url.equals(Constants.LMS_Common_URL + "?")) {
                resetWebview(view);
            } else {
                if (L.isNetworkAvailable(getActivity())) {
                    Log.i(TAG, "shouldOverrideUrlLoading: LAST SEGMENT URL: " + url.substring(url.lastIndexOf("/") + 1));
                    webViewCachePref.putPref(url.substring(url.lastIndexOf("/") + 1), url);
                    view.loadUrl(url);
                } else {
                    if (webViewCachePref.getPref(url.substring(url.lastIndexOf("/") + 1)) != null) {
                        view.loadUrl(url);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Offline page is not available")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
            return true;
        }

        /* @Override
         public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
             L.l(TAG, "IN ON RECEIVED HTTP ERROR");
             resetWebview(view);
             //super.onReceivedHttpError(view, request, errorResponse);
         }

         @Override
         public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
             L.l(TAG, "IN ON RECEIVED SSL ERROR");
             resetWebview(view);
             //super.onReceivedSslError(view, handler, error);
         }
 */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            L.l(TAG, "IN ON RECEIVED ERROR");
            resetWebview(view);
            //super.onReceivedError(view, request, error);
        }
    }
}
