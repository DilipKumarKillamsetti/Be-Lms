package com.mahindra.be_lms.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.util.CustomProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 1/19/2018.
 */

public class ViewDocument extends BaseActivity {

    @BindView(R.id.wvDocPreview)
    WebView wvDocPreview;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_doc);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        String docName = getIntent().getStringExtra("doc_name");
        try {
            getSupportActionBar().show();
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellow)));
            actionBar.setTitle(docName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        final ProgressDialog dialog = new ProgressDialog(ViewDocument.this);
//        dialog.setMessage("Loading File, please wait...");
//        dialog.setCancelable(false);
        dialog = new CustomProgressDialog(this,"");
        dialog.show();

        wvDocPreview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                wvDocPreview.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        String docURL = getIntent().getStringExtra("doc_url");
        String fURL = docURL.replace("forcedownload=1&","");
        wvDocPreview.getSettings().setJavaScriptEnabled(true);
        wvDocPreview.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvDocPreview.loadUrl("http://docs.google.com/gview?embedded=true&chrome=false&url=" + fURL);
        wvDocPreview.getSettings().setLoadWithOverviewMode(true);
    }
}
