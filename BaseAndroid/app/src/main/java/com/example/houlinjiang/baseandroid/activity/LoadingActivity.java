package com.example.houlinjiang.baseandroid.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.houlinjiang.baseandroid.R;
import com.example.houlinjiang.baseandroid.utils.UIUtil;
import com.example.houlinjiang.baseandroid.utils.ViewUtil;

/**
 * Created by houlin.jiang on 2016/12/13.
 */

public abstract class LoadingActivity<Param extends BaseEntryInfo> extends BaseActivity<Param> {

    public static final int STATE_SUCCESS = 1;
    public static final int STATE_NET_FAIL = 2;
    public static final int STATE_LOADING = 5;
    public static final int STATE_NO_DATA = 9;

    protected View mLoadingView = null;
    protected View mSuccessView = null;
    protected View mNetFailView = null;
    protected View mNoDataView = null;

    private int mState = STATE_LOADING;
    protected FrameLayout mLoadingRootView = null;


    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        mLoadingRootView = new FrameLayout(UIUtil.getContext());

        mLoadingView = createLoadingView(inflater, container);
        mNetFailView = createNetFailView(inflater, container);
        mNoDataView = createNoDataView(inflater, container);
        mSuccessView = createSuccessView(inflater, container);

        mLoadingRootView.addView(mLoadingView, ViewUtil.MATCH_PARENT, ViewUtil.MATCH_PARENT);
        mLoadingRootView.addView(mNetFailView, ViewUtil.MATCH_PARENT, ViewUtil.MATCH_PARENT);
        mLoadingRootView.addView(mNoDataView, ViewUtil.MATCH_PARENT, ViewUtil.MATCH_PARENT);
        mLoadingRootView.addView(mSuccessView, ViewUtil.MATCH_PARENT, ViewUtil.MATCH_PARENT);

        setViewShown(STATE_LOADING);

        return mLoadingRootView;
    }
    protected View createLoadingView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_loading, null);
        return view;
    }

    protected View createNetFailView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_net_fail, null);
        view.findViewById(R.id.bt_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewShown(STATE_LOADING);
                onNetFailClick();
            }
        });
        return view;
    }

    protected View createNoDataView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_no_data, null);
        view.findViewById(R.id.bt_change).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoDataClick();
            }
        });
        return view;
    }

    protected abstract View createSuccessView(LayoutInflater inflater, ViewGroup container);


    protected abstract void onNetFailClick();

    protected void onNoDataClick() {

    }


    @Override
    protected void initView() {

    }

    @Override
    protected void refreshView() {

    }
    public void setViewShown(int state) {
        mState = state;
        mLoadingView.setVisibility(state == STATE_LOADING ? View.VISIBLE : View.INVISIBLE);
        mSuccessView.setVisibility(state == STATE_SUCCESS ? View.VISIBLE : View.INVISIBLE);
        mNetFailView.setVisibility(state == STATE_NET_FAIL ? View.VISIBLE : View.INVISIBLE);
        mNoDataView.setVisibility(state == STATE_NO_DATA ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onEventChanged(String group, String event, Object object) {
        return false;
    }
}
