package com.mahindra.be_lms.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MyValidator;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReplyQueryActivity extends BaseActivity {
    private static final int SELECT_FILE = 101;
    private static final int REQUEST_CAMERA = 102;
    @BindView(R.id.etReplyQuery)
    EditText editTextReply;
    @BindView(R.id.ivQueryReplyPicture)
    ImageView imageViewReplyAttachment;
    private String strQueryImg = "";
    private String queryID;

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_query_reply_layout);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.header_drawable));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (!intent.getStringExtra("queryId").isEmpty()) {
            queryID = intent.getStringExtra("queryId");
            L.l(this, "QueryID: " + queryID);
        }
    }

    @OnClick({R.id.ivQueryReplyPicture, R.id.btnQueryReplySumbit})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.ivQueryReplyPicture:
                selectImage();
                break;
            case R.id.btnQueryReplySumbit:
                if (MyValidator.isValidField(editTextReply, getString(R.string.err_query_reply))) {
                    try {
                        replyrequestCall(Constants.LMS_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery),
                getString(R.string.dialog_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), data.getData());
                strQueryImg = encodeToBase64(bm, Bitmap.CompressFormat.PNG, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewReplyAttachment.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        strQueryImg = encodeToBase64(thumbnail, Bitmap.CompressFormat.PNG, 100);
        imageViewReplyAttachment.setImageBitmap(thumbnail);
    }

    private void replyrequestCall(String url) {
        L.pd("Sending message", "Please wait", ReplyQueryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(ReplyQueryActivity.this.getLocalClassName(), "REPLY QUERY RESPONSE: " + response);
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
                L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY VOLLEY ERROR: " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(ReplyQueryActivity.this, PKBDialog.WARNING_TYPE)
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
                params.put("replay_text", L.getText(editTextReply));
                params.put("query_id", queryID);
                params.put("attachment", strQueryImg);
                L.l(ReplyQueryActivity.this.getLocalClassName(), "QUERY REPLY PARAMS: " + params.toString());
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
                                ReplyQueryActivity.this.finish();
                            }
                        }).show();
                QueryResponse queryResponse = new QueryResponse();
                queryResponse.setResposePerson("me");
                queryResponse.setQueryID(queryID);
                queryResponse.setMessage(L.getText(editTextReply));
                queryResponse.setTitle("Reply to query");
                queryResponse.setMsg_type("reply");
                if (!TextUtils.isEmpty(strQueryImg) && !strQueryImg.equalsIgnoreCase("null")) {
                    L.l(this, "attachment present");
                    queryResponse.setQueryReplyAttachment(strQueryImg);
                } else {
                    L.l(this, "attachment absent");
                    queryResponse.setQueryReplyAttachment("");
                }
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

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        startActivity(new Intent(ReplyQueryActivity.this, MainActivity.class));
        ReplyQueryActivity.this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
