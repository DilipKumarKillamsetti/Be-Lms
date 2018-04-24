package com.mahindra.be_lms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by android7 on 10/5/16.
 */

public class DateManagement {
    //    2016-12-10
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    //    10-12-2016
    public static final String DD_MM_YYYY = "dd-MM-yy";
    //    10-December-2016
    public static final String DD_MMMM_YYYY = "dd-MMMM-yyyy";

    public static String getFormatedDate(String strDate, String sourceFormat,
                                         String destinyFormat) {
        SimpleDateFormat df;
        df = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        df = new SimpleDateFormat(destinyFormat, Locale.getDefault());
        return df.format(date);

    }

    public static Date getStrToDate(String strDate, String sourceFormat) {
        SimpleDateFormat df;
        df = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int YEAR = cal.get(Calendar.YEAR);
        int MONTH = cal.get(Calendar.MONTH) + 1;
        int DAY_OF_MONTH = cal.get(Calendar.DAY_OF_MONTH);
        return DAY_OF_MONTH + "-" + MONTH + "-" + YEAR;
    }

    public static int compareDates(String d1,String d2)
    {
        int status = 0;
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            if(date1.after(date2)){

                status = 1;
            }

            if(date1.before(date2)){
                status = 2;
            }

            if(date1.equals(date2)){
                status = 3;
            }

        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
        return status;
    }

}