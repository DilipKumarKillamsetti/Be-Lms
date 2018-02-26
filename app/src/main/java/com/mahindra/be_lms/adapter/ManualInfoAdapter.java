package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.ManualInfoModel;
import com.mahindra.be_lms.util.CommonFunctions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/26/2017.
 */

public class ManualInfoAdapter extends RecyclerView.Adapter<ManualInfoAdapter.CourseViewHolder> {

    private  List<ManualInfoModel> manualInfoModels;
    private final Context context;
    private MyCallback myCallback;

    public ManualInfoAdapter(Context context, List<ManualInfoModel> manualInfoModels) {
        this.context = context;
        this.manualInfoModels = manualInfoModels;
    }

    @Override
    public ManualInfoAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manual_infromation_items, parent, false);
        return new ManualInfoAdapter.CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManualInfoAdapter.CourseViewHolder holder, final int position) {

        //holder.courseName.setText(manualInfoModels.get(position).getFilePath());
        holder.iv_go.setVisibility(View.VISIBLE);
        holder.rvManualName.setText(manualInfoModels.get(position).getManualName());
        Drawable drawableIcon = null;
        String extension = CommonFunctions.getExtension( manualInfoModels.get(position).getFileType());
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
            Picasso.with(context).load( manualInfoModels.get(position).getFilePath()+"&token="+ MyApplication.mySharedPreference.getUserToken()).placeholder(R.drawable.icon_image).into(holder.fileImage);
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
        return manualInfoModels.size();
    }

    public void setManualList(ArrayList<ManualInfoModel> manualInfoModels) {

        this.manualInfoModels = manualInfoModels;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fileImage)
        ImageView fileImage;
        @BindView(R.id.rvManualName)
        TextView rvManualName;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.iv_go)
        ImageView iv_go;
        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}
