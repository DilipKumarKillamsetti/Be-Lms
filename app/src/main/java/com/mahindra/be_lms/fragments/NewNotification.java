package com.mahindra.be_lms.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.PostNotificationAdapter;
import com.mahindra.be_lms.model.Notifications;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

/**
 * Created by Dell on 2/8/2018.
 */

public class NewNotification extends Fragment {

    @BindView(R.id.rvPostNotificationList)
    RecyclerView rvPostNotificationList;
    private PostNotificationAdapter postNotificationAdapter;
    private ArrayList<Notifications> notificationses;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_notification_post_layout, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    public void init(){

        notificationses = new ArrayList<>();
        for(int i=0;i<5;i++){
            Notifications notifications = new Notifications();
            notificationses.add(notifications);

        }
        rvPostNotificationList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPostNotificationList.setHasFixedSize(true);
        postNotificationAdapter = new PostNotificationAdapter(getActivity(), notificationses);
        //postNotificationAdapter.setCallback(this);
        rvPostNotificationList.setAdapter(postNotificationAdapter);

    }


}
