package com.mahindra.be_lms.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

import com.mahindra.be_lms.R;


/**
 * Created by admin on 9/7/2016.
 */
public class ImageLoadingUtils {
    public Bitmap icon;
    private Context context;

    public ImageLoadingUtils(Context context) {
        this.context = context;
        icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    public int convertDipToPixels(float dips) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public Bitmap decodeBitmapFromPath(String filePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        scaledBitmap = BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;

        scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        return scaledBitmap;
    }

    public String decodeStringFromPath(String filePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        scaledBitmap = BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;

        scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        return ImageHelper.encodeToBase64(scaledBitmap, Bitmap.CompressFormat.PNG, 20);
    }


}

