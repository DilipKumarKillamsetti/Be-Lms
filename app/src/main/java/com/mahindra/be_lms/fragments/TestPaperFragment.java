package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.TestPaperAdapter;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.model.Course;
import com.mahindra.be_lms.model.TestPaper;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.Utility;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Updated By Chaitali Chavan on 15/11/2016.
 */
public class TestPaperFragment extends Fragment implements MyCallback, NetworkMethod {
    public static final String TAG = TestPaperFragment.class.getSimpleName();
    @BindView(R.id.rvTestPaper)
    RecyclerView rvTestPaper;
    List<TestPaper> testPaperList;
    String filename;
    String filePath;
    int status;
    private MainActivity mainActivity;
    private DownloadManager downloadManager;
    private Course course;

    public TestPaperFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_paper, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            course = bundle.getParcelable(getString(R.string.course_parcelable_tag));
            assert course != null;
            L.l("Course: " + course.toString());
            testPaperList = new ArrayList<>();
            downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            //  testPaperList = DataProvider.getDummyTestPaperList();
            rvTestPaper.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvTestPaper.setHasFixedSize(true);
            rvTestPaper.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            try {
                if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
                    request(Constants.LMS_URL);
                } else {
                    L.t_long(getString(R.string.err_network_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* if (!testPaperList.isEmpty()){
                adapter.setMyCallback(this);
            }*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle(R.string.frg_testpaper_title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void myCallback(int position) {
        final TestPaper testPaper = testPaperList.get(position);
        //type = ""+testPaper.getTestpaper_type();
        String type = CommonFunctions.getExtension(testPaper.getTestpaper_title());
        L.l(TAG, "File Extension : " + type);
        // final String url = "http:/192.168.1.19/powerol_lms/webservice/pluginfile.php/15061/mod_resource/content/1/125_AngularJS_and_Ionic.pdf?forcedownload=1&token=b5a3ccd8331a2ed6e9def6b84e8a52ef";
        final String url = "http://192.168.1.19/powerol_lms/webservice/pluginfile.php/15061/mod_resource/content/1/" + testPaper.getTestpaper_title() + "?forcedownload=1&token=b5a3ccd8331a2ed6e9def6b84e8a52ef";
        //Image download url
        //http://192.168.1.19/powerol_lms/webservice/pluginfile.php/11643/mod_page/content/16/FIELD%20FLASHING.jpg?token=b5a3ccd8331a2ed6e9def6b84e8a52ef
        L.l(TAG, "Download URL: " + url);
        if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
            if (type.equalsIgnoreCase(".3gp") || type.equalsIgnoreCase(".mpg") || type.equalsIgnoreCase(".mpeg") || type.equalsIgnoreCase(".mpe") || type.equalsIgnoreCase(".mp4") || type.equalsIgnoreCase(".avi")) {
                Intent videoview_intent = new Intent(getActivity(), VideoViewActivity.class);
                videoview_intent.putExtra(getString(R.string.testpaper_url_parcelable_tag), url);
                startActivity(videoview_intent);
            } else if (type.equals(getString(R.string.html_type))) {
                //  bundle.putString("testpaper_url",""+url);
                Fragment htmlViewfrgamnet = new HtmlViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.testpaper_url_parcelable_tag), "" + url);
                htmlViewfrgamnet.setArguments(bundle);
                mainActivity.replaceFrgament(htmlViewfrgamnet);
            } else {
                if (testPaper.getReferenceId() != 0L) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(testPaper.getReferenceId());
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                            L.l("Receiver filename: " + filePath);
                            String filenameArray[] = filename.split("\\.");
                            String extension = filenameArray[filenameArray.length - 1];
                            L.l("Receiver filename extension: " + extension);
                        }
                    }
                    c.close();
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        new CommonFunctions().openFile(getActivity(), filePath);

                    } else {
                        new CommonFunctions().CheckStatus(downloadManager, testPaper.getReferenceId());
                    }

                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("To view " + testPaper.getTestpaper_title() + " please download it.")
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (Utility.checkExternalStoragePermission(getActivity())) {
                                        L.l("Test paper url " + url);
                                        Uri uri = Uri.parse(url);
                                        String filename = uri.getLastPathSegment();
                                        L.l("file to download " + filename);
                                        L.l("Reference ID: " + testPaper.getReferenceId());
                                        long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, filename);
                                        L.l("Reference ID: " + referencedID);
                                        testPaper.setReferenceId(referencedID);
                                    } else {
                                        Utility.callExternalStoragePermission(getActivity());
                                    }

                                }
                            }).setNegativeButton(getString(R.string.dialog_cancel), null)
                            .show();
                }
            }
        } else {
            L.t_long(getString(R.string.err_network_connection));
        }
    }

    @Override
    public void request(String url) {
        L.pd("Fetching Documents", "Please wait", getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "GET TEST DOC RESPONSE: " + response);
                try {
                    updateDisplay(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "VOLLEY ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", Constants.ACTION_GET_QUIZ_DOC);
                params.put("courseid", course.getId());
                params.put("type", "doc");
                L.l(TAG, "GET TEST DOC PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
          /*  {
                "courseid": "352",
                    "message": "Success",
                    "documents": [
                {
                    "docid": "130",
                        "name": "Skill Test0",
                        "linkurl": "mod/resource/view.php?id=130",
                        "filename": "SkillTest.jpg",
                        "filesystempath": "da/61/da61e9468b4d0d0bb8e8156236105e55086d4df4"
                }
                ]
            }*/
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("message").equalsIgnoreCase("Success")) {
                L.l(TAG, "Course ID: " + jsonObject.getString("courseid"));
                JSONArray document_jsonArray = jsonObject.getJSONArray("documents");
                L.l(TAG, "DOCUMENT JSONARRAY: " + document_jsonArray.toString());
                testPaperList = new ArrayList<>();
                for (int i = 0; i < document_jsonArray.length(); i++) {
                    TestPaper testPaper = new TestPaper();
                    testPaper.setId(document_jsonArray.getJSONObject(i).getString("docid"));
                    testPaper.setTestpaper_title(document_jsonArray.getJSONObject(i).getString("filename"));
                    testPaperList.add(testPaper);
                }
                TestPaperAdapter adapter = new TestPaperAdapter(getActivity(), testPaperList);
                rvTestPaper.setAdapter(adapter);
                adapter.setMyCallback(this);
            } else {
                L.t_long(jsonObject.getString("message"));
            }
        }
    }

}
