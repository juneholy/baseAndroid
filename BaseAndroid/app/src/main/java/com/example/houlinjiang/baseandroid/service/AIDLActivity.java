package com.example.houlinjiang.baseandroid.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.houlinjiang.baseandroid.R;

/**
 * Created by houlin.jiang on 2016/12/22.
 */

public class AIDLActivity extends Activity {

    private IMyAidlInterface mMyAIDL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        setTitle("AIDL");
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
            mMyAIDL =  IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("ServiceConnection","onServiceDisconnected");
            mMyAIDL = null;
        }
    };

    public void bindServiceClick(View view) throws RemoteException {
        Intent intent = new Intent(this, MyRemoteService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void test() {
        try {
            mMyAIDL.plus(1,2);
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(this,"The service is not available",Toast.LENGTH_SHORT);
        }
    }

    public void unBindServiceClick(View view) {
        unbindService(connection);
    }
}
