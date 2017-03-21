package com.example.houlinjiang.baseandroid.baseDemo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.example.houlinjiang.baseandroid.R;

/**
 * Created by houlin.jiang on 2017/2/15.
 */

public class ContentProviderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Uri uri = Uri.parse("content://com.holy.baseDemo.myContentProvider");
        getContentResolver().query(uri,null,null,null,null);
    }
}
