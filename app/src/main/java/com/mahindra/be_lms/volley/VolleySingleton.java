package com.mahindra.be_lms.volley;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mahindra.be_lms.MyApplication;

import java.util.Map;

/**
 * Created by Pravin on 10/21/16.
 */
public class VolleySingleton {
    public static final String TAG = "VolleyPatterns";
    private static VolleySingleton sInstance = null;
    private static RequestQueue mRequestQueue;
    private static ImageLoader imageLoader;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public static VolleySingleton getsInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static Map<String, String> checkRequestparam(Map<String, String> param) {
        for (String key : param.keySet()) {
            param.put(key, TextUtils.isEmpty(param.get(key)) ? "" : param.get(key));
        }
        return param;
    }

    private RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getmRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getmRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
