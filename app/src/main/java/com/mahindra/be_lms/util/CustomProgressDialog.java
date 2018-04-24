package com.mahindra.be_lms.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;


public class CustomProgressDialog extends ProgressDialog {

	private Animation animRotate;

	private TextView tvProgress;
	private String message;
	private ImageView iv_loader;

	public CustomProgressDialog(Context context, String message) {
		super(context);
		this.message = message;
		animRotate = AnimationUtils.loadAnimation(context,
				R.anim.progress_dialog_anim);
		setIndeterminate(true);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_alert_view);
		iv_loader = (ImageView) findViewById(R.id.iv_loader);
		iv_loader.setAnimation(animRotate);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}
}
