package com.mahindra.be_lms.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
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
import com.mahindra.be_lms.util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mattgaunt on 10/16/14.
 */
public class MainFragment extends Fragment {

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
    private static final String TAG = MainFragment.class.getSimpleName();
    private final static int FILECHOOSER_RESULTCODE = 1;
    ProgressBar progressBarMyFieldsRecords;
    // private MainActivity mainActivity;
    private boolean sameUrlFlag = false;
    private MainActivity mainActivity;
    private WebView mWebView;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private ValueCallback<Uri> mUploadMessage;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_field_records, container, false);

        // Get reference of WebView from layout/activity_main.xml
        mWebView = (WebView) rootView.findViewById(R.id.wvMyFieldRecords);
        progressBarMyFieldsRecords = (ProgressBar) rootView.findViewById(R.id.progressBarMyFieldsRecords);
        setUpWebViewDefaults(mWebView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL and history stack
            mWebView.restoreState(savedInstanceState);
        }
        if (L.isNetworkAvailable(getActivity())) {

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebChromeClient(new MyWebViewCromClient());
       /* mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if(mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

               // Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
                Intent takePictureIntent = new Intent();
                takePictureIntent.setType("image*//*");
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

                Intent contentSelectionIntent = new Intent(Intent.ACTION_PICK);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image*//*");

                Intent[] intentArray;
                if(takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });*/

            // Load the local index.html file
            String html_url = Constants.LMS_MY_FIELD_RECORD_FORM + MyApplication.mySharedPreference.getUserId() + "&flag=2";//+userID;//&tfkuserPWD=mpkbpowerol";
            if (mWebView.getUrl() == null) {
                mWebView.loadUrl(html_url);
            }
        }
        return rootView;
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
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != getActivity().RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    /* @Override
     public void onActivityResult (int requestCode, int resultCode, Intent data) {
         if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
             super.onActivityResult(requestCode, resultCode, data);
             return;
         }

         Uri[] results = null;

         // Check that the response is a good one
         if(resultCode == Activity.RESULT_OK) {
             if(data == null) {
                 // If there is not data, then we may have taken a photo
                 if(mCameraPhotoPath != null) {
                     results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                 }
             } else {
                 String dataString = data.getDataString();
                 if (dataString != null) {
                     results = new Uri[]{Uri.parse(dataString)};
                 }
             }
         }

         mFilePathCallback.onReceiveValue(results);
         mFilePathCallback = null;
         return;
     }*/
    private class MyWebViewCromClient extends WebChromeClient {
        //The undocumented magic method override
        //Eclipse will swear at you if you try to put @Override here
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            getActivity().startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            getActivity().startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            getActivity().startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.l(TAG, "NEW URL: " + url);
            L.l(TAG, "FLAG: " + sameUrlFlag);
            if (url.equals(Constants.LMS_Common_URL + "login/index.php")) {
                // resetWebview(view);
            } else {
                if (sameUrlFlag && url.equals(Constants.LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?formid=222&masterMenu=211&flag=2")) {
                    mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mainActivity.replaceFrgament(new HomeFragment());
                } else {
                    view.loadUrl(url);
                    sameUrlFlag = false;
                }
            }
            if (url.equals(Constants.LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?formid=222&masterMenu=211&flag=2")) {
                sameUrlFlag = true;
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
         }*/

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            L.l(TAG, "IN ON RECEIVED ERROR:  " + request.getUrl());
            // resetWebview(view);
            //super.onReceivedError(view, request, error);
        }
    }
}

