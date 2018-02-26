package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.mahindra.be_lms.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoViewActivity extends BaseActivity {
    @BindView(R.id.videoView_VideoFragment)
    VideoView videoView;
    @BindView(R.id.progressBar_VideoFragment)
    ProgressBar progressBar;
    private int position = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_view);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
        init();
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                   /* if (!getSupportActionBar().isShowing()){
                        getSupportActionBar().show();
                        mediaController.show(0);
                    }*/
                    position = videoView.getCurrentPosition();
                    return false;
                } else {
                    /*if (getSupportActionBar().isShowing())
                    {
                        getSupportActionBar().hide();
                        mediaController.hide();
                    }*/
                    videoView.seekTo(position);
                    videoView.start();
                    return false;
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (videoView != null) {
            savedInstanceState.putInt("position", videoView.getCurrentPosition());
        }

        assert videoView != null;
        videoView.pause();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent != null) {
            String video_url = intent.getStringExtra(getString(R.string.testpaper_url_parcelable_tag));
            playVideo(video_url);
        }
    }

    private void playVideo(String video_url) {
        Uri video_uri = Uri.parse(video_url);
        progressBar.setVisibility(View.VISIBLE);
        try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setVideoURI(video_uri);
            videoView.requestFocus();
            videoView.setMediaController(mediaController);
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
               /* if (getSupportActionBar().isShowing())
                {
                    getSupportActionBar().hide();
                    mediaController.hide();
                }*/
                videoView.seekTo(position);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //here
                videoView.seekTo(1);
            }
        });
        //here
        if (position != 1) {
            videoView.seekTo(position);
            videoView.start();
        } else {
            //here
            videoView.seekTo(1);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
