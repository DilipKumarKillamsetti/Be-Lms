package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.mahindra.be_lms.R;

public class FeedbackActivity extends BaseActivity {
    EditText mEditTextComment;
    Button mButtonSubmit;
    String mStringComment, mStringRating = "0.0";
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
        addListenerOnRatingBar();
        setTitle("Feedback");
    }

    public void init() {
        mEditTextComment = (EditText) findViewById(R.id.xedt_feedback_comment);
        mButtonSubmit = (Button) findViewById(R.id.xbtn_login);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rate = Float.parseFloat(mStringRating);
                if (rate <= 0.0) {
                    Toast.makeText(getApplicationContext(), "Please rate first", Toast.LENGTH_SHORT).show();
                } else if (mEditTextComment.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter comment", Toast.LENGTH_SHORT).show();
                } else {
                    mStringComment = mEditTextComment.getText().toString();
                    Toast.makeText(getApplicationContext(), "Feedback submitted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FeedbackActivity.this, MainActivity.class));
                }
            }
        });
    }

    public void addListenerOnRatingBar() {
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                mStringRating = String.valueOf(rating);
                //Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_LONG).show();
            }
        });
    }
}
