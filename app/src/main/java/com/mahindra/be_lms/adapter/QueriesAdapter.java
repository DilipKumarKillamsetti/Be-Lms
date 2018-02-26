package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.model.Queries;
import com.mahindra.be_lms.filter.CustomFilterQueries;
import com.mahindra.be_lms.interfaces.MyCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Pravin on 10/18/16.
 */
public class QueriesAdapter extends RecyclerView.Adapter<QueriesAdapter.QueriesViewHolder> implements Filterable {
    private final Context context;
    private final List<Queries> filterList;
    public List<Queries> queriesList;
    private MyCallback myCallback;
    private CustomFilterQueries filter;

    public QueriesAdapter(Context context, List<Queries> queriesList) {
        this.context = context;
        this.queriesList = queriesList;
        this.filterList = queriesList;
    }

    @Override
    public QueriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_query_layout, parent, false);
        return new QueriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueriesViewHolder holder, int position) {
        holder.tvQuerySubject.setText(queriesList.get(position).getQuery_subject());
        int statusColor;
        Drawable statusImage;
        if (queriesList.get(position).getStatus().equalsIgnoreCase("Completed")) {
            statusColor = context.getResources().getColor(R.color.green);
            statusImage = context.getResources().getDrawable(R.drawable.circle_green);
            holder.tvQueryStatus.setText("Completed");
        } else {
            statusColor = context.getResources().getColor(R.color.textColorPrimary);
            statusImage = context.getResources().getDrawable(R.drawable.circle_red);
            holder.tvQueryStatus.setText("Not Completed");
        }
        holder.tvQueryStatus.setTextColor(statusColor);
        holder.ivQueryStatus.setImageDrawable(statusImage);
        //holder.tvQueryResponse.setText(queriesList.get(position).getQuery_response());
    }

    @Override
    public int getItemCount() {
        return queriesList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilterQueries(this, filterList);
        }
        return filter;
    }

    public void setQueriesList(List<Queries> queriesList) {
        this.queriesList = queriesList;
    }

    public class QueriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvQuerySubject)
        TextView tvQuerySubject;
        @BindView(R.id.tvQueryStatus)
        TextView tvQueryStatus;
        @BindView(R.id.ivQueryStatus)
        ImageView ivQueryStatus;

        public QueriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.llQueryItem)
        public void clickQueryItem() {
            myCallback.myCallback(getAdapterPosition());
        }

    }
}
