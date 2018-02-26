package com.mahindra.be_lms;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.DaoMaster;
import com.mahindra.be_lms.db.DaoSession;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.util.Constants;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Pravin .1-Oct-2016
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "MyApplication";
    public static boolean flagProfilePicSet = false;
    public static MySharedPreference mySharedPreference;
    private static MyApplication sInstance = null;
    private static DaoSession daoSession;
    private static boolean isActive;

    /*
      get instance of myapplication class
     */
    public static MyApplication getsInstance() {
        return sInstance;
    }

    /*
      get instance of application
    */
    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static boolean isActivityVisible() {
        return isActive;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        sInstance = this;
        registerActivityLifecycleCallbacks(this);
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getAppContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
        mySharedPreference = new MySharedPreference(sInstance);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Questrial-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed: ");
//        if (!mySharedPreference.checkUserLogin() && mySharedPreference.getNotificationType().equals("2")){
//            MyApplication.mySharedPreference.setNotificationType("");
//            startActivity(new Intent(activity, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        }
        isActive = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isActive = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

}
