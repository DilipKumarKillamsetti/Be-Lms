package com.mahindra.be_lms.lib;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pravin on 26-09-2016
 */
public class MyValidator {
    private static final String REQUIRED_MSG = "Field required";

    /*
     validating email id
     */
    public static boolean isValidEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_email));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_valid_email));
            return false;
        }
        editText.setError(null);
        return true;
    }

    public static boolean isValidPasswordPattern(EditText editText) {
        String password = editText.getText().toString().trim();
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        if(!matcher.matches()){
            setError(editText, MyApplication.getAppContext().getString(R.string.err_password_pattern));
            return false;
        }
        return matcher.matches();

    }

    /*
    validating password
    */
    public static boolean isValidPassword(EditText editText) {
        String pass = editText.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_password));
            return false;
        } else if (pass.length() < 4) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_password_length));
            return false;
        }
        editText.setError(null);
        return true;
    }

    /*
    validating password and confirmpassword
    */
    public static boolean isValidConfirmPassword(EditText newpassword, EditText confirmpassword) {
        String newpass = newpassword.getText().toString().trim();
        String confirmpass = confirmpassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmpass)) {
            setError(confirmpassword, MyApplication.getAppContext().getString(R.string.err_enter_confirm_password));
            return false;
        } else if (!newpass.equals(confirmpass)) {
            setError(confirmpassword, MyApplication.getAppContext().getString(R.string.err_password_confirpassword_not_match));
            return false;
        }
        confirmpassword.setError(null);
        return true;
    }

    /*
     validating EditText empty value without custom msg
    */
    public static boolean isValidField(EditText editText) {
        String txtValue = editText.getText().toString().trim();
        if (TextUtils.isEmpty(txtValue)) {
            setError(editText, REQUIRED_MSG);
            return false;
        }
        editText.setError(null);
        return true;
    }

    /*
    validating TextView empty value without custom msg
    */
    public static boolean isValidField(TextView textView) {
        String txtValue = textView.getText().toString().trim();
        if (TextUtils.isEmpty(txtValue)) {
            setError(textView, REQUIRED_MSG);
            return false;
        }
        textView.setError(null);
        return true;
    }

    /*
    validating EditText empty value with custom msg
    */
    public static boolean isValidField(EditText editText, String msg) {
        String txtValue = editText.getText().toString().trim();
        if (TextUtils.isEmpty(txtValue)) {
            setError(editText, msg);
            return false;
        }
        editText.setError(null);
        return true;
    }

    /*
    validating autocompleteTextView empty value with custom msg
    */
    public static boolean isValidField(AutoCompleteTextView editText, String msg) {
        String txtValue = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(txtValue)) {
            //setError(editText, msg);
            return false;
        }
        editText.setError(null);
        return true;
    }

    /*
    validating TextView empty value with custom msg
    */
    public static boolean isValidField(TextView textView, String msg) {
        String txtValue = textView.getText().toString().trim();
        if (TextUtils.isEmpty(txtValue)) {
            setError(textView, msg);
            return false;
        }
        textView.setError(null);
        return true;
    }

    /*
    validating Spinner empty value without custom msg
    */
    public static boolean isValidSpinner(Spinner spinner) {
        View view = spinner.getSelectedView();
        TextView textView = (TextView) view;
        if (spinner.getSelectedItemPosition() == 0) {
            setError(textView, MyApplication.getAppContext().getString(R.string.err_sp_none_selected));
            return false;
        }
        textView.setError(null);
        return true;
    }

    /**
     * validating Mobile Number value without custom msg
     */
    public static boolean isValidMobile(EditText editText) {
        String mob = editText.getText().toString().trim();
        if (TextUtils.isEmpty(mob)) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_mobile));
            return false;
        } else if (mob.length() != 10) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_mobile_length));
            return false;
        }
        editText.setError(null);
        return true;
    }

    /**
     * validating Mobile Number with +91 value without custom msg
     */
    public static boolean isValidIntenationalMobile(String mobileno, EditText editText, EditText editText1) {
        String countrycode = editText.getText().toString();
        String mobile_no = editText1.getText().toString();
        if (TextUtils.isEmpty(countrycode)) {
            MyValidator.setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_country_code));
            return false;
        } else if (!countrycode.matches("[+][0-9]+")) {
            MyValidator.setError(editText, "Enter valid country code");
            return false;
        } else if (TextUtils.isEmpty(mobile_no)) {
            MyValidator.setError(editText1, MyApplication.getAppContext().getString(R.string.err_enter_mobile));
            return false;
        } else if (!mobileno.startsWith("+")) {
            MyValidator.setError(editText, MyApplication.getAppContext().getString(R.string.err_country_code_plus_sign));
            return false;
        } else if (!isValidMobile(editText1)) {
            return false;
        }
        return true;
    }

    /*
    validating Unique ID value without custom msg
    */
    public static boolean isValidUniqueID(EditText editText) {
        String unique_id = editText.getText().toString().trim();
        if (TextUtils.isEmpty(unique_id)) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_unique_id));
            return false;
        } else if (unique_id.length() > 10) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_uniqueid_length));
            return false;
        }
        editText.setError(null);
        return true;
    }

    public static boolean isValidUserName(EditText editText) {
        String unique_id = editText.getText().toString().trim();
        if (TextUtils.isEmpty(unique_id)) {
            setError(editText, MyApplication.getAppContext().getString(R.string.err_enter_username));
            return false;
        }
        editText.setError(null);
        return true;
    }

    /*
    Set Error and Focus on EditText
    */
    public static void setError(EditText editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }

    /*
   Set Error and Focus on autocompletTextview
   */
    public static void setError(AutoCompleteTextView editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }


    /*
    Set Error and Focus on TextView
    */
    public static void setError(TextView textView, String msg) {
        textView.setError(msg);
        textView.requestFocus();
    }



}
