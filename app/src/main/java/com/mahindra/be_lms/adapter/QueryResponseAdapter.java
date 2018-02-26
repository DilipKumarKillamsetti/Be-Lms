package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Chaitali Chavan on 11/10/16.
 * Modified By Chaitali Chavan on 11/10/16,
 */
public class QueryResponseAdapter extends RecyclerView.Adapter<QueryResponseAdapter.QueriesResponseViewHolder> {
    private List<QueryResponse> queryResponseList;
    private Context context;
    private Callback myCallback;

    public QueryResponseAdapter(Context context, List<QueryResponse> queryResponseList, int width) {
        this.context = context;
        this.queryResponseList = queryResponseList;
        int width1 = width;
    }

    public static Bitmap decodeBase64(String input) {
        L.l("In decode Bitmap function");
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public QueriesResponseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_query_response_layout, parent, false);
        return new QueriesResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueryResponseAdapter.QueriesResponseViewHolder holder, int position) {

        if (queryResponseList.get(position).getMsg_type().equals("response")) {
            holder.llQueryReplyMain.setVisibility(View.GONE);
            holder.llQueryReplyLayout.setVisibility(View.GONE);
            holder.llQueryResponseMain.setVisibility(View.VISIBLE);
            holder.llQueryResponseLayout.setVisibility(View.VISIBLE);
            holder.tvResponseTitle.setVisibility(View.GONE);
            holder.tvResponseMessage.setText("Message: " + queryResponseList.get(position).getMessage());
            holder.tvResponseResponsiblePerson.setText(queryResponseList.get(position).getResposePerson());
            if (!TextUtils.isEmpty(queryResponseList.get(position).getQueryReplyAttachment()) && !queryResponseList.get(position).getQueryReplyAttachment().equalsIgnoreCase("null")) {
                holder.llQueryResponseAttachment.setVisibility(View.VISIBLE);
                String final_url = Constants.LMS_Common_URL + queryResponseList.get(position).getQueryReplyAttachment();
                L.l("Final Response Attach Link: " + final_url);
                Picasso.with(context).load(final_url).placeholder(R.drawable.powerol_logo).into(holder.ivQueryResponseAttachment);
            } else {
                holder.llQueryResponseAttachment.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(queryResponseList.get(position).getQueryResponseExtraLink()) && !queryResponseList.get(position).getQueryResponseExtraLink().equalsIgnoreCase("null")) {
                holder.linearLayoutResponseDocument.setVisibility(View.VISIBLE);
               /*  {query_id=69, type=query_replay, title=Reply for Query,
              extra_link={"144":{"Radiator cleaner and antifreeze recommendation":"1310151038260R.pdf"}},
              message=doc link, notificationType=3, responsible_person=MADMIN} */
                try {
                    //{"141":{"LMS initial registration":"1305160840540R.pdf"}}
                    JSONObject jsonObject = new JSONObject(queryResponseList.get(position).getQueryResponseExtraLink().toString());
                    Iterator<String> iter = jsonObject.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        L.l("Document Tree ID: " + key);
                        try {
                            Object dockeyValue = jsonObject.get(key);
                            L.l("Document keyValue: " + dockeyValue);
                            JSONObject jsonObject1 = new JSONObject(dockeyValue.toString());
                            Iterator<String> iterator = jsonObject1.keys();
                            while (iterator.hasNext()) {
                                String docTitle = iterator.next();
                                try {
                                    Object docName = jsonObject1.get(docTitle);
                                    L.l("Document Name: " + docName.toString());
                                    String extension = CommonFunctions.getExtension(docName.toString());
                                    L.l("File Extension : " + extension);
                                    holder.tvQueryResponseDocumentLink.setText(docTitle);
                                    holder.tvQueryResponseDocumentLink.setPaintFlags(holder.tvQueryResponseDocumentLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            // Something went wrong!
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                holder.linearLayoutResponseDocument.setVisibility(View.GONE);
            }
        } else {
            holder.llQueryResponseMain.setVisibility(View.GONE);
            holder.llQueryResponseLayout.setVisibility(View.GONE);
            holder.llQueryReplyMain.setVisibility(View.VISIBLE);
            holder.llQueryReplyLayout.setVisibility(View.VISIBLE);
            holder.tvReplyTitle.setVisibility(View.GONE);
            //  holder.tvReplyTitle.setText("Title: "+queryResponseList.get(position).getTitle());
            holder.tvReplyMessage.setText("Message: " + queryResponseList.get(position).getMessage());
            holder.tvReplyResponsiblePerson.setText(queryResponseList.get(position).getResposePerson());
            if (!TextUtils.isEmpty(queryResponseList.get(position).getQueryReplyAttachment()) && !queryResponseList.get(position).getQueryReplyAttachment().equalsIgnoreCase("null")) {
                holder.llQueryReplyAttachment.setVisibility(View.VISIBLE);
                Bitmap bitmap = decodeBase64(queryResponseList.get(position).getQueryReplyAttachment());
                if (bitmap != null) {
                    holder.ivQueryReplyAttachment.setImageBitmap(bitmap);
                }
            } else {
                holder.llQueryReplyAttachment.setVisibility(View.GONE);
            }
        }

    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    public void setQueryResponseList(List<QueryResponse> queryResponseList) {
        this.queryResponseList = queryResponseList;
    }

    @Override
    public int getItemCount() {
        return queryResponseList.size();
    }

    public class QueriesResponseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llQueryResponse)
        LinearLayout llQueryResponseLayout;
        @BindView(R.id.llQueryReply)
        LinearLayout llQueryReplyLayout;
        @BindView(R.id.tvResponseTitle)
        TextView tvResponseTitle;
        @BindView(R.id.tvResponseMessage)
        TextView tvResponseMessage;
        @BindView(R.id.tvResponseResponsiblePerson)
        TextView tvResponseResponsiblePerson;
        @BindView(R.id.tvReplyTitle)
        TextView tvReplyTitle;
        @BindView(R.id.tvReplyMessage)
        TextView tvReplyMessage;
        @BindView(R.id.tvReplyResponsiblePerson)
        TextView tvReplyResponsiblePerson;
        @BindView(R.id.llQueryReplyMain)
        LinearLayout llQueryReplyMain;
        @BindView(R.id.llQueryResponseMain)
        LinearLayout llQueryResponseMain;
        @BindView(R.id.ivQueryReplyAttachment)
        ImageView ivQueryReplyAttachment;
        @BindView(R.id.ivQueryResponseAttachment)
        ImageView ivQueryResponseAttachment;
        @BindView(R.id.llQueryResponseAttachment)
        LinearLayout llQueryResponseAttachment;
        @BindView(R.id.llQueryReplyAttachment)
        LinearLayout llQueryReplyAttachment;
        @BindView(R.id.llQueryResponseDocumentLink)
        LinearLayout linearLayoutResponseDocument;
        @BindView(R.id.tvQueryResponseDocumentLink)
        TextView tvQueryResponseDocumentLink;

        public QueriesResponseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.llQueryResponseDocumentLink)
        public void OnClickDocumentLink() {
            myCallback.myCallback(getAdapterPosition());
        }
    }

}
