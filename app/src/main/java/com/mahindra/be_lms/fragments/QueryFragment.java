package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.db.Product;
import com.mahindra.be_lms.db.Queries;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.FilePath;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.util.ImageLoadingUtils;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueryFragment extends Fragment implements NetworkMethod {
    private static final int SELECT_FILE = 101;
    private static final int REQUEST_CAMERA = 102;
    private static final String TAG = "QueryFragment";
    @BindView(R.id.tvQueryFragmentProductTitle)
    TextView tvQueryFragmentProductTitle;
    @BindView(R.id.etQueryFragmentSubject)
    EditText etQueryFragmentSubject;
    @BindView(R.id.etQueryFragmentBody)
    EditText etQueryFragmentBody;
    @BindView(R.id.ivQueryFragmentPicture)
    ImageView ivQueryFragmentPicture;
    private MainActivity mainActivity;
    private String strQueryImg = "";
    private ImageLoadingUtils utils;
    private Uri imageURI;

    public QueryFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        utils = new ImageLoadingUtils(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                Product product = bundle.getParcelable(getString(R.string.product_parcelabel_tag));
                tvQueryFragmentProductTitle.setText(product.getProductName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btnQueryFragmentSumbit)
    public void btnQueryFragmentSumbit() {
        new CommonFunctions().hideSoftKeyboard(getActivity());
        if (validateFields()) {
            if (L.isNetworkAvailable(getActivity())) {
                request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_createproblem&moodlewsrestformat=json");
            } else {
                //L.t(getString(R.string.err_network_connection));
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

    @OnTouch(R.id.rlQueryFragment)
    public boolean onTouchLL() {
        new CommonFunctions().hideSoftKeyboard(getActivity());
        return false;
    }

    private boolean validateFields() {
        boolean flag = true;
        if (!MyValidator.isValidField(etQueryFragmentSubject, getString(R.string.err_enter_subject))) {
            flag = false;
        } else if (!MyValidator.isValidField(etQueryFragmentBody, getString(R.string.err_enter_body))) {
            flag = false;
        }
        return flag;
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ivQueryFragmentPicture)
    public void ivQueryFragmentPicture() {
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery),
                getString(R.string.dialog_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.dialog_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //full size image
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
//        imageURI = Uri.fromFile(file);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
//        startActivityForResult(intent, REQUEST_CAMERA);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            fileTemp = ImageUtils.getOutputMediaFile();
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            if (fileTemp != null) {
//            fileUri = Uri.fromFile(fileTemp);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CAMERA);
//            } else {
//                Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
//            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
    }
/*
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), data.getData());
                strQueryImg=ImageHelper.encodeToBase64(bm, Bitmap.CompressFormat.PNG, 20);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivQueryFragmentPicture.setImageBitmap(bm);
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        strQueryImg= ImageHelper.encodeToBase64(thumbnail, Bitmap.CompressFormat.PNG,100);
//       ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        assert thumbnail != null;
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ivQueryFragmentPicture.setImageBitmap(thumbnail);
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                String filePath = null;
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
                    filePath = getRealPathFromUri(getActivity(), data.getData());
                }
                Log.d(TAG, "onActivityResult: FILE PATH: " + filePath);
                new ImageCompressionAsyncTask().execute(filePath);

            } else if (requestCode == REQUEST_CAMERA) {
                String filePath = FilePath.getPath(getActivity(), imageURI);
                //String filePath = imageURI.getPath();
                Log.d(TAG, "onActivityResult: FILE PATH: " + filePath);
                new ImageCompressionAsyncTask().execute(filePath);
            }
        }
    }

    @Override
    public void request(String url) {
        L.pd("Submitting Query", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                L.dismiss_pd();
                L.l(TAG, "QUERY SUBMIT RESPONSE: " + response.toString());
                try {
                    submitQuery(response);
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "QUERY ERROR: " + error.getMessage());
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.somthing_went_wrong)).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("message", L.getText(etQueryFragmentBody));
                params.put("subject", L.getText(etQueryFragmentSubject));
                params.put("picture", strQueryImg);
                L.l(TAG, "QUERY PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void submitQuery(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
              /*  {"result":"success","message":"Query Submitted Successfully","insert_id":20}*/
//                Queries queries = new Queries();
//                queries.setProductID("");
//                queries.setQueryInsertedID("");
//                queries.setQuerySubject(L.getText(etQueryFragmentSubject));
//                queries.setQueryBody(L.getText(etQueryFragmentBody));
//                queries.setQueryAttachment(strQueryImg);
//                queries.setQueryStatus(false);
//                Log.d(TAG, "submitQuery: " + queries.toString());
//                new DBHelper().saveQuery(queries);
                // L.t(jsonObject.getString("message"));
                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText("Query submited succesfully.")
                        .setCustomImage(R.drawable.success_circle)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                // L.l(TAG, "QUERIES OBJECT TO SAVE: "+new DBHelper().getQueriesList().toString());
                                //popBackStack is go back to product fragment
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }).show();
            } else {
                //L.t(jsonObject.getString("message"));
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
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
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {
            String filePath = imageUri;
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
            strQueryImg = ImageHelper.encodeToBase64(comp_bitmap, Bitmap.CompressFormat.PNG, 20);
            ivQueryFragmentPicture.setImageBitmap(comp_bitmap);

        }

    }
}
