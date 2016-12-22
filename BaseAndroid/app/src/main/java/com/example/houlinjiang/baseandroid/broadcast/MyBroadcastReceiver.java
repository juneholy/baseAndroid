package com.example.houlinjiang.baseandroid.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.houlinjiang.baseandroid.service.MyService;

/**
 * Created by houlin.jiang on 2016/12/22.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("data");
        Toast.makeText(context, "收到一条广播 "+ intent.getAction() + " ，内容是 ： " + message, Toast.LENGTH_LONG).show();
        context.startService(new Intent(context,MyService.class));
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
        }
    }
}

