package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.Notifications;
import com.mahindra.be_lms.util.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/8/2018.
 */

public class PostNotificationAdapter extends RecyclerView.Adapter<PostNotificationAdapter.NotificationsViewHolder> {

    private List<Notifications> notificationList;
    private Context context;
    private Callback myCallback;

    public PostNotificationAdapter(Context context, List<Notifications> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public PostNotificationAdapter.NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_post_notification, parent, false);
        return new PostNotificationAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostNotificationAdapter.NotificationsViewHolder holder, int position) {

//        holder.tvNotificationTitle.setText( notificationList.get(position).getDate());
//        holder.tvNotificationMessage.setText("Message: " + notificationList.get(position).getNotes());

    }

    public List<Notifications> setupNotificationList(List<Notifications> notificationList) {
        this.notificationList = notificationList;
        return this.notificationList;
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_pic)
        CircularImageView circularImageView;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

//        @OnClick(R.id.tvNotificationAttachmentLink)
//        public void OnAttachmentTouch() {
//            myCallback.myCallback(getAdapterPosition(), "Attachment");
//        }
//
//        @OnClick(R.id.llNotiDocumentLink)
//        public void onDocumentClick() {
//            myCallback.myCallback(getAdapterPosition(), "Document");
//        }
    }
}
