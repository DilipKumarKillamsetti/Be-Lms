package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.model.ApprovedProfile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chaitali on 10/5/16.
 */
public class ApprovedProfileAdapter extends RecyclerView.Adapter<ApprovedProfileAdapter.ApprovedViewHolder> {
    private final Context context;
    public List<ApprovedProfile> profileList;
    private MyCallback mCallback;

    public ApprovedProfileAdapter(Context context, List<ApprovedProfile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @Override
    public ApprovedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_approved_request_layout, parent, false);
        return new ApprovedViewHolder(view);
    }

    public void setmCallback(MyCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onBindViewHolder(final ApprovedViewHolder holder, int position) {
        holder.tvProfileName.setText(profileList.get(position).getProfileName());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }


    public class ApprovedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvProfileName)
        TextView tvProfileName;


        public ApprovedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //custom_approved_layout
        @OnClick(R.id.custom_approved_layout)

        public void clickRequestProfileItem() {
            mCallback.myCallback(getAdapterPosition());

        }

    }
}
