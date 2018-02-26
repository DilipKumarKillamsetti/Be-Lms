package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ReplyQueryActivity;
import com.mahindra.be_lms.activities.TechnicalUploadDisclaimerActivity;
import com.mahindra.be_lms.db.Category;
import com.mahindra.be_lms.db.SubCategory;
import com.mahindra.be_lms.interfaces.Request;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.KeyValue;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.FilePath;
import com.mahindra.be_lms.util.UploadFile;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

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
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadToServerFragment extends Fragment {
    public static final String TAG = UploadToServerFragment.class.getSimpleName();
    private static final int PICK_IMAGE = 101;
    private static final int PICK_VIDEO = 102;
    private static final int PICK_HTML = 103;
    private static final int FILE_SELECT_CODE = 123;
    private static final String[] dialogList = {"Image", "Document", "Pdf","Excel","Video"};
    private static final int DIALOG_IMPORT = 1;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    @BindView(R.id.llUploadToServer)
    LinearLayout llUploadToServer;
    @BindView(R.id.etUploadToServerTitle)
    EditText etUploadToServerTitle;
    @BindView(R.id.etUploadToServerDesc)
    EditText etUploadToServerDesc;
    @BindView(R.id.tvUploadToServerFilePath)
    TextView tvUploadToServerFilePath;
    @BindView(R.id.tvTechnicalUploadDisclaimer)
    TextView tvTechnicalUploadDisclaimer;
    //@BindView(R.id.spUploadToServerCategory)
    //Spinner spUploadToServerCategory;
    //  @BindView(R.id.spUploadToServerSubCategory)
    // Spinner spUploadToServerSubCategory;
    @BindView(R.id.rrUploadToServerCategory)
    RelativeLayout rrUploadToServerCategory;
    @BindView(R.id.rrUploadToServerSubCategory)
    RelativeLayout rrUploadToServerSubCategory;
    private String filePath;
    private ProgressDialog progressDialog;
    private List<Category> categoryList;
    private List<SubCategory> subCategoryList;
    private String selectedOption;
    private DBHelper dbHelper;
    private ArrayAdapter subCategoryAdapter;
    private MainActivity mainactivity;
    private boolean IsImg = false;
    private boolean IsPdf = false;
    private boolean IsVideo = false;
    private boolean IsExcel = false;
    private boolean IsDoc = false;
    private boolean IsUploadButtonClick = false;
    private String strSelectedData = "";
    private String selectedFileType;
    public UploadToServerFragment() {
        // Required empty public constructor
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
        } else if (fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx")){
            maxFilesize = Request.EXCEL_MAX_SIZE;
        } else if (fileExt.equalsIgnoreCase("doc") || fileExt.equalsIgnoreCase("docx")){
            maxFilesize = Request.DOC_MAX_SIZE;
        }
        return CommonFunctions.getFileSize(sourceFile) < maxFilesize;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            final String column = "_data";
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
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
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_to_server, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.technical_desclaimer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_tech_disclaimer) {
            startActivity(new Intent(getActivity(), TechnicalUploadDisclaimerActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @OnTouch(R.id.llUploadToServer)
    public boolean OnLayoutTouch() {
        new CommonFunctions().hideSoftKeyboard(getActivity());
        return false;
    }

    private void init() {
        etUploadToServerTitle.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }, new InputFilter.LengthFilter(50)

        });
        dbHelper = new DBHelper();
        //fetch the spinner list eg. companylist,designationlist,locationlist
        getData();
        //inistialize spinner with custom view layout
        subCategoryAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, subCategoryList);
        subCategoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //spUploadToServerSubCategory.setAdapter(subCategoryAdapter);
        // ArrayAdapter categoryAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, categoryList);
        //  categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //  spUploadToServerCategory.setAdapter(categoryAdapter);
        /*spUploadToServerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    L.l("Call item selected listener");
                    updateSubCategoryList(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private void updateSubCategoryList(int position) {
        subCategoryList.clear();
        subCategoryList = dbHelper.getSubCategoryListByCategoryID(categoryList.get(position).getCategoryID());
        L.l("subcategory list size : " + subCategoryList.size());
        subCategoryList.add(0, new SubCategory("0", "0", "Select SubCategory", "0"));
        subCategoryAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, subCategoryList);
        subCategoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //spUploadToServerSubCategory.setAdapter(subCategoryAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainactivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainactivity.getSupportActionBar().show();
            mainactivity.getSupportActionBar().setTitle("");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    OnTouch method for all relative Layout to launch particular spinner
    */
    @OnTouch({R.id.ivCategory, R.id.ivSubCategory, R.id.tvTechnicalUploadDisclaimer})
    public boolean onRelativeLayoutTouch(View view) {
        switch (view.getId()) {
            case R.id.ivCategory:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                //spUploadToServerCategory.performClick();
                break;
            case R.id.ivSubCategory:
                new CommonFunctions().hideSoftKeyboard(getActivity());
                //spUploadToServerSubCategory.performClick();
                break;
            case R.id.tvTechnicalUploadDisclaimer:
                startActivity(new Intent(getActivity(), TechnicalUploadDisclaimerActivity.class));
                break;
        }
        return false;
    }

    private void getData() {
        L.l(TAG, "[METHOD:getData] fetch data from sqlite categroyList,subCategoryList");
       /* categoryList = dbHelper.getCategoryList();
        categoryList.add(0, new Category("0", "Select Category", "1"));
        subCategoryList = dbHelper.getSubCategoryList();
        subCategoryList.add(0, new SubCategory("0", "0", "Select SubCategory", "0"));*/
    }

    @OnClick(R.id.btnUploadToServerSumbit)
    public void btnUploadToServerSumbit() {
        if (L.isNetworkAvailable(getActivity())) {
            L.l(TAG, "ISUploadbtnClick: " + IsUploadButtonClick);
            if (!IsUploadButtonClick) {
                IsUploadButtonClick = true;
                L.pd("Please wait", getActivity());
                if (validateFields()) {
                    L.l(TAG, "FLag: " + checkFileFormat(L.getText(tvUploadToServerFilePath)));
                    if (checkFileFormat(L.getText(tvUploadToServerFilePath))) {
                        L.dismiss_pd();
                        L.pd("Uploading File", "Please Wait..", getActivity());
                        if (L.isNetworkAvailable(getActivity())) {

                            uploadrequestCall(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_uploadtechnicalfile&moodlewsrestformat=json");

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

//                        List<KeyValue> param = new ArrayList<>();
////                        param.add(new KeyValue("action", Constants.ACTION_UPLOAD_DOCUMENT));
//                        //   param.add(new KeyValue("category_id", "");//categoryList.get(spUploadToServerCategory.getSelectedItemPosition()).getCategoryID()));
//                        //  param.add(new KeyValue("subcategory_id", "");//subCategoryList.get(spUploadToServerSubCategory.getSelectedItemPosition()).getSubCategoryID()));
//                        param.add(new KeyValue("filename", L.getText(etUploadToServerTitle)));
//                        param.add(new KeyValue("description", L.getText(etUploadToServerDesc)));
//                        param.add(new KeyValue("userid", new MySharedPreference(getActivity()).getUserId()));
//                        param.add(new KeyValue("filetype","image"));
//                        L.l("PARAM: " + param.toString());
//                        uploadVideo(param);
                    } else {
                        L.dismiss_pd();
                        L.t_long("Please select Image,Video,Pdf,xls/xlsx file formats only");
                        tvUploadToServerFilePath.setText("");
                    }
                } else {
                    IsUploadButtonClick = false;
                    L.dismiss_pd();
                }
            } else {
                L.l(TAG, "btn already clicked");
            }
        } else {
            // L.t(getString(R.string.err_network_connection));
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

    @OnClick(R.id.ibtnUploadToServerSelectFile)
    public void ibtnUploadToServerSelectFile() {
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
                                case 1:
                                    selectedFileType = "document";
                                    IsDoc = true;
                                    chooseDoc();

                                    break;
                                case 2:
                                    selectedFileType = "pdf";
                                    IsPdf = true;
                                    choosePDF();
                                    break;
                                case 3:
                                    selectedFileType = "excel";
                                    IsExcel = true;
                                    chooseExcel();
                                    break;
                                case 4:
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
    }

    private void chooseDoc() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("msword/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Select Doc"), Request.PICK_DOC);
        } else {
            Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(intent, "Select Doc"), Request.PICK_DOC);
        }

    }

    private void chooseExcel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Select Excel"), Request.PICK_EXCEL);
        } else {
            Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(intent, "Select Excel"), Request.PICK_EXCEL);
        }

    }

    private void choosePDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), Request.PICK_PDF);
        } else {
            Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), Request.PICK_PDF);
        }
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "[METHOD] void onActivityResult(requestCode:" + requestCode + ", resultCode:" + resultCode
                + ", data:" + data + ")");

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Request.SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                if (data.toString().contains("file://")) {
                    filePath = data.getData().getPath();
                } else {
                    //filePath = getRealPathFromUri(getActivity(), data.getData());
                    Uri uri = data.getData();
                    Log.e("====", uri.toString());
                    filePath = FilePath.getPath(getActivity(), uri);
                    try {
                        Bitmap bm = null;
                        bm = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), data.getData());
                        strSelectedData = getStringFile(new File(filePath));
                        Log.e("_+_++_+_+__+", strSelectedData);
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
                        tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid file")
                                .setContentText("Choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    tvUploadToServerFilePath.setText("");
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
                        strSelectedData = encodeToBase64(bm, Bitmap.CompressFormat.PNG, 100);
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
                        tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    tvUploadToServerFilePath.setText("");
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
                        tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    tvUploadToServerFilePath.setText("");
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
                        tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    tvUploadToServerFilePath.setText("");
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
                        tvUploadToServerFilePath.setText(filename);
                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setTitleText("Invalid File")
                                .setContentText("choose correct file format")
                                .show();
                    }
                } else {
                    filePath = "";
                    tvUploadToServerFilePath.setText("");
                    showDialog(Request.PICK_DOC);
                }
            }
        }
    }

    private boolean checkFileFormat(String filename) {
        L.l(TAG, "Check file format: " + filename);
        boolean flag = true;
        String extension = CommonFunctions.getExtension(filename);
        L.l(TAG, "extention: " + extension);
        if ((!extension.equalsIgnoreCase("pdf")) && (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg"))
                && (!extension.equalsIgnoreCase("mp4") && !extension.equalsIgnoreCase("3gp") && !extension.equalsIgnoreCase("mpeg") && !extension.equalsIgnoreCase("xls") && !extension.equalsIgnoreCase("xlsx") && !extension.equalsIgnoreCase("doc") && !extension.equalsIgnoreCase("docx"))) {
            L.l(TAG, "extention: " + extension);
            flag = false;
        }
        return flag;
    }

    private void showDialog(int fileType) {
        String msg = "";
        switch (fileType) {
            case Request.SELECT_IMAGE:
                msg = "Image max size " + Request.IMAGE_MAX_SIZE + "MB";
                break;
            case Request.SELECT_VIDEO:
                msg = "Video max size " + Request.IMAGE_MAX_SIZE + "MB";
                break;
            case Request.PICK_PDF:
                msg = "Pdf max size " + Request.IMAGE_MAX_SIZE + "MB";
                break;

        }
        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                .setTitleText("File size is too large")
                .setContentText(msg).show();
    }

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
        if (!MyValidator.isValidField(etUploadToServerTitle, getString(R.string.err_enter_title))) {
            flag = false;
        } else if (!MyValidator.isValidField(etUploadToServerDesc, getString(R.string.err_enter_desc))) {
            flag = false;
        } else if (!MyValidator.isValidField(tvUploadToServerFilePath, getString(R.string.err_select_file))) {
            flag = false;
            L.t("Select File");
        }
        L.l(TAG, "VALIDATE FLAG: " + flag);
        return flag;
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
                        jsonObject = new JSONObject(s);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                            new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                                    .setCustomImage(R.drawable.success_circle)
                                    .setContentText("File uploaded successfully")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
                                            IsUploadButtonClick = false;
                                            clearField();
                                        }
                                    }).show();
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

    private void clearField() {
        //spUploadToServerCategory.setSelection(0);
        //spUploadToServerSubCategory.setSelection(0);
        etUploadToServerTitle.setText("");
        etUploadToServerDesc.setText("");
        tvUploadToServerFilePath.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getSdcardPath() {
        String storepath = null;
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            // Do something for KITKAT and above versions
            File[] possible_kitkat_mounts = getActivity().getExternalFilesDirs(null);
            Log.e("kitkat_mounts.length", ":: " + possible_kitkat_mounts.length);

            if (possible_kitkat_mounts.length > 0) {
                for (int x = 0; x < possible_kitkat_mounts.length; x++) {
                    Log.e("test", "possible_kitkat_mounts " + possible_kitkat_mounts[x].toString());
                    if (possible_kitkat_mounts.length > 1) {
                        storepath = possible_kitkat_mounts[1].toString();
                        L.l(TAG, "External PATH: " + storepath);
                    } else {
                        storepath = possible_kitkat_mounts[0].toString();
                        L.l(TAG, "External PATH: " + storepath);
                    }
                }
            } else {
            }
        }
        return storepath;
    }

    private void uploadrequestCall(String url) {
       // L.pd("Uploading", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(getActivity(), "REPLY QUERY RESPONSE: " + response);
                update(response);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
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
                params.put("filename", L.getText(etUploadToServerTitle));
                params.put("description", L.getText(etUploadToServerDesc));
                params.put("userid", new MySharedPreference(getActivity()).getUserId());
                params.put("filetype",selectedFileType);
                params.put("file",strSelectedData);
                //L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void update(String response) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                new PKBDialog(getActivity(), PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.success_circle)
                        .setContentText("File uploaded successfully")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                IsUploadButtonClick = false;
                                clearField();
                            }
                        }).show();
            }
         else {
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

}
