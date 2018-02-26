package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.QueryResponseActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.activities.ViewDocument;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.ManualInfoAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.ManualInfoModel;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/26/2017.
 */

public class ManualInfoFragment extends Fragment implements MyCallback, NetworkMethod {
    private static final String TAG = ManualInfoFragment.class.getSimpleName();
    @BindView(R.id.rvManualInfoList)
    RecyclerView rvManualInfoList;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private ManualInfoAdapter manualInfoAdapter;
    private DashboardActivity mainActivity;
    private ArrayList<ManualInfoModel> manualInfoModels;
    private String download_url;
    private DownloadManager downloadManager;

    public ManualInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.manual_information_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
       // mainActivity.setDrawerEnabled(false);
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        callApi();
        manualInfoModels = new ArrayList<>();

        rvManualInfoList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvManualInfoList.setHasFixedSize(true);
        //rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        manualInfoAdapter = new ManualInfoAdapter(getActivity(), manualInfoModels);
        manualInfoAdapter.setMyCallback(this);
        rvManualInfoList.setAdapter(manualInfoAdapter);
    }

    private void callApi() {

        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvManualInfoList.setVisibility(View.VISIBLE);
            }

            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=core_course_get_contents&courseid=1&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvManualInfoList.setVisibility(View.GONE);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }


    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: call");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {
                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void request(String url) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("coursescontents");
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(1);
                        JSONArray modulesArray = jsonObject1.getJSONArray("modules");
                        for (int i = 0; i < modulesArray.length(); i++) {
                            JSONObject modulesObject = (JSONObject) modulesArray.get(i);
                            ManualInfoModel manualInfoModel = new ManualInfoModel();
                            manualInfoModel.setId(modulesObject.getString("id"));
                            manualInfoModel.setManualName(modulesObject.getString("name"));
                            if (modulesObject.has("contents")) {
                                JSONArray contentsArray = modulesObject.getJSONArray("contents");
                                JSONObject contentsObject = (JSONObject) contentsArray.get(0);
                                manualInfoModel.setFileType(contentsObject.getString("filename"));
                                manualInfoModel.setFilePath(contentsObject.getString("fileurl"));
                            }
                            manualInfoModels.add(manualInfoModel);
                        }
                        if (manualInfoModels.size() > 0) {
                            manualInfoAdapter.setManualList(manualInfoModels);
                            manualInfoAdapter.notifyDataSetChanged();
                        } else {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                L.dismiss_pd();

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
        });
        int socketTimeout = 30000;
        getRequest.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(getRequest);
    }

    @Override
    public void myCallback(final int position) {
        L.l(TAG, "callback call");
        download_url = manualInfoModels.get(position).getFilePath() + "&token=" + MyApplication.mySharedPreference.getUserToken();
        String extension = CommonFunctions.getExtension(manualInfoModels.get(position).getFileType());
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
            if (L.isNetworkAvailable(getActivity())) {
                sendViewId(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_most_viewed&moodlewsrestformat=json", manualInfoModels.get(position).getId());
            } else {
                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                        .setContentText(getString(R.string.err_network_connection))
                        .setConfirmText("OK")
                        .show();
            }
//            startActivity(new Intent(getActivity(), ProfilePictureActivity.class)
//                    .putExtra("image_url", manualInfoModels.get(position).getFilePath() + "&token=" + MyApplication.mySharedPreference.getUserToken())
//                    .putExtra("title", manualInfoModels.get(position).getManualName())
//                    .putExtra("notProfilePhoto", true));
            replaceFrgament(new ViewDocumentFragmnet(),manualInfoModels.get(position).getFilePath() + "&token=" + MyApplication.mySharedPreference.getUserToken(),"image");

        } else if (extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("mpeg")
                || extension.equalsIgnoreCase("flv")
                || extension.equalsIgnoreCase("avi")
                || extension.equalsIgnoreCase("mov")
                || extension.equalsIgnoreCase("mpg")
                || extension.equalsIgnoreCase("wmv") || extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("swf")) {
            if (L.isNetworkAvailable(getActivity())) {
                if (L.isNetworkAvailable(getActivity())) {
                    sendViewId(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_most_viewed&moodlewsrestformat=json", manualInfoModels.get(position).getId());
                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }
//                Intent videoview_intent = new Intent(getActivity(), VideoViewActivity.class);
//                videoview_intent.putExtra(getString(R.string.testpaper_url_parcelable_tag), download_url);
//                startActivity(videoview_intent);
               replaceFrgament(new ViewDocumentFragmnet(),download_url,"video");
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
                if (L.isNetworkAvailable(getActivity())) {
                    sendViewId(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_most_viewed&moodlewsrestformat=json", manualInfoModels.get(position).getId());
                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }
//                HtmlViewFragment htmlViewFragment = HtmlViewFragment.newInstance(download_url);
//                mainActivity.replaceFrgament(htmlViewFragment);
                replaceFrgament(new ViewDocumentFragmnet(),download_url,"doc");
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
//            if (CommonFunctions.checkDocumentAllreaddyExist(manualInfoModels.get(position).getFilePath())) {
//                File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), manualInfoModels.get(position).getFilePath());
//                new CommonFunctions().openFile(getActivity(), file.getPath());
//            } else {
//                if (L.isNetworkAvailable(getActivity())) {
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage("To view " + manualInfoModels.get(position).getManualName() + " please download it.")
//                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (Utility.checkExternalStoragePermission(getActivity())) {
//                                        if (L.isNetworkAvailable(getActivity())) {
//                                            sendViewId(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_most_viewed&moodlewsrestformat=json", manualInfoModels.get(position).getId());
//                                        } else {
//                                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                                                    .setContentText(getString(R.string.err_network_connection))
//                                                    .setConfirmText("OK")
//                                                    .show();
//                                        }
//                                        L.l(TAG, "Document url " + download_url);
//                                        Uri uri = Uri.parse(download_url);
//                                        String filename = uri.getLastPathSegment();
//                                        L.l(TAG, "file to download " + filename);
//                                        //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
//                                        long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, manualInfoModels.get(position).fileType);
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
            if (Utility.checkExternalStoragePermission(getActivity())) {
                if (L.isNetworkAvailable(getActivity())) {
                    sendViewId(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_most_viewed&moodlewsrestformat=json", manualInfoModels.get(position).getId());
                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }
            }

//                startActivity(new Intent(getActivity(), ViewDocument.class)
//                        .putExtra("doc_url", manualInfoModels.get(position).getFilePath() + "&token=" + MyApplication.mySharedPreference.getUserToken())
//                        .putExtra("doc_name", manualInfoModels.get(position).getManualName()));
            replaceFrgament(new ViewDocumentFragmnet(),manualInfoModels.get(position).getFilePath() + "&token=" + MyApplication.mySharedPreference.getUserToken(),"doc");

            }
        }

    private void sendViewId(String url, final String id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // L.dismiss_pd();
                // Log.e("&*&**&*&&*&",response);
                //updateData(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //L.dismiss_pd();
                L.l(getActivity(), "Volley ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("moduleid", id);
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                L.l(getActivity(), "PARAMS Change MObile: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    public void replaceFrgament(Fragment fragment,String url,String type) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        Bundle args = new Bundle();
        args.putString("url",url);
        args.putString("type",type);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}
