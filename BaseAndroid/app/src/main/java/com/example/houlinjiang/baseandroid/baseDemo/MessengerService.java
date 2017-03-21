package com.example.houlinjiang.baseandroid.baseDemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.houlinjiang.baseandroid.constant.Constants;


/**
 * Created by houlin.jiang on 2017/2/15.
 */

public class MessengerService extends Service{
    private static final String TAG = "MessengerService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_FROM_CLIENT :
                    Log.e(TAG,"receive client msg : " + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message backMsg = Message.obtain(null,Constants.MSG_FROM_CLIENT);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg","ok,i receive you message");
                    backMsg.setData(bundle);
                    try {
                        client.send(backMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private final Messenger mMessenger = new Messenger(new MessengerHandler());
}
