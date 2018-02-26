package com.mahindra.be_lms.adapter;

/**
 * Created by training1 on 11/10/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Profile;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Pravin on 10/18/16.
 */
public class RequestProfileAdapter extends RecyclerView.Adapter<RequestProfileAdapter.ProfilesViewHolder> {
    private final Context context;
    public List<Profile> profileList;
    private Callback myCallback;//,myCallback1;
    private String profile_id;
    // private CustomFilterQueries filter;

    public RequestProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @Override
    public ProfilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_request_profile_layout, parent, false);
        return new ProfilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfilesViewHolder holder, int position) {
        holder.tvRequestProfilename.setText(profileList.get(position).getProfileName());
        L.l("profile_id adapter: " + profile_id);

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setMyCallback(Callback myCallback) {
        this.myCallback = myCallback;
    }

    public class ProfilesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvRequestProfilename)
        TextView tvRequestProfilename;


        public ProfilesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.llRequestProfileItem)
        public void clickRequestProfileItem() {
            myCallback.myCallback(getAdapterPosition(), profileList.get(getAdapterPosition()).getProfileID());

        }

    }
}
