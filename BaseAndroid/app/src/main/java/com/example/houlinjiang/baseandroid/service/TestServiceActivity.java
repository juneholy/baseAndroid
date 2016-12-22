package com.example.houlinjiang.baseandroid.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.houlinjiang.baseandroid.service.MyService.MyBinder;
import com.example.houlinjiang.baseandroid.R;

/**
 * Created by houlin.jiang on 2016/12/20.
 */

public class TestServiceActivity extends Activity{

    private MyService.MyBinder mBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Log.e("MyService", "MainActivity thread id is " + Thread.currentThread().getId());
    }

    public void startServiceClick(View view) {
        startService(new Intent(this,MyService.class));
    }

    public void stopServiceClick(View view) {
        stopService(new Intent(this,MyService.class));
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("ServiceConnection","onServiceConnected");
            mBinder = (MyBinder) service;
            mBinder.startTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("ServiceConnection","onServiceDisconnected");
        }
    };

    public void bindServiceClick(View view) {
        bindService(new Intent(this,MyService.class),connection,BIND_AUTO_CREATE);
    }

    public void unBindServiceClick(View view) {
        unbindService(connection);
    }
}
