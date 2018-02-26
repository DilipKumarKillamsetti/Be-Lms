package com.mahindra.be_lms.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {
    private static final String TAG = "AboutUsActivity";
    // TextView tv_abt_cont;
    @BindView(R.id.sc)
    ScrollView scrollView;
    @BindView(R.id.tv_abt_cont)
    JustifiedTextView tv_abt_cont;
    @BindView(R.id.wvAboutUs)
    WebView wvAboutUs;
    @BindView(R.id.progressBarAboutUs)
    ProgressBar progressBarAboutUs;
    @BindView(R.id.tvAboutUsTitle)
    TextView tvAboutUsTitle;
    private WebViewCachePref webViewCachePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        ButterKnife.bind(this);
        webViewCachePref = WebViewCachePref.newInstance(this);
        //about_powerolcare
        if (getIntent().getStringExtra("about").equals("about_powerolcare") || getIntent().getStringExtra("about").equals("about")) {
            //  tv_abt_cont = (JustifiedTextView) findViewById(R.id.tv_abt_cont);
            //tv_abt_cont.setText(Html.fromHtml(getResources().getString(
            // R.string.about_us)));
            if (getIntent().getStringExtra("about").equals("about_powerolcare")) {
                tvAboutUsTitle.setText("Powerol Care");
            } else {
                tvAboutUsTitle.setText("About Us");
            }
            wvAboutUs.setVisibility(View.VISIBLE);
            wvAboutUs.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            wvAboutUs.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
            wvAboutUs.getSettings().setAllowFileAccess(true);
            wvAboutUs.getSettings().setAppCacheEnabled(true);
            wvAboutUs.getSettings().setJavaScriptEnabled(true);
            wvAboutUs.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wvAboutUs.getSettings().setBuiltInZoomControls(true);
            wvAboutUs.getSettings().setSupportZoom(true);
            wvAboutUs.getSettings().setDisplayZoomControls(false);

            String html_url = Constants.LMS_ABOUT_US;
            //wvAboutUs.loadUrl("file:///android_asset/www/index.html");
            wvAboutUs.loadUrl(html_url);
            wvAboutUs.setWebViewClient(new MyWebViewClient());
            wvAboutUs.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && wvAboutUs.canGoBack()) {
                        wvAboutUs.goBack();
                        return true;
                    }
                    return false;
                }
            });
            wvAboutUs.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    if (progress < 100 && progressBarAboutUs.getVisibility() == ProgressBar.GONE) {
                        progressBarAboutUs.setVisibility(ProgressBar.VISIBLE);
                    }
                    progressBarAboutUs.setProgress(progress);
                    if (progress == 100) {
                        progressBarAboutUs.setVisibility(View.GONE);
                    }
                }
            });
            progressBarAboutUs.setVisibility(View.GONE);

            wvAboutUs.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    L.l(AboutUsActivity.this, "File Name: " + URLUtil.guessFileName(url, contentDisposition, mimetype));
                    if (new CommonFunctions().checkDocumentAllreaddyExist(URLUtil.guessFileName(url, contentDisposition, mimetype))) {
                        File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), URLUtil.guessFileName(url, contentDisposition, mimetype));
                        new CommonFunctions().openFile(AboutUsActivity.this, file.getPath());
                    } else {
                        if (L.isNetworkAvailable(AboutUsActivity.this)) {
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
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            Toast.makeText(AboutUsActivity.this, "Downloading File", Toast.LENGTH_LONG).show();
                        } else {
                            new PKBDialog(AboutUsActivity.this, PKBDialog.WARNING_TYPE)
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
                }
            });
        } else {
            scrollView.setVisibility(View.VISIBLE);
            tvAboutUsTitle.setText("Contact Us");
            Drawable img = getResources().getDrawable(R.drawable.nav_contact);
            img.setBounds(0, 0, 40, 40);
            tvAboutUsTitle.setCompoundDrawables(img, null, null, null);
            tv_abt_cont = (JustifiedTextView) findViewById(R.id.tv_abt_cont);
            tv_abt_cont.setText(Html.fromHtml(getResources().getString(
                    R.string.contact_us)));
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finish();
    }

    private void resetWebview(WebView webView) {
        //Clearing the WebView
        try {
            webView.stopLoading();
        } catch (Exception e) {
            L.l(this, "WebView Error: " + e.getMessage());
        }
        try {
            webView.clearView();
        } catch (Exception e) {
            L.l(this, "WebView Error: " + e.getMessage());
        }
        if (webView.canGoBack()) {
            webView.goBack();
        }
        webView.loadUrl("about:blank");
        new PKBDialog(AboutUsActivity.this, PKBDialog.WARNING_TYPE)
                .setContentText("Web page not available. Try again later.")
                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                    @Override
                    public void onClick(PKBDialog customDialog) {
                        customDialog.dismiss();
                        AboutUsActivity.this.finish();
                    }
                }).show();
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.home_menu, menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId()==R.id.menu_gotohome){
             AboutUsActivity.this.finish();
         }
         return super.onOptionsItemSelected(item);
     }*/
    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        AboutUsActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(Constants.LMS_Common_URL + "login/index.php")) {
                resetWebview(view);
            } else {
                if (L.isNetworkAvailable(AboutUsActivity.this)) {
                    Log.i(TAG, "shouldOverrideUrlLoading: LAST SEGMENT URL: " + url.substring(url.lastIndexOf("/") + 1));
                    webViewCachePref.putPref(url.substring(url.lastIndexOf("/") + 1), url);
                    view.loadUrl(url);
                } else {
                    if (webViewCachePref.getPref(url.substring(url.lastIndexOf("/") + 1)) != null) {
                        view.loadUrl(url);
                    } else {
                        new PKBDialog(AboutUsActivity.this, PKBDialog.WARNING_TYPE)
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
        }
    }
}