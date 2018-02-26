package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationAttachmentActivity extends BaseActivity {
    private static final String TAG = "NotificationAttachmentActivity";
    @BindView(R.id.ivNotificationAttachment)
    ImageView imageView;
    @BindView(R.id.tvNotificationAttachmentImageTitle)
    TextView tvImageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_attachment);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            String attachmentLink = intent.getStringExtra("attachment");
            L.l(TAG, "Attachment Link: " + Constants.LMS_Common_URL + attachmentLink);
            String finalLink = Constants.LMS_Common_URL + attachmentLink;
            String filename = finalLink.substring(finalLink.lastIndexOf("/") + 1);
            L.l(TAG, "filename: " + filename);
            tvImageTitle.setText(filename);
            Picasso.with(this).load(Constants.LMS_Common_URL + attachmentLink).placeholder(R.drawable.powerol_logo).into(imageView);
        }
    }

    @OnClick(R.id.tvNotificationAttachmentClose)
    public void btnCloseClick() {
        NotificationAttachmentActivity.this.finish();
    }
}
