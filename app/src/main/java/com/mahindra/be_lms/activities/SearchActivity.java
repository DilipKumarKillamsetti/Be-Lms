package com.mahindra.be_lms.activities;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.SearchAdapter;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.model.SearchModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class SearchActivity extends BaseActivity implements MyCallback,NetworkMethod {
    @BindView(R.id.spSearchModel)
    Spinner spSearchModel;
    @BindView(R.id.etSearchText)
    EditText etSearchText;
    @BindView(R.id.rrSearchList)
    RecyclerView rrSearchList;
    @BindView(R.id.activity_search)
    RelativeLayout activity_search;

    private String download_url = "";
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private List<String> modelList = new ArrayList<>();
    private List<SearchModel> searchModelList;
    private Document document;
    private  SearchAdapter adapter;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    private  String searchTxt;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        String[] model = new String[]{getResources().getString(R.string.nav_most_viewed), getResources().getString(R.string.nav_learning_quiz), getResources().getString(R.string.nav_powerol_passport),
                getResources().getString(R.string.nav_training_passport), getResources().getString(R.string.nav_manual_and_bulletins), getResources().getString(R.string.nav_training_calendar),
                getResources().getString(R.string.nav_survey_feedback), getResources().getString(R.string.nav_technical_uploads), getResources().getString(R.string.nav_my_field_records)};

        init();
    }

    private void init() {
        Intent intent = getIntent();
        searchTxt = intent.getStringExtra("searchTxt");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        callApi();
        searchModelList = new ArrayList<>();
        rrSearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //rrSearchList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        adapter = new SearchAdapter(this, searchModelList);
        adapter.setMyCallback(this);
        rrSearchList.setAdapter(adapter);



    }

    private  void callApi(){
        if (L.isNetworkAvailable(this)) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rrSearchList.setVisibility(View.VISIBLE);
            }
            L.pd(getString(R.string.dialog_please_wait), this);
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=core_course_search_courses&criterianame=search&criteriavalue="+searchTxt+"&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rrSearchList.setVisibility(View.GONE);
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

    @OnTouch({R.id.activity_search})
    public boolean onTouchSearch(View view) {
        if (view.getId() == R.id.activity_search) {
            new CommonFunctions().hideSoftKeyboard(SearchActivity.this);
        }
        return false;
    }

    @OnClick(R.id.btnSearchSubmit)
    public void searchSubmitOnClick() {
        if (validateField()) {
         /*   new CommonFunctions().hideSoftKeyboard(SearchActivity.this);
            L.l("SearchTXT: " + L.getText(etSearchText) + " ModelSpinner : " + modelList.get(spSearchModel.getSelectedItemPosition()).getModelID() + " " + modelList.get(spSearchModel.getSelectedItemPosition()).getModelName());
            String modeltxt = spSearchModel.getSelectedItemPosition() == 0 ? "" : modelList.get(spSearchModel.getSelectedItemPosition()).getModelID();
            if (spSearchModel.getSelectedItemPosition() == 0) {
                L.l("Search not selected model");
                searchModelList = new DBHelper().getSearchList(L.getText(etSearchText), "", "");
            } else {
                Model model = modelList.get(spSearchModel.getSelectedItemPosition());
                searchModelList = new DBHelper().getSearchList(L.getText(etSearchText), model.getProductID(), model.getModelID());
            }


            if (searchModelList.size() > 0) {
                rrSearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                rrSearchList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
                SearchAdapter adapter = new SearchAdapter(this, searchModelList);
                adapter.setMyCallback(this);
                rrSearchList.setAdapter(adapter);
            } else {
                L.t("No Record Found");
            }*/
        }
    }

    /*
    Validation method of registration fields
     */
    private boolean validateField() {
        boolean flag = true;
        if (!MyValidator.isValidField(etSearchText, getString(R.string.err_enter_search_value))) {
            flag = false;
        }
        return flag;
    }

    @Override
    public void myCallback(int position) {
        SearchModel searchModel = searchModelList.get(position);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void request(String url) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("Status").equalsIgnoreCase("Success")){
                                JSONArray coursesArray = response.getJSONArray("courses");
                                for(int i = 0 ; i<coursesArray.length();i++){
                                    SearchModel searchModel = new Gson().fromJson(String.valueOf(coursesArray.get(i)), SearchModel.class);
                                    searchModelList.add(searchModel);
                                }
                                if (searchModelList.size() > 0) {
                                    tvNoRecordFound.setVisibility(View.GONE);
                                    rrSearchList.setVisibility(View.VISIBLE);
                                    adapter.setCourseList(searchModelList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    tvNoRecordFound.setVisibility(View.VISIBLE);
                                    rrSearchList.setVisibility(View.GONE);
                                }

                            }else{
                                tvNoRecordFound.setVisibility(View.VISIBLE);
                                rrSearchList.setVisibility(View.GONE);
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
                L.l(SearchActivity.this, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(SearchActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);

    }
}
