package com.example.houlinjiang.baseandroid;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.houlinjiang.baseandroid.recovery.callback.RecoveryCallback;
import com.example.houlinjiang.baseandroid.recovery.core.Recovery;

/**
 * Created by houlin.jiang on 2016/12/13.
 */

public class MyApplication extends Application {
    private static Context mContext;
    private static String versionInfo;

    public MyApplication() {
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(mContext == null) {
            mContext = this;
        }

    }

    public Resources getResources() {
        return mContext != null && mContext != this?mContext.getResources():super.getResources();
    }

    public static Context getContext() {
        if(mContext == null) {
            throw new RuntimeException("WTF! you must extends QApplication !!! ");
        } else {
            return mContext;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("zxy", "Recovery: init");
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .skip(TestActivity.class)
                .init(this);
    }

    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            Log.e("zxy", "exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Log.e("zxy", "cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            Log.e("zxy", "exceptionClassName:" + exceptionType);
            Log.e("zxy", "throwClassName:" + throwClassName);
            Log.e("zxy", "throwMethodName:" + throwMethodName);
            Log.e("zxy", "throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }
}