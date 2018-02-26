package com.mahindra.be_lms.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.fragments.QueryFragment;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.KeyValue;
import com.mahindra.be_lms.util.AppHelper;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.FilePath;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.ImageLoadingUtils;
import com.mahindra.be_lms.util.UploadFile;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.util.VolleyMultipartRequest;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

    public class ProfilePictureActivity extends BaseActivity {
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    private static final String TAG = "ProfilePictureActivity";
    @BindView(R.id.ivProfilePicturePreview)
    ImageView ivprofilepicture;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private User user;
    private String userChoosenTask;
    private Bitmap bitmapImg;
    private String strProfile;
    private boolean notProfilePhoto;
    private ImageLoadingUtils utils;
    private Uri imageURI;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        utils = new ImageLoadingUtils(this);
        ActionBar actionBar = getSupportActionBar();
        try {
            getSupportActionBar().show();

            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            actionBar.setTitle(R.string.profile_picture_activity_label);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        user = new DBHelper().getUser();
        L.l("USER : " + user.toString());
        String imageURL = getIntent().getStringExtra("image_url");
        //get boolean value from query details activity send bitmap string
        notProfilePhoto = getIntent().getBooleanExtra("isBitmap", false);
        //this condtion for show bitmap from query details activity
        if (notProfilePhoto) {
            ivprofilepicture.setImageBitmap(ImageHelper.decodeBase64(imageURL));
        } else if (TextUtils.isEmpty(imageURL)) {
            if (!TextUtils.isEmpty(user.getUserPicture())) {
                boolean isFilexists = new ImageHelper().isFileExists(user.getUserID() + ".png");
                if (!user.getUserPicture().isEmpty()) {
                    Picasso.with(ProfilePictureActivity.this)
                            .load(user.getUserPicture()+"&token="+MyApplication.mySharedPreference.getUserToken())
                            .placeholder(getResources().getDrawable(R.drawable.user_new))
                            .into(ivprofilepicture);
//
                } else {
                    L.l(this, "Set Dummy Bitmap to header");
                    ivprofilepicture.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
                }
                L.l(this, "PROFIEL PIC FLAG SET TO HEADER : " + MyApplication.flagProfilePicSet);
            } else {
                L.l(this, "Set Dummy image");
                ivprofilepicture.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(this).load(imageURL).into(ivprofilepicture, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            actionBar.setTitle(getIntent().getStringExtra("title"));
            notProfilePhoto = getIntent().getBooleanExtra("notProfilePhoto", false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_picture_menu, menu);
        if (notProfilePhoto) {
            menu.findItem(R.id.menu_profile_pic_edit).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                L.l(this, "In Back btn option");
                this.finish();
                return true;
            case R.id.menu_profile_pic_edit:
                L.l(this, "On edit btn click");
                if (Utility.checkExternalStoragePermission(this)) {
                    selectImage();
                } else {
                    Utility.callExternalStoragePermission(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result=Utility.checkPermission(this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);
//IMP-----------------API25
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
//            fileTemp = ImageUtils.getOutputMediaFile();
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            if (fileTemp != null) {
//            fileUri = Uri.fromFile(fileTemp);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CAMERA);
//            } else {
//                Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
//            }
        } else {
            Toast.makeText(this, getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }


//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //full size image
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
//        imageURI = Uri.fromFile(file);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
//        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                filePath = FilePath.getPath(this, uri);
                Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                ivprofilepicture.setImageBitmap(myBitmap);
                saveProfileAccount("",myBitmap);

                //new ImageCompressionAsyncTask().execute(FilePath.getPath(this, uri));

            }            else if (requestCode == REQUEST_CAMERA) {
                // new ImageCompressionAsyncTask().execute(FilePath.getPath(this, uri));
                 filePath = FilePath.getPath(this, imageURI);
                Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                ivprofilepicture.setImageBitmap(myBitmap);
                saveProfileAccount("",myBitmap);
                Log.d(TAG, "onActivityResult: FILE PATH: " + filePath);
               // new QueryFragment.ImageCompressionAsyncTask().execute(filePath);
                //new ImageCompressionAsyncTask().execute(FilePath.getPath(this, imageURI));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                callUpdateProfile(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callUpdateProfile(Bitmap bm) {
        try {
            bitmapImg = bm;
            strProfile = ImageHelper.encodeToBase64(bm, Bitmap.CompressFormat.PNG, 20);
            L.l(TAG, "Base64 IMAGE String: " + strProfile);
            if (L.isNetworkAvailable(this)) {
                if (!TextUtils.isEmpty(strProfile)) {
                    updateProfileImage();
                } else {
                    //L.t("Error while capturing image. Please try again");
                    new PKBDialog(this, PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_profile_image_capture_msg))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                @Override
                                public void onClick(PKBDialog customDialog) {
                                    customDialog.dismiss();
                                }
                            }).show();
                }
            } else {
                //  L.t("Please check internet connectivity");
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProfileImage() {
        L.pd("Updating profile image", "Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "PROFILE IMG UPDATE RESPONSE: " + response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                            Log.e("======",response);
                            ivprofilepicture.setImageBitmap(bitmapImg);
                            //L.t("Image updated successfully");
                            new PKBDialog(ProfilePictureActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setContentText(getString(R.string.profile_image_updated_msg))
                                    .setCustomImage(R.drawable.success_circle)
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            //ChangeMobileNumberActivity.this.finish();
                                            ProfilePictureActivity.this.finish();
                                        }
                                    }).show();
                            user.setUserPicture(user.getUserID() + ".png");
                            new DBHelper().updateUser(user);
                            boolean fileSave = new ImageHelper().createDirectoryAndSaveFile(bitmapImg, user.getUserID() + ".png");
                            MyApplication.flagProfilePicSet = fileSave;
                            L.l(TAG, "PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                        } else {
                            //L.t(jsonObject.getString("message"));
                            new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
                                    .setContentText(jsonObject.getString("message"))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "Profile IMG VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.UPDATE_PROFILE_IMAGE_ACTION);
                params.put("userid", new MySharedPreference(ProfilePictureActivity.this).getUserId());
                params.put("profile_pic", strProfile);
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        callUpdateProfile(thumbnail);
        // ivprofilepicture.setImageBitmap(thumbnail);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
             String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {
            String filePath = imageUri;
            Log.e("()__)(+)(",filePath);
            File file = new File(filePath);
            long lenbmpact = file.length() / 1024;
            Log.d("length--->", "" + lenbmpact);

            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            Log.d("length1--->", "" + actualHeight);

            int actualWidth = options.outWidth;
            Log.d("length2--->", "" + actualWidth);

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            //compreesion
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();

            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "Images/");

            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/Query.jpg");
            Log.d("Path-->", uriSting);
            return uriSting;

        }

        //move to next activity image
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Bitmap comp_bitmap = utils.decodeBitmapFromPath(result);
//           / callUpdateProfile(comp_bitmap);

            List<KeyValue> param = new ArrayList<>();
            param.add(new KeyValue("filearea","draft"));
            uploadVideo(param);


            //strQueryImg=ImageHelper.encodeToBase64(comp_bitmap, Bitmap.CompressFormat.PNG,20);
            //ivQueryFragmentPicture.setImageBitmap(comp_bitmap);

        }

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
               // L.dismiss_pd();
                try {
                    L.l("UPLOADED AT " + s);
                    JSONObject jsonObject = null;
                    if (!TextUtils.isEmpty(s)) {
                        jsonObject = new JSONObject(s);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                            new PKBDialog(ProfilePictureActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setCustomImage(R.drawable.success_circle)
                                    .setContentText("File uploaded successfully")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
//                                            IsUploadButtonClick = false;
//                                            clearField();
                                        }
                                    }).show();
                        }
                    } else {
                        new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
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
                    //L.l(TAG, "SDK ABOVE KITKAT FILE PATH: " + filePath);
                } else {
                   // L.l(TAG, "SDK BELOW KITKAT FILE PATH: " + filePath);
                }
                String msg = u.uploadVideo(filePath, param);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo(ProfilePictureActivity.this);
        uv.execute();

    }

    private void saveProfileAccount(String url1, final Bitmap bitmap) {
        // loading or check internet connection or something...
        // ... then
        L.pd("Uploading profile pic", "Please Wait..", ProfilePictureActivity.this);
        String url = Constants.BE_LMS_PIC_UPLOAD_URL+MyApplication.mySharedPreference.getUserToken();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONArray jsonArray =  new JSONArray(resultResponse);
                    if(jsonArray.length()>0){
                        JSONObject result = (JSONObject) jsonArray.get(0);
                        String status = result.getString("Status");
                        if (status.equalsIgnoreCase("Success")) {
                            uploadImageId(result.getString("itemid"));
                        } else {
                            L.dismiss_pd();
                            new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
                                    .setContentText("Something went wrong to upload document")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                        }
                                    }).show();
                        }
                    }else{
                        L.dismiss_pd();
                        ivprofilepicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
                        new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
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
                ivprofilepicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
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
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
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
                params.put("filedata", new VolleyMultipartRequest.DataPart(fileName, AppHelper.getFileDataFromDrawable(getBaseContext(), ivprofilepicture.getDrawable()), "image/*"));

                return params;
            }
        };

        //VolleySingleton.getsInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        VolleySingleton.getsInstance().addToRequestQueue(multipartRequest);
    }

    private void uploadImageId(String s) {
        String url = Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&moodlewsrestformat=json&draftitemid="+s+"&wsfunction=core_user_update_picture";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,   new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if(response.getString("Status").equalsIgnoreCase("success")){
                        user.setUserPicture(response.getString("profileimageurl"));
                        new DBHelper().updateUser(user);

                        L.dismiss_pd();
                        new PKBDialog(ProfilePictureActivity.this, PKBDialog.CUSTOM_IMAGE_TYPE)
                                .setCustomImage(R.drawable.success_circle)
                                .setContentText("File uploaded successfully")
                                .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                    @Override
                                    public void onClick(PKBDialog customDialog) {
                                        customDialog.dismiss();
//                                            IsUploadButtonClick = false;
//                                            clearField();
                                    }
                                }).show();
                    }else{
                        L.dismiss_pd();
                        ivprofilepicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
                        new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
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
                    L.dismiss_pd();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(ProfilePictureActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(ProfilePictureActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }
}