package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.activities.ViewDocument;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.TechnicalUploadsAdapter;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.TechnicalUpload;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTechnicalUploadFragment extends Fragment implements MyCallback, NetworkMethod {
    public static final String TAG = MyTechnicalUploadFragment.class.getSimpleName();
    @BindView(R.id.rvTechnicalUpload)
    RecyclerView rvTechnicalUpload;
    @BindView(R.id.tvMyTechnicalUploadNoRecord)
    TextView tvMyTechnicalUploadNoRecord;
    List<TechnicalUpload> technicalUploadList;
    String filename;
    String filePath;
    int status;
    private MainActivity mainActivity;
    private DownloadManager downloadManager;
    private TechnicalUploadsAdapter adapter;
    private String download_url;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;


    public MyTechnicalUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_technical_uploads, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        init();
        return view;
    }

    private void init() {
        technicalUploadList = new ArrayList<>();
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);


        rvTechnicalUpload.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvTechnicalUpload.setHasFixedSize(true);
        rvTechnicalUpload.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new TechnicalUploadsAdapter(getActivity(), technicalUploadList);
        rvTechnicalUpload.setAdapter(adapter);
        adapter.setMyCallback(this);
        callApi();
    }

    private void callApi() {

        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvTechnicalUpload.setVisibility(View.VISIBLE);
            }
            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_mytechnicalfiles&userid="+MyApplication.mySharedPreference.getUserId()+"&moodlewsrestformat=json");
        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvTechnicalUpload.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                        @Override
