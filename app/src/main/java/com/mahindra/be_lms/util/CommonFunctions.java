package com.mahindra.be_lms.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahindra.be_lms.BuildConfig;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.constant.FileType;
import com.mahindra.be_lms.lib.L;

import java.io.File;

/**
 * Created by Chaitali on 9/23/16.
 */

public class CommonFunctions {
    //Method to get app current version code
    public static int getAppVersionCode() {
        int versionCode = BuildConfig.VERSION_CODE;
        Log.e("version", "code: " + versionCode);
        return versionCode;
    }

    //Method to get app current version name
    public static String getAppVersionName() {
        String versionName = BuildConfig.VERSION_NAME;
        Log.e("version", "Name: " + versionName);
        return versionName;
    }

    //Method to get device device name
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }

        return capitalize(manufacturer) + " " + model;

    } //Method to get device device name

    public static String getDeviceIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) MyApplication.getAppContext().getSystemService(MyApplication.getAppContext().TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

//        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
//                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
//            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static boolean checkDocumentAllreaddyExist(String documentName) {
        File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), documentName);
        L.l("DOCUMENT FILE PATH: " + file.getAbsolutePath());
        if (file.exists()) {
            L.l("DOCUMENT FILE PATH AVAILABLE");
            return true;
        }
        return false;
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    /*
    * Functions to download files
    * */

    public static String EncryptString(String strToEncrypt) {
        Encryption encryption = Encryption.getDefault(Constants.SECERET_KEY, "Salt", new byte[16]);
        String encrypted = encryption.encryptOrNull(strToEncrypt);
        L.l("Encrypted String: " + encrypted);
        return encrypted;
    }

    public static String DecryptString(String strToDecrypt) {
        L.l("STRING TO DECRYPT: " + strToDecrypt);
        Encryption encryption = Encryption.getDefault(Constants.SECERET_KEY, "Salt", new byte[16]);
        String decrypted = encryption.decryptOrNull(strToDecrypt);
        L.l("Decrypted String: " + decrypted);
        return decrypted;
    }

    public static int getFileSize(File file) {
        int sizeKB = (int) (file.length() / 1024);
        L.l("file Size kb: " + sizeKB);
        int sizeMB = sizeKB / 1024;
        L.l("file Size kb: " + sizeMB);
        return sizeMB;
    }

    public static int checkFileType(String extension) {
        int fileType = 0;
        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("JPE")
                || extension.equalsIgnoreCase("IMG")
                || extension.equalsIgnoreCase("GIF")
                || extension.equalsIgnoreCase("PSD")
                || extension.equalsIgnoreCase("jpeg")) {
            fileType = FileType.IMAGE;
        } else if (extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("mpeg")
                || extension.equalsIgnoreCase("flv")
                || extension.equalsIgnoreCase("avi")
                || extension.equalsIgnoreCase("mov")
                || extension.equalsIgnoreCase("mpg")
                || extension.equalsIgnoreCase("wmv") || extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("swf")) {
            fileType = FileType.VIDEO;
        } else if (extension.equalsIgnoreCase("html")) {
            fileType = FileType.HTML;
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            fileType = FileType.EXCEL;
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            fileType = FileType.WORD;
        } else if (extension.equalsIgnoreCase("pdf")) {
            fileType = FileType.PDF;
        }
        return fileType;
    }
     /*
    * End of function to check status of downloading file
    * */

    /*
    * Function to open different types of files
    * */

    //Function to set actionbar title gravity
    public void ActionBarTitleGravity(Context context, ActionBar myactionbar, String titletext) {
        // TODO Auto-generated method stub
        TextView textview = new TextView(context);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutparams);
        textview.setText(titletext);
        textview.setTextSize(20);
        textview.setTextColor(Color.WHITE);
        textview.setGravity(Gravity.CENTER);
        myactionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        myactionbar.setCustomView(textview);
    }

    //Method to hide keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long DownloadData(Context context, DownloadManager downloadManager, Uri uri, String filename) {

        long downloadReference;

        // Create request for android download manager
        //downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(filename);

        //Setting description of request
        request.setDescription(context.getString(R.string.downloading) + filename);

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    /*
    * Function to check status of downloading file
    * */
    public void CheckStatus(DownloadManager downloadManager, long downloadId) {

        DownloadManager.Query downloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        downloadQuery.setFilterById(downloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(downloadQuery);
        if (cursor.moveToFirst()) {
            DownloadStatus(cursor, downloadId);
        }

    }

    private void DownloadStatus(Cursor cursor, long DownloadId) {

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }
        if (statusText.equalsIgnoreCase("STATUS_FAILED")) {
            L.t_long("Failed to open document.. Error to download document");
        } else {
            L.t_top(statusText + " \n" + reasonText);
        }
    }

    public void openFile(Context context, String url) {
        L.l("In open File");
        File file = new File(url);
        Uri uri = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.contains(".doc") || url.contains("." +
                "docx")) {
            L.l("In open File doc");
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(".pdf")) {
            L.l("In open File PDF");
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            L.l("In open File EXCEL");
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //  context.startActivity(intent);
        try {
            L.l("In open File startact");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            L.t("No application available to view this");
            e.printStackTrace();
            // Toast.makeText(OpenPdf.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }
    public  static  String getFileName(String url){
        return url.substring( url.lastIndexOf('/')+1, url.length() );
    }

}
