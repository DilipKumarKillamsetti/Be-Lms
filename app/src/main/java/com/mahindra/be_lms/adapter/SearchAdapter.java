package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.model.SearchModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pravin on 11/7/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final Context context;
    private  List<SearchModel> searchModelList;
    private MyCallback myCallback;

    public SearchAdapter(Context context, List<SearchModel> searchModelList) {
        this.context = context;
        this.searchModelList = searchModelList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_search_result_layout, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
        holder.courseName.setText(searchModelList.get(position).getFullname());
        holder.categoryname.setText(searchModelList.get(position).getCategoryname());

        if(searchModelList.get(position).getSummary().contains("<")){
            if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
                holder.courseDescription.setText(Html.fromHtml(searchModelList.get(position).getSummary(), Html.FROM_HTML_MODE_COMPACT));

            }else{
                holder.courseDescription.setText(Html.fromHtml(searchModelList.get(position).getSummary()));

            }
        }else{
            holder.courseDescription.setText(searchModelList.get(position).getSummary());
        }
        holder.courseDescription.setMaxLines(1);
        holder.moreBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                holder.moreBtn.setVisibility(View.GONE);
                holder.hideBtn.setVisibility(View.VISIBLE);
                holder.courseDescription.setMaxLines(Integer.MAX_VALUE);
            }
        });
        holder.hideBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                holder.moreBtn.setVisibility(View.VISIBLE);
                holder.hideBtn.setVisibility(View.GONE);
                holder.courseDescription.setMaxLines(1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchModelList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public void setCourseList(List<SearchModel> searchModelList) {
        this.searchModelList = searchModelList;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.courseName)
        TextView courseName;
        @BindView(R.id.categoryname)
        TextView categoryname;
        @BindView(R.id.courseDescription)
        TextView courseDescription;
        @BindView(R.id.moreBtn)
        TextView moreBtn;
        @BindView(R.id.hideBtn)
        TextView hideBtn;


        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

//        @OnClick(R.id.llSearchLayout)
//        public void linearLayoutOnclick() {
//            myCallback.myCallback(getAdapterPosition());
//        }
    }
}
