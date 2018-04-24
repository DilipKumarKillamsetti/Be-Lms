package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.Datum;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.videosupport.AAH_CustomViewHolder;
import com.mahindra.be_lms.videosupport.AAH_VideosAdapter;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;


public class MyVideosAdapter extends AAH_VideosAdapter {

    private List<Datum> list;
    private final Picasso picasso;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Callback myCallback;
    private Context context;

    public void setData(List<Datum> modelList) {
        this.list = modelList;
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    public class MyViewHolder extends AAH_CustomViewHolder {
        final TextView tv, postBy, postName, postOn, tv_title, tv_likesCount, tv_commentsCount;
        final ImageView img_vol, img_playback;
        final LinearLayout ll_comment;
        final CheckBox cb_like;
        final CircularImageView header_imageView;
        final FrameLayout fl_click;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x, x.getContext());
            tv = ButterKnife.findById(x, R.id.tv);
            postBy = ButterKnife.findById(x, R.id.postBy);
            postName = ButterKnife.findById(x, R.id.postName);
            img_vol = ButterKnife.findById(x, R.id.img_vol);
            img_playback = ButterKnife.findById(x, R.id.img_playback);
            postOn = ButterKnife.findById(x, R.id.postOn);
            tv_title = ButterKnife.findById(x, R.id.tv_title);
            ll_comment = ButterKnife.findById(x, R.id.ll_comment);
            cb_like = ButterKnife.findById(x, R.id.cb_like);
            tv_commentsCount = ButterKnife.findById(x, R.id.tv_commentsCount);
            tv_likesCount = ButterKnife.findById(x, R.id.tv_likesCount);
            header_imageView = ButterKnife.findById(x, R.id.header_imageView);
            fl_click = ButterKnife.findById(x,R.id.fl_click);
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
        final TextView tv, postBy, postName, newsText, postOn, tv_title, tv_likesCount, tv_commentsCount;
        final LinearLayout ll_comment;
        final CheckBox cb_like;
        final CircularImageView header_imageView;

        public MyTextViewHolder(View x) {
            super(x, x.getContext());
            tv = ButterKnife.findById(x, R.id.tv);
            postBy = ButterKnife.findById(x, R.id.postBy);
            postName = ButterKnife.findById(x, R.id.postName);
            newsText = ButterKnife.findById(x, R.id.newsText);
            postOn = ButterKnife.findById(x, R.id.postOn);
            ll_comment = ButterKnife.findById(x, R.id.ll_comment);
            tv_title = ButterKnife.findById(x, R.id.tv_title);
            cb_like = ButterKnife.findById(x, R.id.cb_like);
            tv_commentsCount = ButterKnife.findById(x, R.id.tv_commentsCount);
            tv_likesCount = ButterKnife.findById(x, R.id.tv_likesCount);
            header_imageView = ButterKnife.findById(x, R.id.header_imageView);
        }
    }

    public MyVideosAdapter(List<Datum> list_urls, Picasso p, Context context) {
        this.list = list_urls;
        this.picasso = p;
        this.context = context;
    }

