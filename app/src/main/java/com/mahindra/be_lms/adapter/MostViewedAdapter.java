package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.MostView;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pravin on 10/4/16.
 * Modified by Chaitali Chavan on 12/11/16.
 */
public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.MostViewHolder> {
    private final Context context;
    private List<MostView> mostViewList;
    private MyCallback myCallback;

    public MostViewedAdapter(Context context, List<MostView> mostViewList) {
        this.context = context;
        this.mostViewList = mostViewList;
    }

    @Override
    public MostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manual_infromation_items, parent, false);
        return new MostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MostViewHolder holder, final int position) {
        holder.tv_count.setVisibility(View.VISIBLE);
        holder.tv_count.setText(mostViewList.get(position).getViewedcount());
        holder.rvManualName.setText(mostViewList.get(position).getName());
        Drawable drawableIcon = null;
        String extension = CommonFunctions.getExtension( mostViewList.get(position).getFilename());
        if (extension.contains("xls") || extension.contains("xlsx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_xls);
        } else if (extension.contains("doc") || extension.contains("docx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_doc);
        } else if (extension.contains("mp4") || extension.contains("mpeg")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_video);
        } else if (extension.contains("pdf")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_pdf);
        } else {
            drawableIcon = context.getResources().getDrawable(R.drawable.unknown_icon);
        }
        holder.fileImage.setImageDrawable(drawableIcon);

        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("JPE")
                || extension.equalsIgnoreCase("IMG")
                || extension.equalsIgnoreCase("GIF")
                || extension.equalsIgnoreCase("PSD")
                || extension.equalsIgnoreCase("jpeg")) {
            Picasso.with(context).load( mostViewList.get(position).getFileurl()+"&token="+ MyApplication.mySharedPreference.getUserToken()).placeholder(R.drawable.icon_image).into(holder.fileImage);
        }

        holder.downloadLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myCallback.myCallback(position);
            }
        });

    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    @Override
    public int getItemCount() {
        return mostViewList.size();
    }

    public void setMostViewList(List<MostView> mostViewList) {
        this.mostViewList = mostViewList;
    }

    public void setVievedList(ArrayList<MostView> mostViews) {
        this.mostViewList = mostViews;
    }


    public class MostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fileImage)
        ImageView fileImage;
        @BindView(R.id.rvManualName)
        TextView rvManualName;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.tv_count)
        TextView tv_count;


        public MostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
