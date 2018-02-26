package com.mahindra.be_lms.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.model.Queries;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyQueryDetailActivity extends BaseActivity implements NetworkMethod {
    @BindView(R.id.tvMyQueryDetailActivitySubject)
    TextView tvMyQueryDetailActivitySubject;
    @BindView(R.id.tvMyQueryDetailActivityStatus)
    TextView tvMyQueryDetailActivityStatus;
    @BindView(R.id.tvMyQueryDetailActivityResponsiblePerson)
    TextView tvMyQueryDetailActivityResponsiblePerson;
    @BindView(R.id.tvMyQueryDetailActivityBody)
    TextView tvMyQueryDetailActivityBody;
    @BindView(R.id.ivMyQueryDetailActivityStatus)
    ImageView ivMyQueryDetailActivityStatus;
    @BindView(R.id.tv_responseTitle)
    TextView tv_responseTitle;
    @BindView(R.id.tvAttachment)
    TextView tvAttachment;
    @BindView(R.id.ivQueryDetailsAttachment)
    ImageView ivQueryDetailsAttachment;
    private Queries queries;
    private EditText etReplyQuery;
    private Bitmap bitmap;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_query_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        L.l(this, "init call");
        Intent intent = getIntent();
        queries = intent.getParcelableExtra("test");

        try {
            getSupportActionBar().show();
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.header_drawable));
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
       /* queryResponseList = new ArrayList<>();
        queryResponseList = new DBHelper().getQueryResponseList(queries.getQueryInsertedID());*/
        tvMyQueryDetailActivitySubject.setText(queries.getQuery_subject());
        tvMyQueryDetailActivityBody.setText(queries.getQuery_body());
        if (!queries.getFilepath().isEmpty()) {
//            bitmap = getBitmapFromURL(queries.getFilepath());
//            if (bitmap != null) {
//                ivQueryDetailsAttachment.setImageBitmap(bitmap);
//            }
            Picasso.with(MyQueryDetailActivity.this) //Context
                    .load(queries.getFilepath()) //URL/FILE
                    .into(ivQueryDetailsAttachment);
        } else {
            tvAttachment.setText("No attachment");
            ivQueryDetailsAttachment.setVisibility(View.GONE);
        }
        setQueryStatus();
       /* if (!queryResponseList.isEmpty()){
            tvMyQueryDetailActivityResponsiblePerson.setText(""+queryResponseList.get(0).getResposePerson());
            tv_responseTitle.setText(""+queryResponseList.get(0).getMessage());
        }else {
            tvMyQueryDetailActivityResponsiblePerson.setText(R.string.str_not_available);
        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setQueryStatus() {
        int statusColor;
        Drawable statusImage;
        if (queries.getStatus().equalsIgnoreCase("Completed")) {
            statusColor = getResources().getColor(R.color.green);
            statusImage = getResources().getDrawable(R.drawable.circle_green);
            tvMyQueryDetailActivityStatus.setText("Completed");
        } else {
            statusColor = getResources().getColor(R.color.textColorPrimary);
            statusImage = getResources().getDrawable(R.drawable.circle_red);
            tvMyQueryDetailActivityStatus.setText("Not Completed");
        }
        tvMyQueryDetailActivityStatus.setTextColor(statusColor);
        ivMyQueryDetailActivityStatus.setImageDrawable(statusImage);
    }

    @OnClick(R.id.tvQueryResponseView)
    public void onViewClick() {
        L.l("View Response click");
        Intent queryResponseIntent = new Intent(this, QueryResponseActivity.class);
        queryResponseIntent.putExtra("queryId", queries.getId());
        queryResponseIntent.putExtra("queryStatus", queries.getStatus());
        queryResponseIntent.putExtra("queryTitle", queries.getQuery_subject());
        queryResponseIntent.putExtra("queryChatView", queries.getChathistory());
        startActivity(queryResponseIntent);
        MyQueryDetailActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.query_menu, menu);
        MenuItem item_close_query = menu.findItem(R.id.action_closeQuery);
        MenuItem item_reply_query = menu.findItem(R.id.action_ReplyQuery);
        item_reply_query.setVisible(false);
        item_close_query.setVisible(false);
//        if (queries.getQueryStatus()) {
//            item_close_query.setVisible(false);
//        } else {
//            //item_close_query.setVisible(true);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_closeQuery:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.dialog_msg_query_reply))
                        .setCancelable(false)
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
                break;
            case R.id.action_ReplyQuery:
                showQueryReplyDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void request(String url) {
        L.pd("Closing Query", "Please wait", MyQueryDetailActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "CLOSE QUERY RESPONSE: " + response);
                try {
                    closeQueryResponse(response);
                } catch (JSONException e) {
                    L.dismiss_pd();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "CLOSE QUERY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(MyQueryDetailActivity.this, PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                /*action = close_queries
                userid = 14528
                query_id = 1*/
                params.put("action", "close_queries");
                params.put("userid", MyApplication.mySharedPreference.getUserId());
                params.put("query_id", queries.getId());
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "QUERY CLOSE PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void closeQueryResponse(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                // L.t(jsonObject.getString("message"));
                queries.setStatus("Completed");
                setQueryStatus();
                //new DBHelper().updateQuery(queries);
                new PKBDialog(this, PKBDialog.CUSTOM_IMAGE_TYPE)
                        .setContentText(jsonObject.getString("message"))
                        .setCustomImage(R.drawable.success_circle)
                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                            @Override
                            public void onClick(PKBDialog customDialog) {
                                customDialog.dismiss();
                                MyQueryDetailActivity.this.finish();
                            }
                        }).show();
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

    private void replyrequestCall(String url) {
        L.pd("Sending message", "Please wait", MyQueryDetailActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "REPLY QUERY RESPONSE: " + response);
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
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "QUERY REPLY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(MyQueryDetailActivity.this, PKBDialog.WARNING_TYPE)
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
                params.put("query_id", queries.getId());
                L.l(MyQueryDetailActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
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
                queryResponse.setQueryID(queries.getId());
                queryResponse.setMessage(L.getText(etReplyQuery));
                queryResponse.setTitle("Reply to query");
                queryResponse.setMsg_type("reply");
                new DBHelper().saveResponse(queryResponse);
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

    @OnClick(R.id.ivQueryDetailsAttachment)
    public void ivQueryDetailsAttachmentClick() {
//        startActivity(new Intent(this, ProfilePictureActivity.class)
//                .putExtra("image_url", queries.getFilepath())
//                .putExtra("title", queries.getQuery_subject())
//                .putExtra("isBitmap", true));
        // showImageDialog();
    }

    private void showImageDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_display_image_layout, null);
        TextView tvCloseDialog = (TextView) dialogView.findViewById(R.id.tvCloseDialog);
        TextView tvUploadImageTitle = (TextView) dialogView.findViewById(R.id.tvUploadedImageTitle);
        ImageView ivUploadedImage = (ImageView) dialogView.findViewById(R.id.ivUploadedImage);
        tvUploadImageTitle.setText(queries.getQuery_subject());
        ivUploadedImage.setImageBitmap(bitmap);

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

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        MyQueryDetailActivity.this.finish();
        startActivity(new Intent(MyQueryDetailActivity.this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


}