    private class LoadingViewHolder extends AAH_CustomViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view, view.getContext());
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View itemView1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_text, parent, false);
            return new MyTextViewHolder(itemView1);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);

            return new LoadingViewHolder(view);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_card, parent, false);
            return new MyViewHolder(itemView);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, final int position) {

        if (list.get(position).getAttachment().equalsIgnoreCase("0")) {

            String text = "<font color=#1a1818>" + list.get(position).getSharedByName();
            ((MyTextViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            if (null != list.get(position).getSharedByName()) {

                        ((MyTextViewHolder) holder).newsText.setText(list.get(position).getDescription());
                ((MyTextViewHolder) holder).cb_like.setChecked(list.get(position).getLikedStatus());
                ((MyTextViewHolder) holder).tv_commentsCount.setText(list.get(position).getCommentsCounts() + " " + "Comments");
                ((MyTextViewHolder) holder).tv_likesCount.setText(list.get(position).getLikesCount() + " " + "Likes");
                if (list.get(position).getUserpic().equals("")) {
                    ((MyTextViewHolder) holder).header_imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
                } else {
                    Picasso.with(context)
                            .load(list.get(position).getUserpic())
                            .resize(200, 200)
                            .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                            .centerCrop()
                            .into(((MyTextViewHolder) holder).header_imageView);
                    Log.e("IS FILEEXIST FLAG: ", "isFilexists");

                }


            }

            ((MyTextViewHolder) holder).tv_likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });
            ((MyTextViewHolder) holder).tv_commentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });

            ((MyTextViewHolder) holder).ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });

            ((MyTextViewHolder) holder).cb_like.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    boolean chekedStatus = ((MyTextViewHolder) holder).cb_like.isChecked();
                    myCallback.myCallback(position, "" + chekedStatus, list.get(position).getId(), "likes");
                }}
            );


        } else {
            if ( TextUtils.isEmpty(list.get(position).getLocation()) ) {
                String text = "<font color=#1a1818>" + list.get(position).getSharedByName() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + list.get(position).getDescription() + "</font>";
                ((MyViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            } else {
                String text = "<font color=#1a1818>" + list.get(position).getSharedByName() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + list.get(position).getDescription() + " at " + list.get(position).getLocation() + "</font>";
                ((MyViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            }


            ((MyViewHolder) holder).tv.setText(list.get(position).getDescription());

            ((MyViewHolder) holder).cb_like.setChecked(list.get(position).getLikedStatus());
            ((MyViewHolder) holder).tv_commentsCount.setText(list.get(position).getCommentsCounts() + " " + "Comments");
            ((MyViewHolder) holder).tv_likesCount.setText(list.get(position).getLikesCount() + " " + "Likes");
            //todo
            String extension = CommonFunctions.getExtension(list.get(position).getImageUrl());

            if (extension.contains("mp4") || extension.contains("mpeg")) {
                holder.setImageUrl(list.get(position).getThumbUrl());
                holder.setVideoUrl(list.get(position).getImageUrl());
                Glide.with(context)
                        .load(list.get(position).getThumbUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.getAAH_ImageView());
            }else{
                holder.setImageUrl(list.get(position).getImageUrl());
                Glide.with(context)
                        .load(holder.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.getAAH_ImageView());
            }

            //load image into imageview
//            if (list.get(position).getImageUrl() != null && !list.get(position).getImageUrl().isEmpty()) {
//              //  picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
//
//                //PicassoCache.getPicassoInstance(context).load(holder.getImageUrl()).into(holder.getAAH_ImageView());
//            } else {
//
//                holder.getAAH_ImageView().setImageBitmap(list.get(position).getBitmap());
//
//            }

            if (list.get(position).getUserpic().equals("")) {

                ((MyViewHolder) holder).header_imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
            } else {
                Picasso.with(context)
                        .load(list.get(position).getUserpic())
                        .resize(200, 200)
                        .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                        .centerCrop()
                        .into(((MyViewHolder) holder).header_imageView);
                Log.e("IS FILEEXIST FLAG: ", "isFilexists");

            }
            ((MyViewHolder) holder).tv_likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });
            ((MyViewHolder) holder).tv_commentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });

            ((MyViewHolder) holder).ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "comments");
                }
            });
            ((MyViewHolder) holder).fl_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(list.get(position).getThumbUrl())){
                        if (holder.isPlaying()) {
                            holder.pauseVideo();
                            holder.setPaused(true);
                        }
                        myCallback.myCallback(position, list.get(position).getImageUrl(), "video", "view");
                    }else{
                        myCallback.myCallback(position, list.get(position).getImageUrl(), "image", "view");
                    }

                }
            });
            holder.setLooping(false); //optional - true by default

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

            if (TextUtils.isEmpty(list.get(position).getThumbUrl())) {
                ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
            }
            ((MyViewHolder) holder).cb_like.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean chekedStatus = ((MyViewHolder) holder).cb_like.isChecked();
                    myCallback.myCallback(position, "" + chekedStatus, list.get(position).getId(), "likes");
                }}
            );
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getAttachment().equalsIgnoreCase("0")) {
            return TYPE_TEXT;
        } else if (list.get(position) == null) {
            return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return TYPE_VIDEO;
        }
    }


}