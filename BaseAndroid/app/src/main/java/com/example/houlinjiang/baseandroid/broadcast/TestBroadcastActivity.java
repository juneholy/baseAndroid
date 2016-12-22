package com.example.houlinjiang.baseandroid.broadcast;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.example.houlinjiang.baseandroid.R;

/**
 * Created by houlin.jiang on 2016/12/22.
 */

public class TestBroadcastActivity extends Activity{
    MyBroadcastReceiver mReceiveBroadCast = new MyBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
    }

    public void registerBroadcastClick(View view) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mReceiveBroadCast, filter);
    }

    public void unregisterBroadcastClick(View view) {
        unregisterReceiver(mReceiveBroadCast);
    }

    public void sendBroadcastClick(View view) {
        ContentResolver cr = getContentResolver();
        Settings.System.putString(cr,Settings.System.AIRPLANE_MODE_ON, "1");
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        sendBroadcast(intent);
    }
}
