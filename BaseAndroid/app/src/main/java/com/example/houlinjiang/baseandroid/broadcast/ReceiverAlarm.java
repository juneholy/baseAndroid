package com.example.houlinjiang.baseandroid.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by houlin.jiang on 2017/1/11.
 */

public class ReceiverAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("msg");
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
