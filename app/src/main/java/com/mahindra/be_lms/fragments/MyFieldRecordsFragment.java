package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFieldRecordsFragment extends Fragment {
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
    private static final String TAG = MyFieldRecordsFragment.class.getSimpleName();
    @BindView(R.id.wvMyFieldRecords)
    WebView wvMyFieldRecords;
    @BindView(R.id.progressBarMyFieldsRecords)
    ProgressBar progressBarMyFieldsRecords;
    private MainActivity mainActivity;
    private boolean sameUrlFlag = false;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private WebViewCachePref webViewCachePref;

    public MyFieldRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_field_records, container, false);
        ButterKnife.bind(this, view);
        webViewCachePref = WebViewCachePref.newInstance(getActivity());
        mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        String html_url = Constants.LMS_MY_FIELD_RECORD_FORM + MyApplication.mySharedPreference.getUserId() + "&flag=2";//+userID;//&tfkuserPWD=mpkbpowerol";
        L.l(TAG, "FINAL MAIN URL: " + html_url);
        // wvMyFieldRecords.loadUrl(html_url);
        wvMyFieldRecords.getSettings();

        // Enable Javascript
        wvMyFieldRecords.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvMyFieldRecords.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvMyFieldRecords.getSettings().setAllowFileAccess(true);
        wvMyFieldRecords.getSettings().setAppCacheEnabled(true);
        wvMyFieldRecords.getSettings().setJavaScriptEnabled(true);
        wvMyFieldRecords.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvMyFieldRecords.getSettings().setBuiltInZoomControls(true);
        wvMyFieldRecords.getSettings().setSupportZoom(true);
        wvMyFieldRecords.getSettings().setDisplayZoomControls(false);

        // Use WideViewport and Zoom out if there is no viewport defined
        wvMyFieldRecords.getSettings().setUseWideViewPort(true);
        wvMyFieldRecords.getSettings().setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        // wvMyFieldRecords.getSettings().setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            wvMyFieldRecords.getSettings().setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wvMyFieldRecords.setWebViewClient(new MyWebViewClient());
        wvMyFieldRecords.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBarMyFieldsRecords.getVisibility() == ProgressBar.GONE) {
                    progressBarMyFieldsRecords.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarMyFieldsRecords.setProgress(progress);
                if (progress == 100) {
                    progressBarMyFieldsRecords.setVisibility(View.GONE);
                }
            }

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

            }

            //For Android <4.4
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent();
                takePictureIntent.setType("image/*");
                takePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooserIntent.setType("image/*");
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

        });


        if (wvMyFieldRecords.getUrl() == null) {
            if (!L.isNetworkAvailable(getActivity())) {
                wvMyFieldRecords.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            wvMyFieldRecords.loadUrl(html_url);
        }
        wvMyFieldRecords.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvMyFieldRecords.canGoBack()) {
                    wvMyFieldRecords.goBack();
                    return true;
                }
                return false;
            }
        });
        progressBarMyFieldsRecords.setVisibility(View.GONE);

        return view;
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
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return imageFile;
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            L.l(TAG, "FLAG: " + sameUrlFlag);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php")) {
                resetWebview(view);
            } else {
                if (sameUrlFlag && url.equals(Constants.LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?formid=222&masterMenu=211&flag=2")) {
                    mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mainActivity.replaceFrgament(new HomeFragment());
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
                    sameUrlFlag = false;
                }
            }
            if (url.equals(Constants.LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?formid=222&masterMenu=211&flag=2")) {
                sameUrlFlag = true;
            }
            return true;
        }

        @Override
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


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                L.l(TAG, "IN ON RECEIVED ERROR:  " + request.getUrl());
            }
            resetWebview(view);
            //super.onReceivedError(view, request, error);
        }
    }

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=1;

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if(requestCode==FILECHOOSER_RESULTCODE) {
            if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)) {
                Uri[] results = null;

                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (intent == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                return;
            } else {
                if (null == mUploadMessage) return;
                Uri result = null;
                String uriString = "";
                if (intent.toString().contains("file://")) {
                    uriString = intent.getData().getPath();
                } else {
                    uriString = getRealPathFromUri(getActivity(), intent.getData());
                }
                Log.d(TAG, "onActivityResult: STRING PATH: " + uriString);
                result = Uri.fromFile(new File(uriString));
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

 /*   public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);


        }
    }

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    */
}
