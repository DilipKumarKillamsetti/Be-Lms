package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.ViewDocument;
import com.mahindra.be_lms.util.ImageHelper;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/12/2018.
 */

public class ViewDocumentFragmnet extends Fragment {

    @BindView(R.id.vv_video)
    VideoView videoView;
    @BindView(R.id.progressBar_VideoFragment)
    ProgressBar progressBar;
    @BindView(R.id.iv_mamualInfo)
    ImageView iv_mamualInfo;
    @BindView(R.id.wv_mamualInfo)
    WebView wv_mamualInfo;
    private int position = 1;
    private DashboardActivity dashboardActivity;
    public ViewDocumentFragmnet(){

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoView != null) {
            outState.putInt("position", videoView.getCurrentPosition());
        }

        assert videoView != null;
        videoView.pause();
        //Save the fragment's state here
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            position =  savedInstanceState.getInt("position");
            //position = getFragmentManager().getFragment(savedInstanceState, );

        }
        init();
        return view;
    }


    public void init(){

        if (getArguments() != null) {
            String video_url = getArguments().getString("url");
            String type = getArguments().getString("type");
            if(type=="video"){
                playVideo(video_url);
            }else if(type=="image"){
                videoView.setVisibility(View.GONE);
                iv_mamualInfo.setVisibility(View.VISIBLE);
               // iv_mamualInfo.setImageBitmap(ImageHelper.decodeBase64(video_url));
                Picasso.with(getActivity())
                        .load(video_url)
                        .placeholder(getResources().getDrawable(R.drawable.user_new))
                        .into(iv_mamualInfo);
            }else if(type=="doc"){
                videoView.setVisibility(View.GONE);
                iv_mamualInfo.setVisibility(View.GONE);
                wv_mamualInfo.setVisibility(View.VISIBLE);
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading File, please wait...");
                dialog.setCancelable(false);
                dialog.show();
                wv_mamualInfo.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        wv_mamualInfo.loadUrl("javascript:(function() { " +
                                "document.querySelector('[role=\"toolbar\"]').remove();})()");
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
               // String docURL = getIntent().getStringExtra("doc_url");
                String fURL = video_url.replace("forcedownload=1&","");
                wv_mamualInfo.getSettings().setJavaScriptEnabled(true);
                wv_mamualInfo.getSettings().setPluginState(WebSettings.PluginState.ON);
                wv_mamualInfo.loadUrl("http://docs.google.com/gview?embedded=true&chrome=false&url=" + fURL);
                wv_mamualInfo.getSettings().setLoadWithOverviewMode(true);
            }

        }
    }

    private void playVideo(String video_url) {
        Uri video_uri = Uri.parse(video_url);
        progressBar.setVisibility(View.VISIBLE);
        try {
            MediaController mediaController = new MediaController(getActivity());
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

            videoView.seekTo(1);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboardActivity = (DashboardActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        videoView.stopPlayback();
        videoView.suspend();

    }
}
