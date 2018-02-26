package com.mahindra.be_lms.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.activities.CommentsActivityDialog;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.videosupport.AAH_CustomViewHolder;
import com.mahindra.be_lms.videosupport.AAH_VideosAdapter;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

import static com.mahindra.be_lms.R.id.img_playback;
import static com.mahindra.be_lms.R.id.img_vol;



public class MyVideosAdapter extends AAH_VideosAdapter {

    private  List<MyModel> list;
    private final Picasso picasso;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;
    private Callback myCallback;
    public void setData(List<MyModel> modelList) {
        this.list = modelList;
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    public class MyViewHolder extends AAH_CustomViewHolder {
        final TextView tv, postBy, postName,postOn;
        final ImageView img_vol, img_playback;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
            tv = ButterKnife.findById(x, R.id.tv);
            postBy = ButterKnife.findById(x, R.id.postBy);
            postName = ButterKnife.findById(x, R.id.postName);
            img_vol = ButterKnife.findById(x, R.id.img_vol);
            img_playback = ButterKnife.findById(x, R.id.img_playback);
            postOn = ButterKnife.findById(x, R.id.postOn);
        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            img_playback.setImageResource(R.drawable.ic_pause);
            if (isMuted) {
                muteVideo();
                img_vol.setImageResource(R.drawable.ic_mute);
            } else {
                unmuteVideo();
                img_vol.setImageResource(R.drawable.ic_unmute);
            }
        }

        @Override
        public void pauseVideo() {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }


    public class MyTextViewHolder extends AAH_CustomViewHolder {
        final TextView tv, postBy, postName,newsText,postOn;
        final LinearLayout ll_comment;

        public MyTextViewHolder(View x) {
            super(x);
            tv = ButterKnife.findById(x, R.id.tv);
            postBy = ButterKnife.findById(x, R.id.postBy);
            postName = ButterKnife.findById(x, R.id.postName);
            newsText = ButterKnife.findById(x, R.id.newsText);
            postOn = ButterKnife.findById(x, R.id.postOn);
            ll_comment = ButterKnife.findById(x, R.id.ll_comment);

        }
    }

    public MyVideosAdapter(List<MyModel> list_urls, Picasso p) {
        this.list = list_urls;
        this.picasso = p;
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View itemView1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_text, parent, false);
            return new MyTextViewHolder(itemView1);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_card, parent, false);
            return new MyViewHolder(itemView);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, final int position) {
        if (list.get(position).getType().equalsIgnoreCase("text")) {
            if(null != list.get(position).getPostedBy()){
                ((MyTextViewHolder) holder).tv.setText(list.get(position).getName());
                ((MyTextViewHolder) holder).postBy.setText("Post By: "+list.get(position).getPostedBy());
                ((MyTextViewHolder) holder).postName.setText(list.get(position).getName());
                ((MyTextViewHolder) holder).newsText.setText(list.get(position).getMessage());
                ((MyTextViewHolder) holder).postOn.setText(list.get(position).getPostedOn());
                if(list.get(position).getMessage().contains("<")){
                    if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
                        ((MyTextViewHolder) holder).newsText.setText(Html.fromHtml(list.get(position).getMessage(), Html.FROM_HTML_MODE_COMPACT));

                    }else{
                        ((MyTextViewHolder) holder).newsText.setText(Html.fromHtml(list.get(position).getMessage()));

                    }
                }else{
                    ((MyTextViewHolder) holder).newsText.setText(list.get(position).getMessage());
                }
            }

            ((MyTextViewHolder) holder).ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position);
                }
            });


        } else {
            ((MyViewHolder) holder).tv.setText(list.get(position).getName());
            ((MyViewHolder) holder).postBy.setText("Post By: "+list.get(position).getPostedBy());
            ((MyViewHolder) holder).postName.setText(list.get(position).getpName());
            ((MyViewHolder) holder).postOn.setText(list.get(position).getPostedOn());
            //todo
            holder.setImageUrl(list.get(position).getImage_url());
            holder.setVideoUrl(list.get(position).getVideo_url());

            //load image into imageview
            if (list.get(position).getImage_url() != null && !list.get(position).getImage_url().isEmpty()) {
                picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
            }

            holder.setLooping(true); //optional - true by default

            //to play pause videos manually (optional)
            ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    } else {
                        holder.playVideo();
                        holder.setPaused(false);
                    }
                }
            });

            //to mute/un-mute video (optional)
            ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MyViewHolder) holder).isMuted) {
                        holder.unmuteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    } else {
                        holder.muteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    }
                    ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
                }
            });

            if (list.get(position).getVideo_url() == null) {
                ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType().equalsIgnoreCase("text")) {
            return TYPE_TEXT;
        } else return TYPE_VIDEO;
    }

}