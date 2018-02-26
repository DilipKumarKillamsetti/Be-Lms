package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.TechnicalUpload;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pravin on 10/4/16.
 */
public class TechnicalUploadsAdapter extends RecyclerView.Adapter<TechnicalUploadsAdapter.TUViewHolder> {
    private final Context context;
    private List<TechnicalUpload> technicalUploadList;
    private MyCallback myCallback;

    public TechnicalUploadsAdapter(Context context, List<TechnicalUpload> technicalUploadList) {
        this.context = context;
        this.technicalUploadList = technicalUploadList;
    }

    @Override
    public TUViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_technical_upload_layout, parent, false);
        return new TUViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TUViewHolder holder, int position) {
        String extension = CommonFunctions.getExtension(technicalUploadList.get(position).getAttachmentList());
        L.l("File Extension : " + extension);
        holder.tvTechnicalUploadTitle.setText(technicalUploadList.get(position).getTechnicalUploadTitle());
        holder.tvTechnicalUploadDesc.setText(technicalUploadList.get(position).getTechnicalUploadDesc());
        holder.tvTechnicalUploadDate.setText(technicalUploadList.get(position).getEntry_date());
        holder.tvTechnicalUploadUserInfo.setVisibility(View.GONE);
        Drawable drawableIcon = null;
        if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_xls);
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_doc);
        } else if (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_video);
        } else if (extension.equalsIgnoreCase("pdf")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_pdf);
        } else {
            drawableIcon = context.getResources().getDrawable(R.drawable.unknown_icon);
        }
        holder.ivTechnicalUploadIcon.setImageDrawable(drawableIcon);
        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("JPE")
                || extension.equalsIgnoreCase("IMG")
                || extension.equalsIgnoreCase("GIF")
                || extension.equalsIgnoreCase("PSD")
                || extension.equalsIgnoreCase("jpeg")) {
            Picasso.with(context).load(technicalUploadList.get(position).getAttachmentList()).into(holder.ivTechnicalUploadIcon);
        }

    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    @Override
    public int getItemCount() {
        return technicalUploadList.size();
    }

    public void setTechnicalUploadList(List<TechnicalUpload> technicalUploadList) {
        this.technicalUploadList = technicalUploadList;
    }

    public class TUViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTechnicalUploadTitle)
        TextView tvTechnicalUploadTitle;
        @BindView(R.id.tvTechnicalUploadDesc)
        TextView tvTechnicalUploadDesc;
        @BindView(R.id.ivTechnicalUploadIcon)
        ImageView ivTechnicalUploadIcon;
        @BindView(R.id.tvTechnicalUploadDate)
        TextView tvTechnicalUploadDate;
        @BindView(R.id.tvTechnicalUploadUserInfo)
        TextView tvTechnicalUploadUserInfo;

        public TUViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(getAdapterPosition());
                }
            });
        }

    }
}
