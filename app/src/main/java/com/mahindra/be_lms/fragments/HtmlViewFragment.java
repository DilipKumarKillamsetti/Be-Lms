package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
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
public class HtmlViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String HTML_URL = "html_url";
    private static final String TAG = "HtmlViewFragment";
    @BindView(R.id.wvHtmlViewFragment)
    WebView wvHtmlViewFragment;
    @BindView(R.id.progressBarHtmlViewFragment)
    ProgressBar progressBarHtmlViewFragment;
    private MainActivity mainActivity;
    private String htmlURL;
    private WebViewCachePref webViewCachePref;

    public HtmlViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HtmlViewFragment newInstance(String htmlURL) {
        HtmlViewFragment fragment = new HtmlViewFragment();
        Bundle args = new Bundle();
        args.putString(HTML_URL, htmlURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            htmlURL = getArguments().getString(HTML_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_html_view, container, false);
        ButterKnife.bind(this, view);
        webViewCachePref = WebViewCachePref.newInstance(getActivity());
        init();
        return view;
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
        mainActivity = (MainActivity) activity;
    }

    private void init() {
        displayWebview(htmlURL);
    }

    private void displayWebview(String html_url) {
        wvHtmlViewFragment.loadUrl(html_url);
        wvHtmlViewFragment.setWebViewClient(new WebViewClient());
        wvHtmlViewFragment.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvHtmlViewFragment.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvHtmlViewFragment.getSettings().setAllowFileAccess(true);
        wvHtmlViewFragment.getSettings().setAppCacheEnabled(true);
        wvHtmlViewFragment.getSettings().setJavaScriptEnabled(true);
        wvHtmlViewFragment.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvHtmlViewFragment.getSettings().setBuiltInZoomControls(true);
        wvHtmlViewFragment.getSettings().setSupportZoom(true);
        wvHtmlViewFragment.getSettings().setDisplayZoomControls(false);
        wvHtmlViewFragment.setWebViewClient(new MyWebViewClient());


        wvHtmlViewFragment.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBarHtmlViewFragment.getVisibility() == ProgressBar.GONE) {
                    progressBarHtmlViewFragment.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarHtmlViewFragment.setProgress(progress);
                if (progress == 100) {
                    progressBarHtmlViewFragment.setVisibility(View.GONE);
                }
            }
        });
        progressBarHtmlViewFragment.setVisibility(View.GONE);
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
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }).show();
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
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
