package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.constant.FileType;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Created By Chaitali Chavan on 16/11/2016.
 */
public class ManualBulletinsFragment extends Fragment {
    private static final String TAG = "ManualBulletinsFragment";
    @BindView(R.id.wvManualBulletinFragment)
    WebView wvManualBulletinFragment;
    @BindView(R.id.progressBarLearnTestPaper)
    ProgressBar progressBarLearnTestPaper;
    private MainActivity mainActivity;
    private WebViewCachePref webViewCachePref;

    public ManualBulletinsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual_bulletins, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        webViewCachePref = WebViewCachePref.newInstance(getActivity());
        User user = new DBHelper().getUser();
        String html_url = Constants.LMS_MANUALS_BULLETIN_URL + user.getUserID();//&tfkuserPWD=mpkbpowerol";
        //String html_url="http://192.168.1.22/powerol_lms/blocks/tfksettings/tfk_form_all_courses.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=47&tfkuserID=3";//+userID;
        L.l(TAG, "FINAL MAIN URL: " + html_url);
        wvManualBulletinFragment.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvManualBulletinFragment.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvManualBulletinFragment.getSettings().setAllowFileAccess(true);
        wvManualBulletinFragment.getSettings().setAppCacheEnabled(true);
        wvManualBulletinFragment.getSettings().setJavaScriptEnabled(true);
        wvManualBulletinFragment.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvManualBulletinFragment.getSettings().setBuiltInZoomControls(true);
        wvManualBulletinFragment.getSettings().setSupportZoom(true);
        wvManualBulletinFragment.getSettings().setDisplayZoomControls(false);
        wvManualBulletinFragment.setWebViewClient(new MyWebViewClient());
        wvManualBulletinFragment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvManualBulletinFragment.canGoBack()) {
                    wvManualBulletinFragment.goBack();
                    return true;
                }
                return false;
            }
        });
        wvManualBulletinFragment.setWebChromeClient(new WebChromeClient() {
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
            wvManualBulletinFragment.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (L.isNetworkAvailable(getActivity())) {

            Log.i(TAG, "shouldOverrideUrlLoading:MAIN LAST SEGMENT URL: " + html_url.substring(html_url.lastIndexOf("/") + 1));
            webViewCachePref.putPref(html_url.substring(html_url.lastIndexOf("/") + 1), html_url);
        }
        wvManualBulletinFragment.loadUrl(html_url);
        /* else {

//            wvManualBulletinFragment.setVisibility(View.GONE);
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
        wvManualBulletinFragment.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                L.l(TAG, "File Name: " + URLUtil.guessFileName(url, contentDisposition, mimetype));
                String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                String extension = CommonFunctions.getExtension(filename);
                int fileType = CommonFunctions.checkFileType(extension);
                if (fileType == FileType.IMAGE) {
                    startActivity(new Intent(getActivity(), ProfilePictureActivity.class)
                            .putExtra("image_url", url)
                            .putExtra("title", "Image")
                            .putExtra("notProfilePhoto", true));
                } else if (fileType == FileType.VIDEO) {
                    Intent videoview_intent = new Intent(getActivity(), VideoViewActivity.class);
                    videoview_intent.putExtra(getString(R.string.testpaper_url_parcelable_tag), url);
                    startActivity(videoview_intent);
                } else {
                    if (new CommonFunctions().checkDocumentAllreaddyExist(filename)) {
                        File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), filename);
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
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                        DownloadManager dm = (DownloadManager) getActivity().getSystemService(getContext().DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getActivity(), "Downloading File", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void resetWebview(WebView webView) {
        //Clearing the WebView
        try {
            webView.stopLoading();

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
        } catch (Exception e) {
            L.l(TAG, "WebView Error: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php") || url.equals(Constants.LMS_Common_URL + "?")) {
                resetWebview(view);
            } else {
                if (L.isNetworkAvailable(getActivity())) {
                    Log.i(TAG, "shouldOverrideUrlLoading: LAST SEGMENT URL: " + url.substring(url.lastIndexOf("/") + 1));
                    webViewCachePref.putPref(url.substring(url.lastIndexOf("/") + 1), url);
                    view.loadUrl(url);
                } else {
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    String extension = CommonFunctions.getExtension(filename);
                    int fileType = CommonFunctions.checkFileType(extension);
                    if (fileType == FileType.IMAGE
                            || fileType == FileType.VIDEO
                            || fileType == FileType.HTML
                            || fileType == 0) {
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

                    } else {
                        if (new CommonFunctions().checkDocumentAllreaddyExist(filename)) {
                            File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), filename);
                            new CommonFunctions().openFile(getActivity(), file.getPath());
                        } else {
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText("This file not downloaded")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                }

            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            L.l(TAG, "IN ON RECEIVED ERROR");
            resetWebview(view);
            //super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }
}
