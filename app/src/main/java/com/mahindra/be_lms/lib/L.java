package com.mahindra.be_lms.lib;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;


/**
 * Created by Pravin .1-Oct-2016
 */

public class L {
    private static final String MYTAG = "mytag";
        private static AlertDialog.Builder builder;
    private static ProgressDialog dialog;

    public static void t(String txt) {
        Toast.makeText(MyApplication.getAppContext(), txt, Toast.LENGTH_SHORT).show();
    }

    public static void t_long(String txt) {
        Toast.makeText(MyApplication.getAppContext(), txt, Toast.LENGTH_LONG).show();
    }


    public static void l(Context context, String txt) {
        if (txt.length() > 0) {
            Log.d(context.getClass().getSimpleName(), txt);
        }
    }

    public static void l(String txt) {
        if (txt.length() > 0) {
            Log.d(MYTAG, txt);
        }
    }

    public static void l(String tag, String txt) {
        if (txt.length() > 0) {
            Log.d(tag, txt);
        }
    }

    public static void t_top(String txt) {
        Toast toast = Toast.makeText(MyApplication.getAppContext(), txt,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();
    }

    public static void pd(String msg, Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public static void pd(String title, String msg, Context context) {
        dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public static void pd(String title, String msg, int icon, Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setIcon(icon);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public static void custom_pd( String msg, Context context) {
        builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = context.getLayoutInflater();
//        alertDialog.setContentView(inflater.inflate(R.layout.custom_alert_view, null));
        dialog.getWindow().setContentView(R.layout.custom_alert_view);
        View view = LayoutInflater.from(context).inflate(
                R.layout.custom_alert_view, null);
        dialog = new ProgressDialog(context);
        dialog.getWindow().setContentView(view);

        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public static void dismiss_pd() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static boolean isDialogVisible() {
        return dialog.isShowing();
    }

    /**
     * check network connectivity
     * network available return true
     * network not available return false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showMessageDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(MyApplication.getAppContext().getString(android.R.string.ok), null)
                .show();
    }


    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    public static String getText(RadioButton radioButton) {
        return radioButton.getText().toString().trim();
    }

    public static String getText(CheckBox checkBox) {
        return checkBox.getText().toString().trim();
    }

    public static String getText(Spinner spinner) {
        return spinner.getSelectedItem().toString().trim();
    }

    public static String checkNull(String txt, String defaultValue) {
        // return txt != null ? txt.equalsIgnoreCase("null") ? defaultValue : txt : TextUtils.isEmpty(txt) ? defaultValue : txt;
        return TextUtils.isEmpty(txt) ? defaultValue : txt.equalsIgnoreCase("null") ? defaultValue : txt;
    }

    public static boolean checkNull(String txt) {
        return txt == null || (TextUtils.isEmpty(txt) || txt.equalsIgnoreCase("null"));
    }

    public static String hideMobileDigits(String mobileNumber) {
        return "xxxxxxx" + mobileNumber.substring(7, 10);
    }

}
