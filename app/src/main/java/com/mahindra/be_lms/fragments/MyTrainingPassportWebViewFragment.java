package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTrainingPassportWebViewFragment extends Fragment {

    public static final String TAG = MyTrainingPassportWebViewFragment.class.getSimpleName();
    @BindView(R.id.wvTrainingPassport)
    WebView wvTrainingPassport;
    @BindView(R.id.progressBarTrainingPassport)
    ProgressBar progressBarTrainingPassport;
    private String flag;
    private MainActivity mainActivity;
    private WebViewCachePref webViewCachePref;

    public MyTrainingPassportWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_training_passport_web_view, container, false);
        ButterKnife.bind(this, view);
        mainActivity.setDrawerEnabled(false);
        webViewCachePref = WebViewCachePref.newInstance(getActivity());
        /*Bundle bundle= getArguments();
        if (bundle!=null){
            flag = bundle.getString("flag");
            L.l(TAG, "Intent Flag: "+flag);
        }*/
        String html_url = Constants.LMS_TRAINING_PASSPORT_URL + MyApplication.mySharedPreference.getUserId() + "&flag=1";//&tfkuserPWD=mpkbpowerol";
        L.l(TAG, "FINAL MAIN URL: " + html_url);
        wvTrainingPassport.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvTrainingPassport.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvTrainingPassport.getSettings().setAllowFileAccess(true);
        wvTrainingPassport.getSettings().setAppCacheEnabled(true);
        wvTrainingPassport.getSettings().setJavaScriptEnabled(true);
        wvTrainingPassport.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvTrainingPassport.getSettings().setBuiltInZoomControls(true);
        wvTrainingPassport.getSettings().setSupportZoom(true);
        wvTrainingPassport.getSettings().setDisplayZoomControls(false);
        wvTrainingPassport.setWebViewClient(new MyWebViewClient());

        if (!L.isNetworkAvailable(getActivity())) {
            wvTrainingPassport.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        wvTrainingPassport.loadUrl(html_url);
        wvTrainingPassport.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvTrainingPassport.canGoBack()) {
                    wvTrainingPassport.goBack();
                    return true;
                }
                return false;
            }
        });
        wvTrainingPassport.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBarTrainingPassport.getVisibility() == ProgressBar.GONE) {
                    progressBarTrainingPassport.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarTrainingPassport.setProgress(progress);
                if (progress == 100) {
                    progressBarTrainingPassport.setVisibility(View.GONE);
                }
            }
        });
        progressBarTrainingPassport.setVisibility(View.GONE);


        return view;
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().setTitle("");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        getActivity().getSupportFragmentManager().popBackStack();
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

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            resetWebview(view);
            //super.onReceivedError(view, request, error);
        }
    }
}
