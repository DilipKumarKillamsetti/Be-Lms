package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Model;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pravin on 11/2/16.
 */
public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> {

    private final Context context;
    private final List<Model> modelList;
    private MyCallback myCallback;

    public ModelAdapter(Context context, List<Model> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_model_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ModelViewHolder holder, int position) {
        holder.tvModelName.setText(modelList.get(position).getModelName());
        L.l("Model Sequence: " + modelList.get(position).getModelSequence());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvModelName)
        TextView tvModelName;

        public ModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.llModelFragment)
        public void tvModelNameClick() {
            myCallback.myCallback(getAdapterPosition());
        }
    }
}
