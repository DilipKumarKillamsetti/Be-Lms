package com.mahindra.be_lms.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.CommonFunctions;

import java.io.File;

/**
 * Created by Chaitali on 10/18/16.
 * Broadcast Receiver for download manager
 */

public class DownloadReceiver extends BroadcastReceiver {
    DownloadManager down_m;
    String filename = "";
    String filePath = "";
    int status;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        L.l("receiver action : " + action);
        //check if the broadcast message is for our enqueued download
        final long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
        L.l("Receiver DownID: " + referenceId);
        down_m = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            //L.t("File download completed..!!");
            /*new AlertDialog.Builder(MyApplication.getAppContext())
                    .setTitle("File download completed")
                    .setMessage("Do you want open this file?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(referenceId);
                            Cursor c = down_m.query(query);
                            if (c.moveToFirst()) {
                                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                    filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                                    L.l("Receiver filename: " + filePath);
                                    String filenameArray[] = filename.split("\\.");
                                    String extension = filenameArray[filenameArray.length - 1];
                                    L.l("Receiver filename extension: " + extension);
                                }
                            }
                            c.close();
                            if (!filePath.isEmpty()){
                                try {
                                    new CommonFunctions().openFile(context, filePath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();*/

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(referenceId);
            Cursor c = down_m.query(query);
            if (c.moveToFirst()) {
                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String downloadFilePath = null;
                    downloadFilePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (downloadFilePath != null) {
                        File mFile = new File(Uri.parse(downloadFilePath).getPath());
                        filePath = mFile.getAbsolutePath();
                    }
                    filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                    L.l("Receiver filename: " + downloadFilePath);
                    String filenameArray[] = filename.split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    L.l("Receiver filename extension: " + extension);
                }
            }
            c.close();
            L.t_long("File download completed..!!");
            if (!filePath.isEmpty()) {
                L.t_long("File download completed..!!");
                new CommonFunctions().openFile(context, filePath);
            } else {
                new CommonFunctions().CheckStatus(down_m, referenceId);
            }

        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
            //IN THIS SECTION YOU CAN WRITE YOUR LOGIC TO CANCEL DOWNLOAD AS STATED IN ABOVE ANSWER
            // new CommonFunctions().CheckStatus(down_m, referenceId);
            down_m.remove(referenceId);
        }
    }
}
