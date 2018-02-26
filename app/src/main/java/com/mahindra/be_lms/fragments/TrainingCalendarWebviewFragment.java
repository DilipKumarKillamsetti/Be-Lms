package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Chaitali Chavan on 12/26/2016.
 */
public class TrainingCalendarWebviewFragment extends Fragment {
    private static final String TAG = "TrainingCalendarWebview";
    @BindView(R.id.wvTrainingCalendar)
    WebView webViewTrainingCalendar;
    @BindView(R.id.pbTrainingCalendar)
    ProgressBar progressBarTrainingCalendar;
    private MainActivity mainActivity;

    public TrainingCalendarWebviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_calendar_webview, container, false);
        ButterKnife.bind(this, view);
        mainActivity.setDrawerEnabled(false);
        try {
            String html_url = Constants.LMS_TRAINING_CALENDAR_LINK + MyApplication.mySharedPreference.getUserId();//+userID;//&tfkuserPWD=mpkbpowerol";
            L.l(TAG, "FINAL MAIN URL: " + html_url);
            if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
                webViewTrainingCalendar.loadUrl(html_url);
                webViewTrainingCalendar.getSettings().setJavaScriptEnabled(true);
                webViewTrainingCalendar.setWebViewClient(new MyWebViewClient());
                webViewTrainingCalendar.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK
                                && event.getAction() == MotionEvent.ACTION_UP
                                && webViewTrainingCalendar.canGoBack()) {
                            webViewTrainingCalendar.goBack();
                            return true;
                        }
                        return false;
                    }
                });
                webViewTrainingCalendar.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 100 && progressBarTrainingCalendar.getVisibility() == ProgressBar.GONE) {
                            progressBarTrainingCalendar.setVisibility(ProgressBar.VISIBLE);
                        }
                        progressBarTrainingCalendar.setProgress(progress);
                        if (progress == 100) {
                            progressBarTrainingCalendar.setVisibility(View.GONE);
                        }
                    }
                });
                progressBarTrainingCalendar.setVisibility(View.GONE);
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
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

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php")) {
                resetWebview(view);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                L.l(TAG, "IN ON RECEIVED ERROR:  " + request.getUrl());
            }
            resetWebview(view);
        }
    }
}
