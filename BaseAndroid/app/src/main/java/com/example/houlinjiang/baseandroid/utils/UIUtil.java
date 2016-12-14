package com.example.houlinjiang.baseandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.houlinjiang.baseandroid.MyApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lipan on 15/10/22.
 * 提供全局的context和资源获取
 * 提供了dp/px转换
 * 提供了向主线程发送消息的函数,并提供和主线程并行的线程运行短耗时任务
 */
public class UIUtil {
    public static final int MAIN_THREAD_MAX = 50;
    public static final int SUB_THREAD_MAX = 1000;
    private static final String SUB_THREAD_NAME = "qunar_sub_thread";
    private static Method sQueueNext;
    private static MessageQueue sQueue;
    private static Field sTarget;
    private static HandlerThread mSubThread;
    private static Handler mMainHandler;
    private static Handler mSubHandler;
    private static Map<Runnable, Runnable> mRunnableMainMap;
    private static Map<Runnable, Runnable> mRunnableSubMap;

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static Thread getMainThread() {
        return Looper.getMainLooper().getThread();
    }

    /** 获取辅助线程 */
    public static synchronized HandlerThread getSubThread() {
        if (mSubThread == null) {
            mSubThread = new HandlerThread(SUB_THREAD_NAME);
            mSubThread.start();
        }
        return mSubThread;
    }

    /** 判断是否处于主线程 */
    public static boolean isRunInMainThread() {
        return Thread.currentThread() == getMainThread();
    }

    /** 判断是否处于辅助线程 */
    public static boolean isRunInSubThread() {
        return Thread.currentThread() == getSubThread();
    }

    /** 获取主线程的Handler,此Handler是全局唯一的,可有效防止Handler生命周期过程造成内存泄露 */
    public static synchronized Handler getMainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    /** 获取辅助线程的Handler */
    public static synchronized Handler getSubHandler() {
        if (mSubHandler == null) {
            mSubHandler = new Handler(getSubThread().getLooper());
        }
        return mSubHandler;
    }

    /** 发送任务到主线程,即使当前处于主线程,任务也不会立马执行,而是被放到消息队列中 */
    public static boolean postToMain(Runnable runnable) {
        return postToMain(runnable, 0);
    }

