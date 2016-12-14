package com.example.houlinjiang.baseandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

import com.example.houlinjiang.baseandroid.MyApplication;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lipan on 15/6/3.
 * 提供了一些列的对View的操作
 * 键盘隐藏和现实
 * 移除view,请求刷新view
 * 屏幕尺寸获取
 * tag设置和id生成
 * 触摸点相对view的坐标转换
 * View之间的坐标系转换
 * View和View之间的坐标系转换
 */
public class ViewUtil {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    /** 屏幕的宽度 */
    public static final int SCREEN_WIDTH;
    /** 屏幕的高度 */
    public static final int SCREEN_HEIGHT;
    static{
        DisplayMetrics dm = MyApplication.getContext().getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
    }

    public static int dip2px(float dip) {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, UIUtil.getContext().getResources().getDisplayMetrics()) + 0.5f);
    }

    /** 把自身从View中删除 */
    public static void removeSelfFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }

    /** 向上发起绘制请求,如果all为true,即使绘制正在进行,也能通知到顶层 */
    public static void requestLayoutParent(View view, boolean isAll) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof View) {
            if (!parent.isLayoutRequested()) {
                parent.requestLayout();
                if (!isAll) {
                    break;
                }
            }
            parent = parent.getParent();
        }
    }

    /** 判断触摸落点是否在view上 */
    public static boolean isTouchInView(MotionEvent ev, View v) {
        int[] vLoc = new int[2];
        v.getLocationOnScreen(vLoc);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();
        return motionX >= vLoc[0] && motionX <= (vLoc[0] + v.getWidth()) && motionY >= vLoc[1] && motionY <= (vLoc[1] + v.getHeight());
    }

    /** View之间的坐标系转换 */
    public static int[] getLocationInOtherView(View view, View other) {
        int[] vLoc = new int[2];
        int[] oLoc = new int[2];
        int[] loc = new int[2];
        view.getLocationOnScreen(vLoc);
        other.getLocationOnScreen(oLoc);
        loc[0] = vLoc[0] - oLoc[0];
        loc[1] = vLoc[1] - oLoc[1];
        return loc;
    }

    /** findView的泛型封装 */
    public static <T extends View> T findViewById(View layout, int id) {
        return (T) layout.findViewById(id);
    }

    /** 获取一个id */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /** 设置view的tag */
    public static void setTag(View view, int id, Object obj) {
        Object tag = view.getTag();

        SparseArray<Object> array = null;
        if (tag == null || !(tag instanceof SparseArray)) {
            array = new SparseArray<Object>();
        }
        if (tag != null && !(tag instanceof SparseArray)) {
            array.put(0, tag);
        }
        array.put(id, obj);
        view.setTag(array);
    }

    /** 获取View的tag */
    public static Object getTag(View view, int id) {
        Object tag = view.getTag();
        if (tag != null && tag instanceof SparseArray) {
            SparseArray<Object> array = (SparseArray<Object>) tag;
            return array.get(id);
        }
        if (id == 0) {
            return tag;
        }
        return null;
    }

    /** 获取View所在的Activity */
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            return (Activity) context;
        }
        ViewParent parent = view.getParent();
        if (parent  != null  && parent instanceof View) {
            return getActivity((View) parent);
        }
        return null;
    }

    /** 关闭键盘 */
    public static void hideSoftInput(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) UIUtil.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("UIUtil","showSoftInput",e);
        }
    }

    /** 打开键盘 */
    public static void showSoftInput(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) UIUtil.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        } catch (Exception e) {
            Log.e("UIUtil","hideSoftInput",e);
        }
    }

    /** 键盘是否打开 */
    public static boolean isSoftInputShown() {
        try {
            InputMethodManager imm = (InputMethodManager) UIUtil.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception e) {
            Log.e("UIUtil","isSoftInputShown",e);
        }
        return false;
    }

    /** 设置view是否可见 */
    public static void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    /** 设置view的子View是否可见 */
    public static void setChildVisibility(ViewGroup view, int visibility) {
        if (view != null) {
            for (int i = 0; i < view.getChildCount(); i++) {
                view.getChildAt(i).setVisibility(visibility);
            }
        }
    }

    public static void measure(View view) {
        LayoutParams layoutParams = view.getLayoutParams();
        int widthMeasureSpec;
        int heightMeasureSpec;

        if (layoutParams == null || layoutParams.width < 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(ViewUtil.SCREEN_WIDTH, MeasureSpec.AT_MOST);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        }

        if (layoutParams == null || layoutParams.height < 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(ViewUtil.SCREEN_HEIGHT, MeasureSpec.AT_MOST);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        }
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    public static Bitmap draw(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
//        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /** 根据第一个view是否可见来设置第二个view的padding */
    public static void setBottomMargin(View view, ViewParent parent) {
        if (parent != null && parent instanceof View) {
            View parentView = (View) parent;
            MarginLayoutParams layoutParams = null;
            if (parentView.getLayoutParams() instanceof MarginLayoutParams) {
                layoutParams = (MarginLayoutParams)parentView.getLayoutParams();
            }
            if (layoutParams == null) {
                return;
            }
            if (view.getVisibility() == View.VISIBLE) {
                layoutParams.setMargins(0, 0, 0, 0);
            } else {
                layoutParams.setMargins(0, 0, 0, UIUtil.dip2px(10));
            }
        }
    }
}
