package com.example.houlinjiang.baseandroid.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.houlinjiang.baseandroid.utils.UIUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by houlin.jiang on 2016/12/27.
 */

public class SQLiteStorage implements IStorage{

    private static SQLiteDatabase mInstance;

    public static SQLiteDatabase getInstance() {
        if (mInstance == null) {
            synchronized (SQLiteStorage.class) {
                if (mInstance == null) {
                    mInstance = UIUtil.getContext().openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                    mInstance.execSQL("CREATE TABLE storage (_key VARCHAR PRIMARY KEY, name VARCHAR)");
                }
            }
        }
        return mInstance;
    }
    @Override
    public boolean putSerializable(String var1, Serializable var2) {
//        mInstance.execSQL("INSERT INTO storage VALUES(?,?)",var1,JSON.toJSONString(var2));
        return false;
    }

    @Override
    public boolean putBytes(String var1, byte[] var2) {
        return false;
    }

    @Override
    public boolean putInt(String var1, int var2) {
        return false;
    }

    @Override
    public boolean putShort(String var1, short var2) {
        return false;
    }

    @Override
    public boolean putLong(String var1, long var2) {
        return false;
    }

    @Override
    public boolean putFloat(String var1, float var2) {
        return false;
    }

    @Override
    public boolean putDouble(String var1, double var2) {
        return false;
    }

    @Override
    public boolean putString(String var1, String var2) {
        return false;
    }

    @Override
    public boolean putBoolean(String var1, boolean var2) {
        return false;
    }

    @Override
    public <T extends Serializable> T getSerializable(String var1, Class<T> var2, T var3) {
        return null;
    }

    @Override
    public int getInt(String var1, int var2) {
        return 0;
    }

    @Override
    public double getDouble(String var1, double var2) {
        return 0;
    }

    @Override
    public float getFloat(String var1, float var2) {
        return 0;
    }

    @Override
    public short getShort(String var1, short var2) {
        return 0;
    }

    @Override
    public long getLong(String var1, long var2) {
        return 0;
    }

    @Override
    public String getString(String var1, String var2) {
        return null;
    }

    @Override
    public boolean getBoolean(String var1, boolean var2) {
        return false;
    }

    @Override
    public byte[] getBytes(String var1, byte[] var2) {
        return new byte[0];
    }

    @Override
    public boolean remove(String var1) {
        return false;
    }

    @Override
    public boolean contains(String var1) {
        return false;
    }

    @Override
    public Map<String, Object> getAll() {
        return null;
    }

    @Override
    public List<String> getKeys() {
        return null;
    }

    @Override
    public boolean cleanAllStorage() {
        return false;
    }
}