//                        public void onClick(PKBDialog customDialog) {
//                            customDialog.dismiss();
//                        }
//                    }).show();
//            tvMyTechnicalUploadNoRecord.setVisibility(View.VISIBLE);
        }

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
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void myCallback(final int position) {
        L.l(TAG, "callback call");
        download_url = technicalUploadList.get(position).getAttachmentList();
        String extension = CommonFunctions.getExtension(technicalUploadList.get(position).getAttachmentList());
        L.l(TAG, "callback call Type: " + extension);
        L.l(TAG, "callback call PATH FILE: " + download_url);
        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("JPE")
                || extension.equalsIgnoreCase("IMG")
                || extension.equalsIgnoreCase("GIF")
                || extension.equalsIgnoreCase("PSD")
                || extension.equalsIgnoreCase("jpeg")) {
            startActivity(new Intent(getActivity(), ProfilePictureActivity.class)
                    .putExtra("image_url",technicalUploadList.get(position).getAttachmentList())
                    .putExtra("title", technicalUploadList.get(position).getTechnicalUploadTitle())
                    .putExtra("notProfilePhoto", true));
        } else if (extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("mpeg")
                || extension.equalsIgnoreCase("flv")
                || extension.equalsIgnoreCase("avi")
                || extension.equalsIgnoreCase("mov")
                || extension.equalsIgnoreCase("mpg")
                || extension.equalsIgnoreCase("wmv") || extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("swf")) {
            if (L.isNetworkAvailable(getActivity())) {
                Intent videoview_intent = new Intent(getActivity(), VideoViewActivity.class);
                videoview_intent.putExtra(getString(R.string.testpaper_url_parcelable_tag), download_url);
                startActivity(videoview_intent);
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }).show();
            }
        } else if (extension.equalsIgnoreCase("html")) {
            //  bundle.putString("testpaper_url",""+url);
            if (L.isNetworkAvailable(getActivity())) {
                HtmlViewFragment htmlViewFragment = HtmlViewFragment.newInstance(download_url);
                mainActivity.replaceFrgament(htmlViewFragment);
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }).show();
            }
        } else {
//            if (CommonFunctions.checkDocumentAllreaddyExist(technicalUploadList.get(position).getAttachmentList())) {
//                File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), technicalUploadList.get(position).getAttachmentList());
//                new CommonFunctions().openFile(getActivity(), file.getPath());
//            } else {
//                if (L.isNetworkAvailable(getActivity())) {
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage("To view " + technicalUploadList.get(position).getTechnicalUploadTitle() + " please download it.")
//                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (Utility.checkExternalStoragePermission(getActivity())) {
//                                        L.l(TAG, "Document url " + download_url);
//                                        Uri uri = Uri.parse(download_url);
//                                        String filename = uri.getLastPathSegment();
//                                        L.l(TAG, "file to download " + filename);
//                                        //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
//                                        long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, filename);
//                                        L.l(TAG, "Reference ID: " + referencedID);
//                                        technicalUploadList.get(position).setTechnicalDocumentReferenceID(referencedID);
////                                documentTreeMastersList.get(position).setDocReferencedID(referencedID);
////                                Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
////                                document.setDocumentReferencedID(referencedID);
////                                document.setDocumentHitDate(new Date());
////                                document.setDocumentHitCount(document.getDocumentHitCount() + 1);
////                                new DBHelper().updateDocument(document);
//                                    } else {
//                                        Utility.callExternalStoragePermission(getActivity());
//                                    }
//
//                                }
//                            }).setNegativeButton(getString(R.string.dialog_cancel), null)
//                            .show();
//                } else {
//                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                            .setContentText(getString(R.string.err_network_connection))
//                            .setConfirmText("OK")
//                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                                @Override
//                                public void onClick(PKBDialog customDialog) {
//                                    customDialog.dismiss();
//                                    getActivity().getSupportFragmentManager().popBackStack();
//                                }
//                            }).show();
//                }
//            }

            startActivity(new Intent(getActivity(), ViewDocument.class)
                    .putExtra("doc_url", technicalUploadList.get(position).getAttachmentList() + "&token=" + MyApplication.mySharedPreference.getUserToken())
                    .putExtra("doc_name", technicalUploadList.get(position).getTechnicalUploadTitle()));

        }
    }

    @Override
    public void request(String url) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
        @Override
        public void onResponse(JSONArray response) {
                        L.dismiss_pd();
                        L.l(TAG, "RESPONSE : " + response.toString());
                        try {
                            updateDisplay(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "ERROR : " + error.getMessage());
                tvMyTechnicalUploadNoRecord.setVisibility(View.VISIBLE);
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            // {"result":"success","message":"Success","data":[{"id":"1","news_title":"amita","attachments":"Image-289.jpg",
            // "entry_date":"04 Jan 2017","message":"amita"}]}
            if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                if (jsonObject.has("alluploads")) {
                    JSONArray technicalUploadsArray = new JSONArray(jsonObject.getJSONArray("alluploads").toString());
                    for (int i = 0; i < technicalUploadsArray.length(); i++) {
                        TechnicalUpload technicalUpload = new Gson().fromJson(String.valueOf(technicalUploadsArray.get(i)), TechnicalUpload.class);
                        technicalUploadList.add(technicalUpload);
                    }
                    if (technicalUploadList.size() > 0) {
                        adapter.setTechnicalUploadList(technicalUploadList);
                        adapter.notifyDataSetChanged();
                    } else {
                        tvMyTechnicalUploadNoRecord.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                // L.t(jsonObject.getString("message"));
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

    private void showImageDialog(TechnicalUpload technicalUpload) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_display_image_layout, null);
        TextView tvCloseDialog = (TextView) dialogView.findViewById(R.id.tvCloseDialog);
        TextView tvUploadImageTitle = (TextView) dialogView.findViewById(R.id.tvUploadedImageTitle);
        ImageView ivUploadedImage = (ImageView) dialogView.findViewById(R.id.ivUploadedImage);
        tvUploadImageTitle.setText(technicalUpload.getTechnicalUploadTitle());
        Picasso.with(getActivity()).load(Constants.DOC_UPLOAD_DOWNLOAD_URL + technicalUpload.getAttachmentList()).into(ivUploadedImage);

        builder.setView(dialogView).setCancelable(false);
        final Dialog dialog = builder.create();
        dialog.show();
        tvCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
