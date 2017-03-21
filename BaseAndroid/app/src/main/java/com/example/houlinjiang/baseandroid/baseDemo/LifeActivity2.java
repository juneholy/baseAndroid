package com.example.houlinjiang.baseandroid.baseDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.example.houlinjiang.baseandroid.R;

/**
 * Created by houlin.jiang on 2017/2/14.
 */

public class LifeActivity2 extends Activity{
    private final static String TAG = "LifeActivity**2**";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"===========onCreate=============");
        Log.e(TAG,this +"");

        setContentView(R.layout.activity_life_cycle);
        init();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_test);
        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LifeActivity2.this,LifeActivity1.class));
            }
        });
        //onSaveInstanceState 也会传到这里，但如果有，肯定不为空
        if (savedInstanceState != null) {

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //建议在这里恢复数据
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG,"===========onRestoreInstanceState=============");
    }

    private void init() {
        //activity 的初始化工作
    }

    @Override
    protected void onRestart() {
        //activity从不可见变为可见
        //onPause => onDestroy => onRestart
        super.onRestart();
        Log.e(TAG,"===========onRestart=============");
    }

    @Override
    protected void onStart() {
        // activity 可见但还未出现在前台，无法交互
        super.onStart();
        Log.e(TAG,"===========onStart=============");
    }


    @Override
    protected void onNewIntent(Intent intent) {
        //启动模式为singleTop且栈顶有实例
        //启动模式为singleTask且栈内有实例
        super.onNewIntent(intent);
        Log.e(TAG,"===========onNewIntent=============");
        Log.e(TAG,this +"");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG,"===========onSaveInstanceState=============");
    }



    @Override
    protected void onResume() {
        //activity 出现在前台，可以和用户交互
        super.onResume();
        Log.e(TAG,"===========onResume=============");
    }

    @Override
    protected void onPause() {
        // 正在停止
        // onPause => 新的activity onResume() =>onStop()
        super.onPause();
        saveData();
        Log.e(TAG,"===========onPause=============");
    }

    private void saveData() {
        //处理非耗时数据保存
        // 停止动画
    }

    @Override
    protected void onStop() {
        super.onStop();
        myGc();
        Log.e(TAG,"===========onStop=============");
    }

    private void myGc() {
        //回收
        //资源释放
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"===========onDestroy=============");
    }
}
