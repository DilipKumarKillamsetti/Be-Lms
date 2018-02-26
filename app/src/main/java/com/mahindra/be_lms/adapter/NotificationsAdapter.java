package com.mahindra.be_lms.adapter;

/**
 * Created by training1 on 11/12/16.
 */

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.model.Notifications;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {
    private List<Notifications> notificationList;
    private Context context;
    private Callback myCallback;

    public NotificationsAdapter(Context context, List<Notifications> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_notification_layout, parent, false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, int position) {

        holder.tvNotificationTitle.setText( notificationList.get(position).getDate());
        holder.tvNotificationMessage.setText("Message: " + notificationList.get(position).getNotes());
//        if (!TextUtils.isEmpty(notificationList.get(position).getNotificationattachLink()) && !notificationList.get(position).getNotificationattachLink().equalsIgnoreCase("null")) {
//            holder.linearLayoutAttachment.setVisibility(View.VISIBLE);
//            String finalLink = Constants.LMS_Common_URL + notificationList.get(position).getNotificationattachLink();
//            String filename = finalLink.substring(finalLink.lastIndexOf("/") + 1);
//            L.l("filename: " + filename);
//            holder.tvNotificationAttachmentLink.setText(filename);
//            holder.tvNotificationAttachmentLink.setPaintFlags(holder.tvNotificationAttachmentLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        } else {
//            holder.linearLayoutAttachment.setVisibility(View.GONE);
//        }
//
//        if (!TextUtils.isEmpty(notificationList.get(position).getNotificationextraLink()) && !notificationList.get(position).getNotificationextraLink().equalsIgnoreCase("null")) {
//            holder.linearLayoutNotiDocumentLink.setVisibility(View.VISIBLE);
//            Drawable drawable = null;
//            try {
//                //{"141":{"LMS initial registration":"1305160840540R.pdf"}}
//                JSONObject jsonObject = new JSONObject(notificationList.get(position).getNotificationextraLink().toString());
//                Iterator<String> iter = jsonObject.keys();
//                while (iter.hasNext()) {
//                    String key = iter.next();
//                    L.l("Document Tree ID: " + key);
//                    try {
//                        Object dockeyValue = jsonObject.get(key);
//                        L.l("Document keyValue: " + dockeyValue);
//                        JSONObject jsonObject1 = new JSONObject(dockeyValue.toString());
//                        Iterator<String> iterator = jsonObject1.keys();
//                        while (iterator.hasNext()) {
//                            String docTitle = iterator.next();
//                            try {
//                                Object docName = jsonObject1.get(docTitle);
//                                L.l("Document Name: " + docName.toString());
//                                String extension = CommonFunctions.getExtension(docName.toString());
//                                L.l("File Extension : " + extension);
//                                if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
//                                    drawable = context.getResources().getDrawable(R.drawable.icon_xls);
//                                } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
//                                    drawable = context.getResources().getDrawable(R.drawable.icon_doc);
//                                } else if (extension.equalsIgnoreCase("pdf")) {
//                                    drawable = context.getResources().getDrawable(R.drawable.icon_pdf);
//                                } else {
//                                    drawable = context.getResources().getDrawable(R.drawable.unknown_icon);
//                                }
//                                holder.tvNotificationDocumentLink.setText(docTitle);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } catch (JSONException e) {
//                        // Something went wrong!
//                        e.printStackTrace();
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            holder.ivNotificationDocumentIcon.setImageDrawable(drawable);
//        } else {
//            holder.linearLayoutNotiDocumentLink.setVisibility(View.GONE);
//        }
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
        @BindView(R.id.llnotificationMain)
        LinearLayout linearLayout;
        @BindView(R.id.tvNotificationTitle)
        TextView tvNotificationTitle;
        @BindView(R.id.tvNotificationMessage)
        TextView tvNotificationMessage;
        @BindView(R.id.tvNotificationAttachmentLink)
        TextView tvNotificationAttachmentLink;
        @BindView(R.id.llNotiAttachment)
        LinearLayout linearLayoutAttachment;
        @BindView(R.id.llNotiDocumentLink)
        LinearLayout linearLayoutNotiDocumentLink;
        @BindView(R.id.tvNotificationDocumentLink)
        TextView tvNotificationDocumentLink;
        @BindView(R.id.ivNotificationDocumentIcon)
        ImageView ivNotificationDocumentIcon;

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
