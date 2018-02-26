package com.mahindra.be_lms.lib;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;


/**
 * Created by Pravin on 11/30/16.
 */

public class PKBDialog extends Dialog implements View.OnClickListener {
    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;
    private int mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mWarningFrame;
    private Button mConfirmButton, mCancelButton;
    private OnPKBDialogClickListner mCancelClickListener;
    private OnPKBDialogClickListner mConfirmClickListener;
    private String mTitleText;
    private TextView mTitleTextView, mContentTextView;
    private String mContentText;
    private String mCancelText;
    private String mConfirmText;
    private ImageView mCustomImage;
    private Drawable mCustomImgDrawable;


    public PKBDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public PKBDialog(Context context, int alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mAlertType = alertType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pkb_dialog);
        View mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
        mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
        mCustomImage = (ImageView) findViewById(R.id.custom_image);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);
    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        if (!fromCreate) {
            // restore all of views state before switching alert type
            restore();
        }
        switch (mAlertType) {
            case ERROR_TYPE:
                mErrorFrame.setVisibility(View.VISIBLE);
                break;
            case SUCCESS_TYPE:
                //warning_frame.setVisibility(View.VISIBLE);
                // initial rotate layout of success mask
                // mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                //  mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                mCancelButton.setVisibility(View.VISIBLE);
                break;
            case WARNING_TYPE:
                // mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                mWarningFrame.setVisibility(View.VISIBLE);
                break;
            case CUSTOM_IMAGE_TYPE:
                setCustomImage(mCustomImgDrawable);
                break;
            case PROGRESS_TYPE:
                break;
        }
    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);
        mConfirmButton.setBackgroundResource(R.drawable.blue_button_background);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(PKBDialog.this);
            } else {
                Log.d("mytag", "cancel btn else");
                PKBDialog.super.cancel();
                //dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(PKBDialog.this);
            } else {
                Log.d("mytag", "confirm btn else");
                PKBDialog.super.dismiss();
                // dismissWithAnimation();
            }
        }
    }

    public PKBDialog setCancelClickListener(OnPKBDialogClickListner listener) {
        mCancelClickListener = listener;
        return this;
    }

    public PKBDialog setConfirmClickListener(OnPKBDialogClickListner listener) {
        mConfirmClickListener = listener;
        return this;
    }

    public PKBDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public PKBDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getTitleText() {
        return mTitleText;
    }

    public PKBDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public String getContentText() {
        return mContentText;
    }

    public PKBDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public PKBDialog setCanclable(boolean canclable) {
        setCancelable(canclable);
        return this;
    }

    public PKBDialog showContentText(boolean isShow) {
        boolean mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public PKBDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public PKBDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public PKBDialog showCancelButton(boolean isShow) {
        boolean mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public interface OnPKBDialogClickListner {
        void onClick(PKBDialog customDialog);
    }


}
