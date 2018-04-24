package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.MyModel;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.videosupport.AAH_CustomViewHolder;
import com.mahindra.be_lms.videosupport.AAH_VideosAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;


public class MyVideosAdapterByMe extends AAH_VideosAdapter {

    private List<MyModel> list;
    private final Picasso picasso;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Callback myCallback;
    private Context context;

    public void setData(List<MyModel> modelList) {
        this.list = modelList;
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    public class MyViewHolder extends AAH_CustomViewHolder {
        final TextView tv, postBy, postName, postOn, tv_title, tv_likesCount, tv_commentsCount;
        final ImageView img_vol, img_playback;
        final LinearLayout ll_comment,ll_delete;
        final CheckBox cb_like;
        final CircularImageView header_imageView;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x, x.getContext());
            ll_delete = ButterKnife.findById(x, R.id.ll_delete);
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
        final LinearLayout ll_comment,ll_delete;
        final CheckBox cb_like;
        final CircularImageView header_imageView;

        public MyTextViewHolder(View x) {
            super(x, x.getContext());
            ll_delete = ButterKnife.findById(x, R.id.ll_delete);
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

    public MyVideosAdapterByMe(List<MyModel> list_urls, Picasso p, Context context) {
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

        if (list.get(position).getType().equalsIgnoreCase("text")) {
            ((MyTextViewHolder) holder).ll_delete.setVisibility(View.VISIBLE);
            String text = "<font color=#1a1818>" + list.get(position).getPostedBy();
            ((MyTextViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            if (null != list.get(position).getPostedBy()) {
                ((MyTextViewHolder) holder).tv.setText(list.get(position).getName());
                ((MyTextViewHolder) holder).postBy.setText("Post By: " + list.get(position).getPostedBy());
                ((MyTextViewHolder) holder).postName.setText(list.get(position).getName());
                ((MyTextViewHolder) holder).newsText.setText(list.get(position).getName());
                ((MyTextViewHolder) holder).postOn.setText(list.get(position).getPostedOn());
                ((MyTextViewHolder) holder).cb_like.setChecked(list.get(position).isLikeStatus());
                ((MyTextViewHolder) holder).tv_commentsCount.setText(list.get(position).getCommentCount() + " " + "Comments");
                ((MyTextViewHolder) holder).tv_likesCount.setText(list.get(position).getLikesCount() + " " + "Likes");
                if (list.get(position).getProfilePic().equals("")) {
                    ((MyTextViewHolder) holder).header_imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
                } else {
                    Picasso.with(context)
                            .load(list.get(position).getProfilePic())
                            .resize(200, 200)
                            .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                            .centerCrop()
                            .into(((MyTextViewHolder) holder).header_imageView);
                    Log.e("IS FILEEXIST FLAG: ", "isFilexists");

                }
//                if(list.get(position).getMessage( )!= null){
//                    if (list.get(position).getMessage().contains("<")) {
//                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
//                            ((MyTextViewHolder) holder).newsText.setText(Html.fromHtml(list.get(position).getMessage(), Html.FROM_HTML_MODE_COMPACT));
//
//                        } else {
//                            ((MyTextViewHolder) holder).newsText.setText(Html.fromHtml(list.get(position).getMessage()));
//                        }
//                    } else {
//                        ((MyTextViewHolder) holder).newsText.setText(list.get(position).getMessage());
//                    }
//                }

            }

            ((MyTextViewHolder) holder).ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, list.get(position).getComments(), list.get(position).getId(), "comments");
                }
            });

            ((MyTextViewHolder) holder).ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "post");
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
            ((MyViewHolder) holder).ll_delete.setVisibility(View.VISIBLE);
            if (list.get(position).getLocation().equals("null") || list.get(position).getLocation().equals("")  ) {
                String text = "<font color=#1a1818>" + list.get(position).getPostedBy() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + list.get(position).getName() + "</font>";
                ((MyVideosAdapterByMe.MyViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            } else {
                String text = "<font color=#1a1818>" + list.get(position).getPostedBy() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + list.get(position).getName() + " at " + list.get(position).getLocation() + "</font>";
                ((MyVideosAdapterByMe.MyViewHolder) holder).tv_title.setText(Html.fromHtml(text));
            }

            ((MyViewHolder) holder).tv.setText(list.get(position).getName());
            ((MyViewHolder) holder).postBy.setText("Post By: " + list.get(position).getPostedBy());
            ((MyViewHolder) holder).postName.setText(list.get(position).getpName());
            ((MyViewHolder) holder).postOn.setText(list.get(position).getPostedOn());
            ((MyViewHolder) holder).cb_like.setChecked(list.get(position).isLikeStatus());
            ((MyViewHolder) holder).tv_commentsCount.setText(list.get(position).getCommentCount() + " " + "Comments");
            ((MyViewHolder) holder).tv_likesCount.setText(list.get(position).getLikesCount() + " " + "Likes");
            //todo
            holder.setImageUrl(list.get(position).getImage_url());
            holder.setVideoUrl(list.get(position).getVideo_url());

            //load image into imageview
            if (list.get(position).getImage_url() != null && !list.get(position).getImage_url().isEmpty()) {
                picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
            } else {

                holder.getAAH_ImageView().setImageBitmap(list.get(position).getBitmap());

            }

            if (list.get(position).getProfilePic().equals("")) {

                ((MyViewHolder) holder).header_imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
            } else {
                Picasso.with(context)
                        .load(list.get(position).getProfilePic())
                        .resize(200, 200)
                        .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                        .centerCrop()
                        .into(((MyViewHolder) holder).header_imageView);
                Log.e("IS FILEEXIST FLAG: ", "isFilexists");

            }

            ((MyViewHolder) holder).ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, list.get(position).getComments(), list.get(position).getId(), "comments");
                }
            });
            ((MyViewHolder) holder).ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(position, "", list.get(position).getId(), "post");
                }
            });

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

            if (list.get(position).getVideo_url() == "") {
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
        if (list.get(position).getType().equalsIgnoreCase("text")) {
            return TYPE_TEXT;
        } else if (list.get(position) == null) {
            return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return TYPE_VIDEO;
        }
    }


}