    /** 发送一个延迟任务到主线程消息队列中 */
    public static boolean postToMain(final Runnable runnable, long delayMillis) {
        boolean test = false;
        if (test) {
            return getMainHandler().postDelayed(runnable, delayMillis);
        } else {
            if (mRunnableMainMap == null) {
                mRunnableMainMap = new HashMap<>();
            }
            Runnable wrapper = new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    runnable.run();
                    mRunnableMainMap.remove(runnable);
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime > MAIN_THREAD_MAX) {
                        showShortToast("主线程任务耗时:" + (endTime - startTime) + "  in " + runnable);
                        Log.e("耗时", "主线程任务耗时:" + (endTime - startTime) + "  in " + runnable);
                    }
                }
            };
            mRunnableMainMap.put(runnable, wrapper);
            return getMainHandler().postDelayed(wrapper, delayMillis);
        }
    }

    /** 在主线程中运行当前任务,如果当前是处于主线程中,则立马执行,如果不是,则发送到主线程的消息队列中 */
    public static void runInMain(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }

    /** 从主线程的消息队列中移除指定的任务 */
    public static void removeFromMain(Runnable runnable) {
        if (mRunnableMainMap != null && mRunnableMainMap.containsKey(runnable)) {
            getMainHandler().removeCallbacks(mRunnableMainMap.get(runnable));
            mRunnableMainMap.remove(runnable);
        } else {
            getMainHandler().removeCallbacks(runnable);
        }
    }

    /** 发送任务到辅助线程的消息队列中 */
    public static boolean postToSub(Runnable runnable) {
        return postToSub(runnable, 0);
    }

    /** 发送一个延迟任务到辅助线程的消息队列中 */
    public static boolean postToSub(final Runnable runnable, long delayMillis) {
        boolean test = false;
        if (test) {
            return getSubHandler().postDelayed(runnable, delayMillis);
        } else {
            if (mRunnableSubMap == null) {
                mRunnableSubMap = new HashMap<>();
            }
            Runnable wrapper = new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    runnable.run();
                    mRunnableSubMap.remove(runnable);
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime > SUB_THREAD_MAX) {
                        showShortToast("辅助线程任务耗时:" + (endTime - startTime) + "  in " + runnable);
                        Log.e("耗时", "主线程任务耗时:" + (endTime - startTime) + "  in " + runnable);
                    }
                }
            };
            mRunnableSubMap.put(runnable, wrapper);
            return getSubHandler().postDelayed(wrapper, delayMillis);
        }
    }

    /** 在辅助线程中运行当前任务 */
    public static void runInSub(Runnable runnable) {
        if (isRunInSubThread()) {
            runnable.run();
        } else {
            postToSub(runnable);
        }
    }

    /** 从辅助线程中移除任务 */
    public static void removeFromSub(Runnable runnable) {
        if (mRunnableSubMap != null && mRunnableSubMap.containsKey(runnable)) {
            getSubHandler().removeCallbacks(mRunnableSubMap.get(runnable));
            mRunnableSubMap.remove(runnable);
        } else {
            getSubHandler().removeCallbacks(runnable);
        }
    }

    /** dip转换成px */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static int dip2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /** dip转换成px */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /** px转换成dip */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /** 填充布局 */
    public static View inflate(int resId) {
        return View.inflate(getContext(), resId, null);
    }

    /** 获取app的resource */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /** 获取app的resource */
    public static Resources getResources(Context context) {
        if (context != null) {
            return context.getResources();
        }
        return getContext().getResources();
    }

    /** 获取字符串资源 */
    public static String getString(int resId) {
        return getString(null, resId);
    }

    /** 获取字符串资源 */
    public static String getString(Context context, int resId) {
        return getResources(context).getString(resId);
    }

    /** 获取字符串数组资源 */
    public static String[] getStringArray(int resId) {
        return getStringArray(null, resId);
    }

    /** 获取字符串数组资源 */
    public static String[] getStringArray(Context context, int resId) {
        return getResources(context).getStringArray(resId);
    }

    /** 获取dimen资源 */
    public static int getDimens(int resId) {
        return getDimens(null, resId);
    }

    /** 获取dimen资源 */
    public static int getDimens(Context context, int resId) {
        return getResources(context).getDimensionPixelSize(resId);
    }

    /** 获取图片资源 */
    public static Drawable getDrawable(int resId) {
        return getDrawable(null, resId);
    }

    /** 获取图片资源 */
    public static Drawable getDrawable(Context context, int resId) {
        return getResources(context).getDrawable(resId);
    }

    /** 获取颜色资源 */
    public static int getColor(int resId) {
        return getColor(null, resId);
    }

    /** 获取颜色资源 */
    public static int getColor(Context context, int resId) {
        return getResources(context).getColor(resId);
    }

    /** 获取颜色选择器 */
    public static ColorStateList getColorStateList(int resId) {
        return getColorStateList(null, resId);
    }

    /** 获取颜色选择器 */
    public static ColorStateList getColorStateList(Context context, int resId) {
        return getResources(context).getColorStateList(resId);
    }

    public static void showLongToast(final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        runInMain(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UIUtil.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showShortToast(final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        runInMain(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UIUtil.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 执行主线程消息队列中的其它任务 */
    public static void runOtherMessageOnMainThread() throws Exception {
        if (sQueue == null) {
            sQueue = Looper.getMainLooper().myQueue();
        }

        if (sQueueNext == null) {
            sQueueNext = MessageQueue.class.getDeclaredMethod("next");
            sQueueNext.setAccessible(true);
        }

        if (sTarget == null) {
            sTarget = Message.class.getDeclaredField("target");
            sTarget.setAccessible(true);
        }

        Message msg = (Message) sQueueNext.invoke(sQueue);
        Handler handler = (Handler) sTarget.get(msg);
        handler.dispatchMessage(msg);
        msg.recycle();
    }
}
