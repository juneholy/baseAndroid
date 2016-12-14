package com.example.houlinjiang.baseandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.houlinjiang.baseandroid.constant.EventKey;
import com.example.houlinjiang.baseandroid.manager.EventManager;
import com.example.houlinjiang.baseandroid.manager.EventManager.OnEventObserver;

/**
 * Created by houlin.jiang on 2016/12/13.
 */

public abstract class BaseActivity<EntryInfo extends BaseEntryInfo> extends Activity implements OnEventObserver{

    public static final String FRAGMENT_PARAM_KEY = "FRAGMENT_PARAM_KEY";

    /** 该bundle是从其他\Activity传递过来,或者从销毁中恢复过来保存的数据 */
    protected Bundle mBundle;
    /** 外部携带的参数 */
    protected EntryInfo mFragmentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        mFragmentInfo = (EntryInfo) mBundle.getSerializable(FRAGMENT_PARAM_KEY);   //每个页面都需要入参
        if (mFragmentInfo == null) {
            finish();
            return;
        }
        if (!validateData()) {
            finish();
        } else {
            initView();
            if (hasRegister()) {
                EventManager.getInstance().register(EventKey.INVALIDATE, this);
            }
            refreshView();
            onActivityCreated();
        }
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container);

    /** 是否需要注册通用事件 */
    protected boolean hasRegister() {
        return false;
    }
    protected boolean validateData() {
        return true;
    }

    protected abstract void initView();

    protected abstract void refreshView();

    public void notifyChanged() {
        refreshView();
    }

    protected void onActivityCreated() {

    }
}
