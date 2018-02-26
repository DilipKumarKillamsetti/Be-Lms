package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.activities.ViewDocument;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.MostViewedAdapter;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.MostView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostViewedByOtherFragment extends Fragment implements MyCallback, NetworkMethod {
    public static final String TAG = MostViewedByOtherFragment.class.getSimpleName();
    @BindView(R.id.rvMostViewedByOther)
    RecyclerView rvMostViewedByOther;
    @BindView(R.id.tvMostViewedByOtherRecordNotFound)
    TextView tvMostViewedByOtherRecordNotFound;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private MostViewedAdapter adapter;
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private ArrayList<MostView> mostViewsByOther;
    private String download_url;
    private MainActivity mainActivity;
    public MostViewedByOtherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_most_viewed_by_other, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        callApi();
        mostViewsByOther = new ArrayList<>();

        rvMostViewedByOther.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMostViewedByOther.setHasFixedSize(true);
        //rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new MostViewedAdapter(getActivity(),mostViewsByOther);
        adapter.setMyCallback(this);
        rvMostViewedByOther.setAdapter(adapter);

    }

    private void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvMostViewedByOther.setVisibility(View.VISIBLE);
            }

            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_othersviewed&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvMostViewedByOther.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
//            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                    .setContentText(getString(R.string.err_network_connection))
//                    .setConfirmText("OK")
//                    .show();
        }
    }

    @Override
    public void myCallback(final int position) {
        download_url = mostViewsByOther.get(position).getFileurl()+"&token="+MyApplication.mySharedPreference.getUserToken();
        final String extension = CommonFunctions.getExtension(mostViewsByOther.get(position).getFilename());
        L.l(TAG, "callback call Type: " + extension);
        L.l(TAG, "callback call PATH FILE: " + download_url);
        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("JPE")
                || extension.equalsIgnoreCase("IMG")
                || extension.equalsIgnoreCase("GIF")
                || extension.equalsIgnoreCase("PSD")
                || extension.equalsIgnoreCase("jpeg")) {
            // showImageDialog(technicalUploadList.get(position));

            startActivity(new Intent(getActivity(), ProfilePictureActivity.class)
                    .putExtra("image_url", mostViewsByOther.get(position).getFileurl()+"&token="+MyApplication.mySharedPreference.getUserToken())
                    .putExtra("title", mostViewsByOther.get(position).getName())
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
                //L.t(getString(R.string.err_network_connection));
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                            }
                        }).show();
            }
        } else if (extension.equalsIgnoreCase("html")) {
            //  bundle.putString("testpaper_url",""+url);
            if (L.isNetworkAvailable(getActivity())) {

                HtmlViewFragment htmlViewFragment = HtmlViewFragment.newInstance(download_url);
                mainActivity.replaceFrgament(htmlViewFragment);
            } else {
                //L.t(getString(R.string.err_network_connection));
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                            }
                        }).show();
            }
        } else {
//            if (CommonFunctions.checkDocumentAllreaddyExist(mostViewsByOther.get(position).getFileurl())) {
//                File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), mostViewsByOther.get(position).getFileurl());
//                new CommonFunctions().openFile(getActivity(), file.getPath());
//            } else {
//                if (L.isNetworkAvailable(getActivity())) {
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage("To view " + mostViewsByOther.get(position).getName() + " please download it.")
//                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (Utility.checkExternalStoragePermission(getActivity())) {
//
//                                        L.l(TAG, "Document url " + download_url);
//                                        Uri uri = Uri.parse(download_url);
//                                        String filename = uri.getLastPathSegment();
//                                        L.l(TAG, "file to download " + filename);
//                                        //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
//                                        long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, mostViewsByOther.get(position).getFilename());
//                                        L.l(TAG, "Reference ID: " + referencedID);
//                                        //  technicalUploadList.get(position).setTechnicalDocumentReferenceID(referencedID);
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
//                    //L.t(getString(R.string.err_network_connection));
//                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                            .setContentText(getString(R.string.err_network_connection))
//                            .setConfirmText("OK")
//                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                                @Override
//                                public void onClick(PKBDialog customDialog) {
//                                    customDialog.dismiss();
////ChangeMobileNumberActivity.this.finish();
//                                }
//                            }).show();
//                }
//            }
            startActivity(new Intent(getActivity(), ViewDocument.class)
                    .putExtra("doc_url", mostViewsByOther.get(position).getFileurl() + "&token=" + MyApplication.mySharedPreference.getUserToken())
                    .putExtra("doc_name", mostViewsByOther.get(position).getFilename()));

        }
    }

    @Override
    public void request(String url) { JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,   new Response.Listener<JSONArray>()  {
        @Override
        public void onResponse(JSONArray response) {

            try {
                if(response.length()>0){
                    JSONObject jsonObject = response.getJSONObject(0);
                    if(jsonObject.getString("Status").equalsIgnoreCase("Success")){

                        JSONArray jsonArray = jsonObject.getJSONArray("mostviewed");

                        for(int i = 0 ; i<jsonArray.length();i++){

                            JSONObject modulesObject = (JSONObject) jsonArray.get(i);
                            MostView mostView = new Gson().fromJson(String.valueOf(jsonArray.get(i)), MostView.class);
                            mostViewsByOther.add(mostView);
                        }
                        if (mostViewsByOther.size() > 0) {
                            adapter.setVievedList(mostViewsByOther);
                            adapter.notifyDataSetChanged();
                        } else {
                            tvMostViewedByOtherRecordNotFound.setVisibility(View.VISIBLE);
                        }
                    }else{
                        tvMostViewedByOtherRecordNotFound.setVisibility(View.VISIBLE);
                    }
                }else{
                    tvMostViewedByOtherRecordNotFound.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            L.dismiss_pd();

//            String responseData = "{\"statusCode\":\"200\",\"result\":\"Success\",\"message\":\"Success\",\"data\":{\"id\":\"121\",\"username\":\"U000121\",\"firstname\":\"Vijay\",\"lastname\":\"Kumavat\",\"emailid\":\"v11.kumavat@yahoo.in\",\"mobileno\":\"+918149143550\",\"organization_code\":\"\",\"organization\":\"Krios\",\"picture\":\"\",\"location\":\"296\",\"designation\":\"4\",\"qualification\":\"25\",\"role\":\"Customer\",\"dob\":\"11-03-1999\",\"doj\":\"11-09-2017\",\"profiles\":\"Customer,Tester,Samriddhi\",\"groups\":\"MASL,Customer,Tester\",\"menurights\":{\"registration\":\"N\",\"search\":\"N\",\"powerolCare\":\"N\",\"mostViewed\":\"N\",\"myProfile\":\"N\",\"surveyFeedbacks\":\"N\",\"myTrainingPassport\":\"N\",\"learningTestQuizs\":\"N\",\"manualsBulletins\":\"Y\",\"trainingCalenderNomination\":\"N\",\"queriesResponse\":\"N\",\"technicalUploads\":\"N\",\"myFieldRecords\":\"N\",\"reports\":\"N\",\"manpowerEdition\":\"N\"}}}";
//            updateDisplay(responseData);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            L.dismiss_pd();
            L.l(getActivity(), "ERROR : " + error.getMessage());
            if (L.checkNull(error.getMessage())) {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText("Network problem please try again").show();
            }
        }
    }) ;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }
}
