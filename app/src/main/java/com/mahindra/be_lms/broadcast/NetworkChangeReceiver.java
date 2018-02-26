package com.mahindra.be_lms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mahindra.be_lms.lib.NetworkUtil;

/**
 * Created by Chaitali on 10/18/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        //L.t("Status : "+status);

    }
}
