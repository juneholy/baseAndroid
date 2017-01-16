package com.example.houlinjiang.baseandroid;

import android.Manifest.permission;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.houlinjiang.baseandroid.constant.RequestCode;
import com.example.houlinjiang.baseandroid.service.DaemonService;

public class MainActivity extends AppCompatActivity implements RequestCode{

    private TextView tv_log;
    private Button btStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_log = (TextView) findViewById(R.id.tv_log);
        btStart = (Button) findViewById(R.id.bt_testClick);
        btStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                testOnClick();
            }
        });
    }

    private void startAlarm() {
        Intent intent = new Intent("ALARM_CLOCK");
        intent.putExtra("msg","闹钟响了");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60*1000,pendingIntent);
    }

    private void testOnClick() {
        startService(new Intent(this,DaemonService.class));
    }


    public void testCrash(View view) {
        Activity activity = null;
        activity.finish();
    }

    public void getAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{permission.GET_ACCOUNTS,permission.ACCOUNT_MANAGER},REQUEST_CODE_REQUEST_PERMISSION);
            return;
        }
        Account[] accounts = accountManager.getAccounts();
        StringBuilder sb = new StringBuilder();
        for (Account a: accounts) {
            sb.append(a.name).append("\n");
        }
        tv_log.setText(sb.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSION) {
            getAccount();
        }
    }
}
