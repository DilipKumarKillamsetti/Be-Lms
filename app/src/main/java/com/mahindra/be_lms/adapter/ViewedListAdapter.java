package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.MostViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/28/2017.
 */

public class ViewedListAdapter extends RecyclerView.Adapter<ViewedListAdapter.MostViewHolder > {

    private final List<MostViewModel> mostViewModels;
    private final Context context;
    private Callback mCallback;

    public ViewedListAdapter(Context context, List<MostViewModel> mostViewModels) {
        this.context = context;
        this.mostViewModels = mostViewModels;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public ViewedListAdapter.MostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.most_viewed_items, parent, false);
        return new ViewedListAdapter.MostViewHolder (view);
    }

    @Override
    public void onBindViewHolder(final ViewedListAdapter.MostViewHolder holder, final int position) {

        holder.rvFileName.setText(mostViewModels.get(position).getDocName());

    }

    @Override
    public int getItemCount() {
        return mostViewModels.size();
    }

    public class MostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rvFileName)
        TextView rvFileName;
        @BindView(R.id.fileImage)
        ImageView fileImage;

        public MostViewHolder (View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}
