package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

        if(notificationList.get(position).getAttachment().equals("0")){
            String text = "<font color=#1a1818>" + notificationList.get(position).getSharedByName() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + notificationList.get(position).getDescription() + "</font>";
            holder.tv_details.setText(Html.fromHtml(text));
        }else{
            if(notificationList.get(position).getLocation()== null || notificationList.get(position).getLocation().equals("") ){
                String text = "<font color=#1a1818>" + notificationList.get(position).getSharedByName() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + notificationList.get(position).getDescription() + "</font>";
                holder.tv_details.setText(Html.fromHtml(text));
            }else{
                String text = "<font color=#1a1818>" + notificationList.get(position).getSharedByName() + "</font> <font color=#B6B6B6>Shared</font> <font color=#1a1818>" + notificationList.get(position).getDescription() +" at "+notificationList.get(position).getLocation() + "</font>";
                holder.tv_details.setText(Html.fromHtml(text));
            }
        }

        if (notificationList.get(position).getUserPic().equals("")) {
            holder.circularImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_new));
        } else {
            Picasso.with(context)
                    .load(notificationList.get(position).getUserPic())
                    .resize(200, 200)
                    .placeholder(context.getResources().getDrawable(R.drawable.user_new))
                    .centerCrop()
                    .into(holder.circularImageView);
            Log.e("IS FILEEXIST FLAG: ","isFilexists");

        }



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

    public void setNotificationList(ArrayList<Notifications> notificationses) {
        this.notificationList = notificationses;

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
