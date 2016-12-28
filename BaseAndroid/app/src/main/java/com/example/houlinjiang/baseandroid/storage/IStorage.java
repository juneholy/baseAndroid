package com.example.houlinjiang.baseandroid.storage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by houlin.jiang on 2016/12/27.
 */

public interface IStorage {

    boolean putSerializable(String var1, Serializable var2);

    boolean putBytes(String var1, byte[] var2);

    boolean putInt(String var1, int var2);

    boolean putShort(String var1, short var2);

    boolean putLong(String var1, long var2);

    boolean putFloat(String var1, float var2);

    boolean putDouble(String var1, double var2);

    boolean putString(String var1, String var2);

    boolean putBoolean(String var1, boolean var2);

    <T extends Serializable> T getSerializable(String var1, Class<T> var2, T var3);

    int getInt(String var1, int var2);

    double getDouble(String var1, double var2);

    float getFloat(String var1, float var2);

    short getShort(String var1, short var2);

    long getLong(String var1, long var2);

    String getString(String var1, String var2);

    boolean getBoolean(String var1, boolean var2);

    byte[] getBytes(String var1, byte[] var2);

    boolean remove(String var1);

    boolean contains(String var1);

    Map<String, Object> getAll();

    List<String> getKeys();

    boolean cleanAllStorage();

}
