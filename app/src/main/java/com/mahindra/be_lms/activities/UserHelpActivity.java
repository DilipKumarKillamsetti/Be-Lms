package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserHelpActivity extends AppCompatActivity {
    private static final String TAG = "UserHelpActivity";
    @BindView(R.id.wvUserHelp)
    WebView wvUserHelp;
    @BindView(R.id.progressBarTrainingPassport)
    ProgressBar progressBarTrainingPassport;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private String flag;
    private String type;
    private boolean contact_to_admin_fromHome;
    private WebViewCachePref webViewCachePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_help);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        webViewCachePref = WebViewCachePref.newInstance(this);
        type = getIntent().getStringExtra("help_type");

        contact_to_admin_fromHome = getIntent().getBooleanExtra("contact_to_admin_fromHome", false);
        if (type.equals("faq") || contact_to_admin_fromHome) {
            findViewById(R.id.footer_view).setVisibility(View.GONE);
        }
        if (L.isNetworkAvailable(this)) {
            setHeaderTitle(type);
            String html_url = Constants.USER_HELP_LINK + "type=" + type;
            Log.d(TAG, "init: help URL: " + html_url);
            wvUserHelp.loadUrl(html_url);
            wvUserHelp.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            wvUserHelp.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
            wvUserHelp.getSettings().setAllowFileAccess(true);
            wvUserHelp.getSettings().setAppCacheEnabled(true);
            wvUserHelp.getSettings().setJavaScriptEnabled(true);
            wvUserHelp.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wvUserHelp.getSettings().setBuiltInZoomControls(true);
            wvUserHelp.getSettings().setSupportZoom(true);
            wvUserHelp.getSettings().setDisplayZoomControls(false);
            wvUserHelp.setWebViewClient(new MyWebViewClient());
            wvUserHelp.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && wvUserHelp.canGoBack()) {
                        wvUserHelp.goBack();
                        return true;
                    }
                    return false;
                }
            });
            wvUserHelp.setWebChromeClient(new WebChromeClient() {
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
        } else {
            wvUserHelp.setVisibility(View.GONE);
            new PKBDialog(this, PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            finish();
                        }
                    }).show();
            //L.t(getString(R.string.err_network_connection));
        }
    }

    private void setHeaderTitle(String type) {
        String title = "";
        switch (type) {
            case "faq":
                title = "FAQ";
                break;
            case "contact_admin":
                title = "Contact to admin";
                break;
            case "registration":
                title = "Help";
                break;

        }
        tvTitle.setText(title);
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
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
        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                .setContentText("Web page not available. Try again later.")
                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                    @Override
                    public void onClick(PKBDialog customDialog) {
                        finish();
                    }
                }).show();
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * check user come from mainacitivty and userlogin=false
         */
        if (!MyApplication.mySharedPreference.checkUserLogin() && type.equals("faq")
                || !MyApplication.mySharedPreference.checkUserLogin() && contact_to_admin_fromHome) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php") || url.equals(Constants.LMS_Common_URL + "?")) {
                resetWebview(view);
            } else {
                if (L.isNetworkAvailable(UserHelpActivity.this)) {
                    Log.i(TAG, "shouldOverrideUrlLoading: LAST SEGMENT URL: " + url.substring(url.lastIndexOf("/") + 1));
                    webViewCachePref.putPref(url.substring(url.lastIndexOf("/") + 1), url);
                    view.loadUrl(url);
                } else {
                    if (webViewCachePref.getPref(url.substring(url.lastIndexOf("/") + 1)) != null) {
                        view.loadUrl(url);
                    } else {
                        new PKBDialog(UserHelpActivity.this, PKBDialog.WARNING_TYPE)
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
