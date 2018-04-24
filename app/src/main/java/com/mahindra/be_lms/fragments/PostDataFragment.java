package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.DisclaimerActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.Request;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.KeyValue;
import com.mahindra.be_lms.util.AppHelper;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.FilePath;
import com.mahindra.be_lms.util.MultipartRequest;
import com.mahindra.be_lms.util.UploadFile;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.util.VolleyMultipartRequest;
import com.mahindra.be_lms.videocompression.MediaController;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mahindra.be_lms.R.id.etUploadToServerDesc;
import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

/**
 * Created by Dell on 1/31/2018.
 */

public class PostDataFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = PostDataFragment.class.getSimpleName();
    private DashboardActivity dashboardActivity;
    @BindView(R.id.ll_remove)
    LinearLayout ll_remove;
    @BindView(R.id.ll_file)
    LinearLayout ll_file;
    @BindView(R.id.ll_tag)
    LinearLayout ll_tag;
    @BindView(R.id.ll_location)
    LinearLayout ll_location;
    @BindView(R.id.et_location)
    EditText et_location;
    @BindView(R.id.iv_postImage)
    ImageView iv_postImage;
    @BindView(R.id.vv_postVideo)
    VideoView vv_postVideo;
    @BindView(R.id.rl_videoView)
    RelativeLayout rl_videoView;
    @BindView(R.id.btn_post)
    Button btn_post;
    @BindView(R.id.et_post_text)
    EditText et_post_text;
    @BindView(R.id.header_imageView)
    CircularImageView header_imageView;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    private User user;
    private static final int PICK_IMAGE = 101;
    private static final int PICK_VIDEO = 102;
    private static final int PICK_HTML = 103;
    private static final int FILE_SELECT_CODE = 123;
    private static final String[] dialogList = {"Image", "Video"};
    private static final int DIALOG_IMPORT = 1;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    private boolean IsImg = false;
    private boolean IsPdf = false;
    private boolean IsVideo = false;
    private boolean IsExcel = false;
    private boolean IsDoc = false;
    private boolean IsUploadButtonClick = false;
    private String strSelectedData = "";
    private String selectedFileType;
    private String filePath;
    private ProgressDialog progressDialog;
    public PostDataFragment() {

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private String encodePDFToBase64(String filepath) throws IOException {
        InputStream inputStream = new FileInputStream(filepath);
        String inputStreamToString = inputStream.toString();
        byte[] byteArray = inputStreamToString.getBytes();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        user = new DBHelper().getUser();
        if (!user.getUserPicture().isEmpty()) {
            Picasso.with(getActivity())
                    .load(user.getUserPicture() + "&token=" + MyApplication.mySharedPreference.getUserToken())
                    .resize(200, 200)
                    .placeholder(getResources().getDrawable(R.drawable.user_new))
                    .centerCrop()
                    .into(header_imageView);
        } else {
            header_imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
        }
        tv_userName.setText(user.getFullname());

        ll_remove.setOnClickListener(this);
        ll_file.setOnClickListener(this);
        ll_tag.setOnClickListener(this);
        ll_location.setOnClickListener(this);
        btn_post.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_remove:

                iv_postImage.setVisibility(View.GONE);
                rl_videoView.setVisibility(View.GONE);

                break;
            case R.id.ll_file:

                if (Utility.checkExternalStoragePermission(getActivity())) {
                    IsImg = false;
                    IsPdf = false;
                    IsVideo = false;
                    IsExcel = false;
                    IsDoc = false;
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.select_file))
                            .setItems(dialogList, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //L.t("" + dialogList[which]);
                                    boolean flag = true;
                                    switch (which) {
                                        case 0:
                                            selectedFileType = "image";
                                            IsImg = true;
                                            selectImage();
                                            break;
                                        case 2:
                                            selectedFileType = "document";
                                            IsDoc = true;
                                            //chooseDoc();

                                            break;
                                        case 4:
                                            selectedFileType = "pdf";
                                            IsPdf = true;
                                            //choosePDF();
                                            break;
                                        case 3:
                                            selectedFileType = "excel";
                                            IsExcel = true;
                                            // chooseExcel();
                                            break;
                                        case 1:
                                            selectedFileType = "video";
                                            IsVideo = true;
                                            chooseVideo();
                                            break;
                                    }

                                }


                            }).show();
                } else {
                    Utility.callExternalStoragePermission(getActivity());
                }

                break;
            case R.id.ll_tag:

                break;
            case R.id.ll_location:
                et_location.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_post:
                if (validateFields()) {
                    if (filePath == null) {

                        if (L.isNetworkAvailable(getActivity())) {
                           progressDialog = new CustomProgressDialog(getActivity(),"");
                            progressDialog.show();
                            uploadrequestCall(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addpost&moodlewsrestformat=json", "");

                        } else {
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_network_connection))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                        }

                    } else {
                        if (L.isNetworkAvailable(getActivity())) {
                            postData(Constants.BE_LMS_PIC_UPLOAD_URL + MyApplication.mySharedPreference.getUserToken());
                        } else {
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
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
                break;
            default:
                break;
        }
    }

    private void uploadrequestCall(String url, final String id) {
        // L.pd("Uploading", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                L.l(getActivity(), "REPLY QUERY RESPONSE: " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() != 0) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                        if (jsonObject.getString("Status").equals("Success")) {

                            new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setCustomImage(R.drawable.success_circle)
                                    .setContentText(jsonObject.getString("message"))
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            //clearPreviousUserNotification();
                                            replaceFrgament(new NewsAndUpdateFragment());

                                        }
                                    }).show();
                        } else {

                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                // update(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*action = reply_on_queries
                userid = 14528
                replay_text = Query1 Reply
                        query_id = 1
*/
                //params.put("filename", L.getText(etUploadToServerTitle));
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("tag", "Tagged");
                params.put("subject", "Test");
                params.put("message", et_post_text.getText().toString());
                params.put("filetype", selectedFileType);
                params.put("file", id);
                params.put("location", et_location.getText().toString());
                //L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    // For Images
    private void selectImage() {
        galleryIntent();
    }

    private void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), Request.SELECT_IMAGE);

        } else {
            L.l("bellow kitkat");
            List<Intent> targets = new ArrayList<Intent>();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            List<ResolveInfo> candidates = getActivity().getPackageManager().queryIntentActivities(intent, 0);

            for (ResolveInfo candidate : candidates) {
                String packageName = candidate.activityInfo.packageName;
                if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.google.android.apps.docs")) {
                    Intent iWantThis = new Intent();
                    iWantThis.setType("image/*");
                    iWantThis.setAction(Intent.ACTION_GET_CONTENT);
                    iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    iWantThis.setPackage(packageName);
                    targets.add(iWantThis);
                }
            }
            Intent chooser = Intent.createChooser(targets.remove(0), "Select Image");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
            startActivityForResult(chooser, Request.SELECT_IMAGE);
        }
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    public static boolean checkFileValidation(String filePath) throws NullPointerException {
        File sourceFile = new File(filePath);
        String fileExt = CommonFunctions.getExtension(filePath);
        int maxFilesize = 1;
        if (fileExt.equalsIgnoreCase("pdf")) {
            maxFilesize = Request.PDF_MAX_SIZE;
        } else if (fileExt.equalsIgnoreCase("png") || fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg")) {
            maxFilesize = Request.IMAGE_MAX_SIZE;
        } else if (fileExt.equalsIgnoreCase("mp4") || fileExt.equalsIgnoreCase("3gp") || fileExt.equalsIgnoreCase("mpeg")) {
            maxFilesize = Request.VIDEO_MAX_SIZE;
        } else if (fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx")) {
            maxFilesize = Request.EXCEL_MAX_SIZE;
        } else if (fileExt.equalsIgnoreCase("doc") || fileExt.equalsIgnoreCase("docx")) {
            maxFilesize = Request.DOC_MAX_SIZE;
        }
        return CommonFunctions.getFileSize(sourceFile) < maxFilesize;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Request.SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                    progressDialog = new CustomProgressDialog(getActivity(),"");
                    progressDialog.show();
                    new VideoCompressor().execute();
                } else {
                    //filePath = getRealPathFromUri(getActivity(), data.getData());
                    Uri uri = data.getData();
                    Log.e("====", uri.toString());
                    progressDialog = new CustomProgressDialog(getActivity(),"");
                    progressDialog.show();
                    filePath = FilePath.getPath(getActivity(), uri);
                    new VideoCompressor().execute();
                    try {
                        Bitmap bm = null;
                        bm = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), data.getData());
                      //  strSelectedData = getStringFile(new File(filePath));
                        iv_postImage.setVisibility(View.GONE);
                        vv_postVideo.setVisibility(View.GONE);
                        rl_videoView.setVisibility(View.GONE);
                        Uri videoUri = Uri.parse(filePath);
                        //  Uri videoUri = Uri.parse(filePath);
                        Log.d("Video Player", filePath);
                        vv_postVideo.setVideoURI(videoUri);
                        vv_postVideo.requestFocus();
                        //videoView.start();
                        //Log.e("_+_++_+_+__+", strSelectedData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Log.d(TAG, "onActivityResult VIDEO: FILE PATH: " + filePath);
                if (checkFileValidation(filePath)) {
                    String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                    L.l(TAG, "VIDEO PATH: " + filePath);
                    String fileExtention = CommonFunctions.getExtension(filename);
                    if (fileExtention.equalsIgnoreCase("mp4") || fileExtention.equalsIgnoreCase("3gp") || fileExtention.equalsIgnoreCase("mpeg")) {
                        // tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid file")
                                .setContentText("Choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
//                    tvUploadToServerFilePath.setText("");
                    showDialog(Request.SELECT_VIDEO);
                }

            } else if (requestCode == Request.SELECT_IMAGE) {
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
                    // filePath = getRealPathFromUri(getActivity(), data.getData());
                    strSelectedData = "";
                    Uri uri = data.getData();
                    filePath = FilePath.getPath(getActivity(), uri);
                    Log.e("====", uri.toString());
                    try {
                        Bitmap bm = null;
                        bm = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), uri);
                        iv_postImage.setVisibility(View.VISIBLE);
                        rl_videoView.setVisibility(View.GONE);
                        iv_postImage.setImageBitmap(bm);
                        //strSelectedData = encodeToBase64(bm, Bitmap.CompressFormat.PNG, 100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onActivityResult IMAGE: FILE PATH: " + filePath);
                if (checkFileValidation(filePath)) {
                    String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                    L.l(TAG, "IMAGE PATH: " + filePath);
                    String fileExtention = CommonFunctions.getExtension(filename);
                    if (fileExtention.equalsIgnoreCase("png") || fileExtention.equalsIgnoreCase("jpg") || fileExtention.equalsIgnoreCase("jpeg")) {
                        //  tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    //tvUploadToServerFilePath.setText("");
                    showDialog(Request.SELECT_IMAGE);
                }

            } else if (requestCode == Request.PICK_PDF) {
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
//                    filePath = getRealPathFromUri(getActivity(), data.getData());
                    strSelectedData = "";
                    Uri uri = data.getData();
                    Log.e("====", uri.toString());
                    filePath = FilePath.getPath(getActivity(), uri);
                    try {
                        strSelectedData = getStringFile(new File(filePath));
                        Log.e("_+_+_++_", encodePDFToBase64(filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onActivityResult PDF: FILE PATH: " + filePath);
                if (checkFileValidation(filePath)) {
                    String fileExtention = CommonFunctions.getExtension(filePath);
                    L.l(TAG, "PDF PATH: " + filePath);
                    String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                    L.l(TAG, "PDF PATH: " + filePath);
                    if (fileExtention.equals("pdf")) {
                        //tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    //tvUploadToServerFilePath.setText("");
                    showDialog(Request.PICK_PDF);
                }
            } else if (requestCode == Request.PICK_EXCEL) {
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
//                    filePath = getRealPathFromUri(getActivity(), data.getData());
                    strSelectedData = "";
                    Uri uri = data.getData();
                    Log.e("====", uri.toString());
                    filePath = FilePath.getPath(getActivity(), uri);
                    try {
                        strSelectedData = getStringFile(new File(filePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onActivityResult PDF: FILE PATH: " + filePath);
                if (checkFileValidation(filePath)) {
                    String fileExtention = CommonFunctions.getExtension(filePath);
                    L.l(TAG, "PDF PATH: " + filePath);
                    String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                    L.l(TAG, "PDF PATH: " + filePath);
                    if (fileExtention.equals("xls") || fileExtention.equals("xlsx")) {
                        // tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    // tvUploadToServerFilePath.setText("");
                    showDialog(Request.PICK_EXCEL);
                }
            } else if (requestCode == Request.PICK_DOC) {
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
                    strSelectedData = "";
                    Uri uri = data.getData();
                    filePath = FilePath.getPath(getActivity(), uri);
                    try {
                        strSelectedData = getStringFile(new File(filePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onActivityResult DOC: FILE PATH: " + filePath);
                if (checkFileValidation(filePath)) {
                    String fileExtention = CommonFunctions.getExtension(filePath);
                    L.l(TAG, "PDF DOC: " + filePath);
                    String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                    L.l(TAG, "PDF DOC: " + filePath);
                    if (fileExtention.equals("doc") || fileExtention.equals("docx")) {
                        // tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    //tvUploadToServerFilePath.setText("");
                    showDialog(Request.PICK_DOC);
                }
            }
        }
    }

    private void showDialog(int fileType) {
        String msg = "";
        switch (fileType) {
            case Request.SELECT_IMAGE:
                msg = "Image max size " + Request.IMAGE_MAX_SIZE + "MB";
                break;
            case Request.SELECT_VIDEO:
                msg = "Video max size " + Request.VIDEO_MAX_SIZE + "MB";
                break;
            case Request.PICK_PDF:
                msg = "Pdf max size " + Request.IMAGE_MAX_SIZE + "MB";
                break;

        }
        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                .setTitleText("File size is too large")
                .setContentText(msg).show();
    }


    // For Video
    private void chooseVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a Video"), Request.SELECT_VIDEO);
        } else {
            L.l("bellow kitkat");
            List<Intent> targets = new ArrayList<Intent>();
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            List<ResolveInfo> candidates = getActivity().getPackageManager().queryIntentActivities(intent, 0);

            for (ResolveInfo candidate : candidates) {
                String packageName = candidate.activityInfo.packageName;
                if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.google.android.apps.docs")) {
                    Intent iWantThis = new Intent();
                    iWantThis.setType("video/*");
                    iWantThis.setAction(Intent.ACTION_GET_CONTENT);
                    iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    iWantThis.setPackage(packageName);
                    targets.add(iWantThis);
                }
            }
            Intent chooser = Intent.createChooser(targets.remove(0), "Select a Video");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
            startActivityForResult(chooser, Request.SELECT_VIDEO);
        }
    }


    private boolean validateFields() {
        boolean flag = true;
        /*if (!MyValidator.isValidSpinner(spUploadToServerCategory)) {
            L.t(getString(R.string.err_select_category));
            flag = false;
        } else if (!MyValidator.isValidSpinner(spUploadToServerSubCategory)) {
            L.t(getString(R.string.err_select_subcategory));
            flag = false;
        } else */
        if (!MyValidator.isValidField(et_post_text, getString(R.string.err_enter_post))) {
            flag = false;
        }
//        else if (!MyValidator.isValidField(etUploadToServerDesc, getString(R.string.err_enter_desc))) {
//            flag = false;
//        } else if (!MyValidator.isValidField(tvUploadToServerFilePath, getString(R.string.err_select_file))) {
//            flag = false;
//            L.t("Select File");
//        }
        L.l(TAG, "VALIDATE FLAG: " + flag);
        return flag;
    }

    private void saveProfileAccount() {

        L.pd("Uploading profile pic", "Please Wait..", getActivity());
        String url = Constants.BE_LMS_PIC_UPLOAD_URL + MyApplication.mySharedPreference.getUserToken();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                Log.e("=============", resultResponse);
                try {
                    JSONArray jsonArray = new JSONArray(resultResponse);

                    if (jsonArray.length() > 0) {
                        JSONObject result = (JSONObject) jsonArray.get(0);
                        String status = result.getString("Status");
                        if (status.equalsIgnoreCase("Success")) {

                            if (L.isNetworkAvailable(getActivity())) {

                                uploadrequestCall(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addpost&moodlewsrestformat=json", result.getString("itemid"));

                            } else {
                                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                        .setContentText(getString(R.string.err_network_connection))
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                            @Override
                                            public void onClick(PKBDialog customDialog) {
                                                customDialog.dismiss();
                                            }
                                        }).show();
                            }
                        } else {
                            L.dismiss_pd();
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText("Something went wrong to upload document")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                        }
                    } else {
                        L.dismiss_pd();
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Something went wrong to upload document")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("Status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("filearea", "draft");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                String fileName = CommonFunctions.getFileName(filePath);
                File file = new File(filePath);
                byte[] bytesArray = new byte[(int) file.length()];
                //params.put("filedata", new VolleyMultipartRequest.DataPart(fileName, bytesArray, "video/*"));
                params.put("filedata", new VolleyMultipartRequest.DataPart(fileName, AppHelper.getFileDataFromDrawable(getActivity(), iv_postImage.getDrawable()), "image/*"));
                return params;
            }
        };

        //VolleySingleton.getsInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        VolleySingleton.getsInstance().addToRequestQueue(multipartRequest);
    }

    private void uploadVideo(final List<KeyValue> param) {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            private Context context;

            public UploadVideo(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //uploading = ProgressDialog.show(context, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //  uploading.dismiss();
                L.dismiss_pd();
                try {
                    L.l("UPLOADED AT " + s);
                    JSONObject jsonObject = null;
                    if (!TextUtils.isEmpty(s)) {
                        JSONArray jsonArray = new JSONArray(s);

                        if (jsonArray.length() > 0) {
                            JSONObject result = (JSONObject) jsonArray.get(0);
                            String status = result.getString("Status");
                            if (status.equalsIgnoreCase("Success")) {
                                // uploadImageId(result.getString("itemid"));
                                if (L.isNetworkAvailable(getActivity())) {

                                    uploadrequestCall(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addpost&moodlewsrestformat=json", result.getString("itemid"));

                                }
                            }
                        }
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText("Something went wrong to upload document")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
                                    }
                                }).show();
                        //L.t("Something went wrong to upload document");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                L.l(TAG, "DO IN BACKGROUND CALL");
                UploadFile u = new UploadFile();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    L.l(TAG, "SDK ABOVE KITKAT FILE PATH: " + filePath);
                } else {
                    L.l(TAG, "SDK BELOW KITKAT FILE PATH: " + filePath);
                }
                String msg = u.uploadVideo(filePath, param);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo(getActivity());
        uv.execute();

    }

    public void postData(String url){
        progressDialog = new CustomProgressDialog(getActivity(),"");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        // Add request params excluding files below
        HashMap<String, String> params = new HashMap<>();
        params.put("filearea", "draft");

//Add files to a request
        HashMap<String, File> fileParams = new HashMap<>();

        File file = new File(filePath);
        fileParams.put("filedata", file);


        MultipartRequest mMultipartRequest = new MultipartRequest(url,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        // error handling
                       progressDialog.dismiss();
                    }
                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // mProgressDialog.cancel();
                        //Toast.makeText(getActivity(), "Multipart response check", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() > 0) {
                                JSONObject result = (JSONObject) jsonArray.get(0);
                                String status = result.getString("Status");
                                if (status.equalsIgnoreCase("Success")) {

                                    if (L.isNetworkAvailable(getActivity())) {

                                        uploadrequestCall(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_addpost&moodlewsrestformat=json", result.getString("itemid"));

                                    } else {
                                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                                .setContentText(getString(R.string.err_network_connection))
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                                    @Override
                                                    public void onClick(PKBDialog customDialog) {
                                                        customDialog.dismiss();
                                                    }
                                                }).show();
                                    }
                                } else {
                                    L.dismiss_pd();
                                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                            .setContentText("Something went wrong to upload document")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                                @Override
                                                public void onClick(PKBDialog customDialog) {
                                                    customDialog.dismiss();
                                                }
                                            }).show();
                                }
                            } else {
                                L.dismiss_pd();
                                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                        .setContentText("Something went wrong to upload document")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                            @Override
                                            public void onClick(PKBDialog customDialog) {
                                                customDialog.dismiss();
                                            }
                                        }).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, fileParams, params
        );
        mMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/**
 * adding request to queue
 */
        requestQueue.add(mMultipartRequest);

    }

    private class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(filePath);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                Log.e("Compression", "Compression successfully!");
                Log.e("Compressed File Path", "" + MediaController.cachedFile.getPath());
                filePath = "" + MediaController.cachedFile.getPath();
                progressDialog.dismiss();

            }

        }
    }



}
