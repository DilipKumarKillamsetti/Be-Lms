package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.model.HomeMenu;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pravin on 11/22/16.
 */
public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.HomeMenuViewHolder> {
    private final Context context;
    private List<HomeMenu> homeMenuList;
    private MyCallback myCallback;

    public HomeMenuAdapter(Context context, List<HomeMenu> homeMenuList) {
        this.context = context;
        this.homeMenuList = homeMenuList;
    }

    @Override
    public HomeMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeMenuViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_home_menu_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeMenuViewHolder holder, int position) {
        holder.ll_home_menu.setBackgroundDrawable(context.getResources().getDrawable(homeMenuList.get(position).getBack_color()));
        holder.tvHomeMenuName.setText(homeMenuList.get(position).getMenuName());
        holder.ivHomeMenuIcon.setImageDrawable(context.getResources().getDrawable(homeMenuList.get(position).getMenuIcon()));
    }

    @Override
    public int getItemCount() {
        return homeMenuList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public void setHomeMenuList(List<HomeMenu> homeMenuList) {
        this.homeMenuList = homeMenuList;
    }

    public class HomeMenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivHomeMenuIcon)
        ImageView ivHomeMenuIcon;
        @BindView(R.id.tvHomeMenuName)
        TextView tvHomeMenuName;
        @BindView(R.id.ll_home_menu)
        LinearLayout ll_home_menu;

        public HomeMenuViewHolder(View itemView) {
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
