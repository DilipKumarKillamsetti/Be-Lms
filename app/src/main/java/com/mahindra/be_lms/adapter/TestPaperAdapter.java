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
import com.mahindra.be_lms.model.TestPaper;
import com.mahindra.be_lms.util.CommonFunctions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chaitali on 10/8/16.
 */
public class TestPaperAdapter extends RecyclerView.Adapter<TestPaperAdapter.MyTestPaperViewHolder> {
    private final Context context;
    private final List<TestPaper> testPaperList;
    MyCallback myCallback;

    public TestPaperAdapter(Context context, List<TestPaper> testPaperList) {
        this.context = context;
        this.testPaperList = testPaperList;
    }

    @Override
    public MyTestPaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_testpaper_layout, parent, false);
        return new MyTestPaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTestPaperViewHolder holder, int position) {
        String filname = testPaperList.get(position).getTestpaper_title();
        int pos = filname.lastIndexOf(".");
        if (pos > 0) {
            filname = filname.substring(0, pos);
        }
        L.l(context, "filename: " + filname);
        holder.tvTestPaperTitle.setText(filname);
        // holder.tvTestPaperDesc.setText(testPaperList.get(position).getTestpaper_desc());
        // L.l("test type: "+testPaperList.get(position).getTestpaper_type());
        //L.l("test url: "+testPaperList.get(position).getTestpaper_url());
        Drawable drawableIcon = null;
        /*if (testPaperList.get(position).getTestpaper_type().equals(context.getString(R.string.pdf_type))){
            drawableIcon=context.getResources().getDrawable(R.drawable.icon_pdf);
        }else if (testPaperList.get(position).getTestpaper_type().equals(context.getString(R.string.video_type))){
            drawableIcon=context.getResources().getDrawable(R.drawable.icon_video);
        }else if (testPaperList.get(position).getTestpaper_type().equals(context.getString(R.string.excel_type))){
            drawableIcon=context.getResources().getDrawable(R.drawable.icon_xls);
        }else if (testPaperList.get(position).getTestpaper_type().equals(context.getString(R.string.doc_type))){
            drawableIcon=context.getResources().getDrawable(R.drawable.icon_doc);
        }else if (testPaperList.get(position).getTestpaper_type().equals(context.getString(R.string.html_type))){
            drawableIcon=context.getResources().getDrawable(R.drawable.icon_html);
        }*/
        String extension = CommonFunctions.getExtension(testPaperList.get(position).getTestpaper_title());
        L.l("File Extension : " + extension);
        if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_xls);
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_doc);
        } else if (extension.equalsIgnoreCase("pdf")) {
            drawableIcon = context.getResources().getDrawable(R.drawable.icon_pdf);
        } else {
            drawableIcon = context.getResources().getDrawable(R.drawable.unknown_icon);
        }
        holder.ivTestPaperIcon.setImageDrawable(drawableIcon);
    }

    @Override
    public int getItemCount() {
        return testPaperList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public class MyTestPaperViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTestPaperTitle)
        TextView tvTestPaperTitle;
        @BindView(R.id.tvTestPaperDesc)
        TextView tvTestPaperDesc;
        @BindView(R.id.ivTestPaperIcon)
        ImageView ivTestPaperIcon;

        public MyTestPaperViewHolder(View itemView) {
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
