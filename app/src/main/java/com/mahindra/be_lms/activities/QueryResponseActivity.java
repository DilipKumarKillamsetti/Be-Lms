package com.mahindra.be_lms.activities;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.QueryResponseAdapter;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.fragments.HtmlViewFragment;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
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
import butterknife.OnClick;

public class QueryResponseActivity extends BaseActivity implements Callback, View.OnClickListener, NetworkMethod {
    //    @BindView(R.id.rvqueryResponse)
//    RecyclerView rvqueryResponse;
    @BindView(R.id.tvQueryResponseNotFound)
    TextView tvQueryResponseNotFound;
    @BindView(R.id.tvQueryTitle)
    TextView tvQueryTitle;
    @BindView(R.id.wvHtmlViewFragment)
    WebView wvHtmlViewFragment;
    @BindView(R.id.messageText)
    EditText messageText;
    @BindView(R.id.sendButton)
    ImageView sendButton;
    private EditText etReplyQuery;
    private String queryID;
    private List<QueryResponse> queryResponseList;// = new ArrayList<>();
    private QueryResponseAdapter queryResponseAdapter;
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private String download_url;
    private String queryStatus;
    private long documentRefID;
    private String queryChatHistory;
    private String querySolvedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_response);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        sendButton.setOnClickListener(QueryResponseActivity.this);
        if (!intent.getStringExtra("queryId").isEmpty()) {
            queryID = intent.getStringExtra("queryId");
            L.l(this, "QueryID: " + queryID);
            queryStatus = intent.getStringExtra("queryStatus");
            if (queryStatus.equalsIgnoreCase("Completed")) {
                querySolvedStatus = "1";
            } else {
                querySolvedStatus = "0";
            }
            L.l(this, "QueryStatus: " + queryStatus);
            String querySubject = intent.getStringExtra("queryTitle");
            L.l(this, "QueryTitle: " + querySubject);
            tvQueryTitle.setText("Subject: " + querySubject);
            queryResponseList = new ArrayList<>();
            queryChatHistory = intent.getStringExtra("queryChatView");


            if (null == queryChatHistory) {
                tvQueryResponseNotFound.setVisibility(View.VISIBLE);
                wvHtmlViewFragment.setVisibility(View.GONE);
            } else {
                tvQueryResponseNotFound.setVisibility(View.GONE);
                wvHtmlViewFragment.setVisibility(View.VISIBLE);
                displayWebview(queryChatHistory);

            }
            if (null != queryChatHistory) {
                reload();
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupList(List<QueryResponse> queryResponseList) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        L.l("FUll Width: " + width);
        int applyWidth = (width - ((width % 100) * 10));
        L.l("substract Width: 10% " + applyWidth);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.query_menu, menu);
        MenuItem item_close_query = menu.findItem(R.id.action_closeQuery);
        MenuItem item_reply_query = menu.findItem(R.id.action_ReplyQuery);
        item_close_query.setVisible(false);
        //if (queryStatus) {
        item_reply_query.setVisible(false);
        //}
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {

//                if (null == queryChatHistory) {
//                    tvQueryResponseNotFound.setVisibility(View.VISIBLE);
//                    wvHtmlViewFragment.setVisibility(View.GONE);
//                } else {
//                    tvQueryResponseNotFound.setVisibility(View.GONE);
//                    wvHtmlViewFragment.setVisibility(View.VISIBLE);
//                    displayWebview(queryChatHistory);
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_closeQuery:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.dialog_msg_query_reply))
                        .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                try {
                                    request(Constants.LMS_URL);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(getString(R.string.dialog_no), null).show();
                break;*/
            case R.id.action_ReplyQuery:
                //showQueryReplyDialog();
                startActivity(new Intent(QueryResponseActivity.this, ReplyQueryActivity.class).putExtra("queryId", queryID));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
 Show AlertDialog for Query Reply
  */
    private void showQueryReplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_query_reply_layout, null);
        builder.setView(dialogView)
                .setTitle(getString(R.string.dialog_query_response_title))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .setPositiveButton(getString(R.string.dialog_submit), null);
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btn_positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setTransformationMethod(null);
                Button btn_negative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setTransformationMethod(null);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etReplyQuery = (EditText) dialogView.findViewById(R.id.etReplyQuery);
                        if (MyValidator.isValidField(etReplyQuery, getString(R.string.err_query_reply))) {
                            //start activity OTP
                            // L.t(getString(R.string.validation_complete));
                            d.dismiss();
                            try {
                                replyrequestCall(Constants.LMS_URL);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        d.show();
    }

    private void replyrequestCall(String url) {
        L.pd("Sending message", "Please wait", QueryResponseActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(QueryResponseActivity.this.getLocalClassName(), "REPLY QUERY RESPONSE: " + response);
                try {
                    queryReplyResponse(response);
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(QueryResponseActivity.this.getLocalClassName(), "QUERY REPLY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(QueryResponseActivity.this, PKBDialog.WARNING_TYPE)
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
                params.put("action", "reply_on_queries");
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("replay_text", L.getText(etReplyQuery));
                params.put("query_id", queryID);
                L.l(QueryResponseActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void queryReplyResponse(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                //L.t(jsonObject.getString("message"));
                new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setCustomImage(R.drawable.success_circle)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                            }
                        }).show();
                QueryResponse queryResponse = new QueryResponse();
                queryResponse.setResposePerson("me");
                queryResponse.setQueryID(queryID);
                queryResponse.setMessage(L.getText(etReplyQuery));
                queryResponse.setTitle("Reply to query");
                queryResponse.setMsg_type("reply");
                new DBHelper().saveResponse(queryResponse);
                queryResponseList = new ArrayList<>();
                queryResponseList = new DBHelper().getQueryResponseList(queryID);
                queryResponseAdapter.setQueryResponseList(queryResponseList);
                queryResponseAdapter.notifyDataSetChanged();
                tvQueryResponseNotFound.setVisibility(View.GONE);
            } else {
                // L.t(jsonObject.getString("message"));
                new PKBDialog(this, PKBDialog.WARNING_TYPE)
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

//    @OnClick(R.id.footertext)
//    public void OnfooterTextClick() {
//        startActivity(new Intent(QueryResponseActivity.this, MainActivity.class));
//        QueryResponseActivity.this.finish();
//    }

    @Override
    public void myCallback(int position) {
        QueryResponse queryResponse = queryResponseList.get(position);
        /*try {
            L.l(this, "Selected Doc: " + queryResponse.getQueryResponseExtraLink());
            //{"141":{"LMS initial registration":"1305160840540R.pdf"}}
            JSONObject jsonObject = new JSONObject(queryResponse.getQueryResponseExtraLink().toString());
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String docTreeID = iter.next();
                L.l("Document Tree ID: " + docTreeID);
                try {
                    Object dockeyValue = jsonObject.get(docTreeID);
                    L.l("Document keyValue: " + dockeyValue);
                    JSONObject jsonObject1 = new JSONObject(dockeyValue.toString());
                    Iterator<String> iterator = jsonObject1.keys();
                    while (iterator.hasNext()) {
                        final String docTitle = iterator.next();
                        L.l("Doc Title: " + docTitle);
                        try {
                            final Object docName = jsonObject1.get(docTitle);
                            L.l("Document Name: " + docName.toString());
                            String extension = CommonFunctions.getExtension(docName.toString());
                            L.l("File Extension : " + extension);
                            List<Document> documentList = new ArrayList<>();
                            documentList = new DBHelper().getDocumentList(docTreeID);
                            L.l("Doc List : " + documentList.toString());
                            download_url = Constants.DOC_TREE_DOWNLOAD_URL;
                            download_url = download_url + docName;
                            for (final Document document : documentList) {
                                L.l("For Doc name : " + document.getDocumentName().toString());
                                if (document.getDocumentName().compareTo(dockeyValue.toString()) == 0) {
                                    if (CommonFunctions.checkDocumentAllreaddyExist(docTitle)) {
                                        L.l("Doc Present");
                                        File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), docTitle);
                                        new CommonFunctions().openFile(this, file.getPath());
                                    } else {
                                        if (document.getDocumentReferencedID() != 0L) {
                                            DownloadManager.Query query = new DownloadManager.Query();
                                            query.setFilterById(documentList.get(position).getDocumentReferencedID());
                                            Cursor c = downloadManager.query(query);
                                            if (c.moveToFirst()) {
                                                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                                    filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                                    L.l("Receiver filename: " + filePath);
                                                    String filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                                                    L.l("Receiver filename : " + filename);
                                                }
                                            }
                                            c.close();
                                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                                new CommonFunctions().openFile(QueryResponseActivity.this, filePath);
                                            } else {
                                                new CommonFunctions().CheckStatus(downloadManager, documentList.get(position).getDocumentReferencedID());
                                            }
                                        } else {
                                            new AlertDialog.Builder(QueryResponseActivity.this)
                                                    .setMessage("To view " + docTitle + " please download it.")
                                                    .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            if (L.isNetworkAvailable(QueryResponseActivity.this)) {
                                                                if (Utility.checkExternalStoragePermission(QueryResponseActivity.this)) {
                                                                    download_url = Constants.DOC_TREE_DOWNLOAD_URL + docName.toString();
                                                                    L.l(QueryResponseActivity.this, "Document url " + download_url);
                                                                    Uri uri = Uri.parse(download_url);
                                                                    //String filename = uri.getLastPathSegment();
                                                                    String filename = docTitle;
                                                                    L.l(QueryResponseActivity.this, "file to download " + filename);
                                                                    //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
                                                                    long referencedID = new CommonFunctions().DownloadData(QueryResponseActivity.this, downloadManager, uri, filename);
                                                                    L.l(QueryResponseActivity.this, "Reference ID: " + referencedID);
                                                                    document.setDocumentReferencedID(referencedID);
                                                                } else {
                                                                    Utility.callExternalStoragePermission(QueryResponseActivity.this);
                                                                }
                                                            } else {
                                                                new PKBDialog(QueryResponseActivity.this, PKBDialog.WARNING_TYPE)
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
                                                    }).setNegativeButton(getString(R.string.dialog_cancel), null)
                                                    .show();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        try {
            L.l(this, "Selected Doc: " + queryResponse.getQueryResponseExtraLink());
            //{"141":{"LMS initial registration":"1305160840540R.pdf"}}
            JSONObject jsonObject = new JSONObject(queryResponse.getQueryResponseExtraLink().toString());
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String docTreeID = iter.next();
                L.l("Document Tree ID: " + docTreeID);
                try {
                    Object dockeyValue = jsonObject.get(docTreeID);
                    L.l("Document keyValue: " + dockeyValue);
                    JSONObject jsonObject1 = new JSONObject(dockeyValue.toString());
                    Iterator<String> iterator = jsonObject1.keys();
                    while (iterator.hasNext()) {
                        final String docTitle = iterator.next();
                        L.l("Doc Title: " + docTitle);
                        try {
                            final Object docName = jsonObject1.get(docTitle);
                            L.l("Document Name: " + docName.toString());
                            String extension = CommonFunctions.getExtension(docName.toString());
                            L.l("File Extension : " + extension);
                            download_url = Constants.DOC_TREE_DOWNLOAD_URL;
                            download_url = download_url + docName;
                            if (CommonFunctions.checkDocumentAllreaddyExist(docName.toString())) {
                                L.l("Doc Present");
                                File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), docName.toString());
                                new CommonFunctions().openFile(this, file.getPath());
                            } else {
                                if (documentRefID != 0L) {
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(documentRefID);
                                    Cursor c = downloadManager.query(query);
                                    if (c.moveToFirst()) {
                                        status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                            filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                            L.l("Receiver filename: " + filePath);
                                            String filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                                            L.l("Receiver filename : " + filename);
                                        }
                                    }
                                    c.close();
                                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                        new CommonFunctions().openFile(QueryResponseActivity.this, filePath);
                                    } else {
                                        new CommonFunctions().CheckStatus(downloadManager, documentRefID);
                                    }
                                } else {
                                    new AlertDialog.Builder(QueryResponseActivity.this)
                                            .setMessage("To view " + docTitle + " please download it.")
                                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    if (L.isNetworkAvailable(QueryResponseActivity.this)) {
                                                        if (Utility.checkExternalStoragePermission(QueryResponseActivity.this)) {
                                                            download_url = Constants.DOC_TREE_DOWNLOAD_URL + docName.toString();
                                                            L.l(QueryResponseActivity.this, "Document url " + download_url);
                                                            Uri uri = Uri.parse(download_url);
                                                            //String filename = uri.getLastPathSegment();
                                                            String filename = docName.toString();
                                                            L.l(QueryResponseActivity.this, "file to download " + filename);
                                                            //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
                                                            long referencedID = new CommonFunctions().DownloadData(QueryResponseActivity.this, downloadManager, uri, filename);
                                                            L.l(QueryResponseActivity.this, "Reference ID: " + referencedID);
                                                            documentRefID = referencedID;
                                                        } else {
                                                            Utility.callExternalStoragePermission(QueryResponseActivity.this);
                                                        }
                                                    } else {
                                                        new PKBDialog(QueryResponseActivity.this, PKBDialog.WARNING_TYPE)
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
                                            }).setNegativeButton(getString(R.string.dialog_cancel), null)
                                            .show();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void myCallback(int position, String tag) {
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    private void displayWebview(String html_url) {
        wvHtmlViewFragment.loadUrl(html_url);
        //wvHtmlViewFragment.setWebViewClient(new WebViewClient());
        wvHtmlViewFragment.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        wvHtmlViewFragment.getSettings().setAppCachePath(MyApplication.getAppContext().getCacheDir().getAbsolutePath());
        wvHtmlViewFragment.getSettings().setAllowFileAccess(true);
        wvHtmlViewFragment.getSettings().setAppCacheEnabled(true);
        wvHtmlViewFragment.getSettings().setJavaScriptEnabled(true);
        //wvHtmlViewFragment.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvHtmlViewFragment.getSettings().setBuiltInZoomControls(false);
        wvHtmlViewFragment.getSettings().setSupportZoom(false);
        wvHtmlViewFragment.getSettings().setDisplayZoomControls(false);
        wvHtmlViewFragment.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        wvHtmlViewFragment.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wvHtmlViewFragment.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //int topHeight = wvHtmlViewFragment.getContentHeight();

//        wvChat.loadData(chat_, "text/html; charset=utf-8", "UTF-8");
//        wvHtmlViewFragment.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                //use the param "view", and call getContentHeight in scrollTo
//                view.scrollTo(0, view.getMeasuredHeight());
//            }
//        });
        // wvHtmlViewFragment.setWebViewClient(new HtmlViewFragment.MyWebViewClient());
    }

    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reload();
                wvHtmlViewFragment.removeCallbacks(this);
                displayWebview(queryChatHistory);
            }
        }, 10000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sendButton:
                if (!messageText.getText().toString().isEmpty()) {
                    CommonFunctions.hideSoftKeyboard(QueryResponseActivity.this);
                    if (L.isNetworkAvailable(this)) {
                        request(Constants.BE_LMS_ROOT_URL + MyApplication.mySharedPreference.getUserToken() + "&wsfunction=custommobwebsrvices_chat&moodlewsrestformat=json");
                    } else {
                        new PKBDialog(this, PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.err_network_connection))
                                .setConfirmText("OK")
                                .show();
                    }
                } else {
                    Toast.makeText(QueryResponseActivity.this, "Please enter message.", Toast.LENGTH_SHORT).show();
                }


                break;
            default:
                break;

        }
    }

    @Override
    public void request(String url) {
        //L.pd("Please wait", this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // L.dismiss_pd();
                updateData(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //L.dismiss_pd();
                L.l(QueryResponseActivity.this, "Volley ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("queryid", queryID);
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("message", messageText.getText().toString());
                params.put("issolved", querySolvedStatus);

                L.l(QueryResponseActivity.this, "PARAMS Change MObile: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);

    }

    private void updateData(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);
            if (jsonArray.length() != 0) {

                JSONObject queryResult = (JSONObject) jsonArray.get(0);
                if (queryResult.getString("result").equalsIgnoreCase("passed")) {
                    wvHtmlViewFragment.setVisibility(View.VISIBLE);
                    messageText.setText("");
                    tvQueryResponseNotFound.setVisibility(View.GONE);
                    queryChatHistory = queryResult.getString("chathistory");
                    // reload();
                    displayWebview(queryChatHistory);

                } else {
                    Toast.makeText(QueryResponseActivity.this, "Message not sent.Please try again.", Toast.LENGTH_SHORT).show();
                    tvQueryResponseNotFound.setVisibility(View.VISIBLE);
                }

            } else {
                tvQueryResponseNotFound.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

