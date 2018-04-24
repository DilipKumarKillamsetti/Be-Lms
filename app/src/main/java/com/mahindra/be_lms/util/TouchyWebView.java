package com.mahindra.be_lms.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Dell on 3/16/2018.
 */

public class TouchyWebView extends WebView {

    public TouchyWebView(Context context) {
        super(context);
    }

    public TouchyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}
