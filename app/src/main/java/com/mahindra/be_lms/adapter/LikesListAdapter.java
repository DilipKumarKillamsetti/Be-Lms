package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.LikesListResponse;
import com.mahindra.be_lms.model.Notifications;
import com.mahindra.be_lms.util.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/8/2018.
 */

public class LikesListAdapter extends RecyclerView.Adapter<LikesListAdapter.NotificationsViewHolder> {

    private List<LikesListResponse> likesListResponses;
    private Context context;
    private Callback myCallback;

    public LikesListAdapter(Context context, List<LikesListResponse> likesListResponses) {
        this.context = context;
        this.likesListResponses = likesListResponses;
    }

    @Override
    public LikesListAdapter.NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_post_notification, parent, false);
        return new LikesListAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LikesListAdapter.NotificationsViewHolder holder, int position) {

        holder.tv_details.setText(likesListResponses.get(position).getUsername());
        if (likesListResponses.get(position).getUserpic().equals("")) {
            holder.circularImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
        } else {
            Picasso.with(context)
                    .load(likesListResponses.get(position).getUserpic())
                    .resize(200, 200)
                    .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                    .centerCrop()
                    .into(holder.circularImageView);
            Log.e("IS FILEEXIST FLAG: ","isFilexists");

        }



    }

    public List<LikesListResponse> setupNotificationList(List<LikesListResponse> likesListResponses) {
        this.likesListResponses = likesListResponses;
        return this.likesListResponses;
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    @Override
    public int getItemCount() {
        return likesListResponses.size();
    }

    public void setNotificationList(ArrayList<LikesListResponse> likesListResponses) {
        this.likesListResponses = likesListResponses;

    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_pic)
        CircularImageView circularImageView;
        @BindView(R.id.tv_details)
        TextView tv_details;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
