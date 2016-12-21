package com.example.houlinjiang.baseandroid;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Created by houlin.jiang on 2016/12/20.
 */

public class MyService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("MyService","onCreate");
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("MyService","onStartCommand");
        initNotification();
        /**
        1):START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
         系统就会重新创建这个服务并且调用onStartCommand()方法，但是它不会重新传递最后的Intent对象，这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
         2):START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务
        3):START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。这适用于如下载文件
        4):START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。

         */
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("MyService","onDestroy");
        super.onDestroy();
    }

    private void initNotification() {
        Notification.Builder builder = new Builder(this);

        RemoteViews rv = new RemoteViews(getPackageName(),R.layout.layout_notification);
        rv.setTextViewText(R.id.tv_notification,"通知展示内容");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getService(this,1,intent, PendingIntent.FLAG_NO_CREATE);

        builder.setSmallIcon(R.drawable.icon)
                .setContentText("常驻前台")
                .setCustomContentView(rv)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
}
