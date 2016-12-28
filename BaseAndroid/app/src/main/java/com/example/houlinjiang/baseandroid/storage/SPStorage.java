package com.example.houlinjiang.baseandroid.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by houlin.jiang on 2016/12/27.
 */

public class SPStorage implements IStorage{

    private SharedPreferences mSharedPreferences;
    private String prefix;

    private SPStorage(Context context, String name, String prefix) {
        this.mSharedPreferences = context.getSharedPreferences(name, 0);
        this.prefix = prefix;
    }

    public static IStorage newInstance(Context context, String name, String prefix) {
        return new SPStorage(context, name, prefix);
    }

    public static byte[] i2b(int i) {
        byte[] result = new byte[]{(byte)(i >> 24 & 255), (byte)(i >> 16 & 255), (byte)(i >> 8 & 255), (byte)(i & 255)};
        return result;
    }

    private static byte[] l2b(long value) {
        byte[] result = new byte[]{(byte)((int)(value >> 56 & 255L)), (byte)((int)(value >> 48 & 255L)), (byte)((int)(value >> 40 & 255L)), (byte)((int)(value >> 32 & 255L)), (byte)((int)(value >> 24 & 255L)), (byte)((int)(value >> 16 & 255L)), (byte)((int)(value >> 8 & 255L)), (byte)((int)(value & 255L))};
        return result;
    }

    public static int b2i(byte[] bytes) {
        int value = 0;

        for(int i = 0; i < 4; ++i) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 255) << shift;
        }

        return value;
    }

    private static long b2l(byte[] b) {
        long result = 0L;

        for(int i = 0; i < 8; ++i) {
            int shift = (7 - i) * 8;
            result += ((long)b[i] & 255L) << shift;
        }

        return result;
    }

    private boolean putBytes(int type, String key, byte[] value) {
        if(value != null && value.length != 0) {
            try {
                byte[] e1 = new byte[value.length + 1];
                e1[0] = (byte)type;
                System.arraycopy(value, 0, e1, 1, value.length);
                final Editor editor = this.mSharedPreferences.edit();
                String v = Base64.encodeToString(value, 2);
                editor.putString(this.getPrefix() + key, v);
                if (VERSION.SDK_INT < 9) {
                    return editor.commit();
                } else {
                    editor.apply();
                    return true;
                }
            } catch (Throwable var7) {
                return false;
            }
        } else {
            final Editor e = this.mSharedPreferences.edit();
            e.putString(this.getPrefix() + key, "");
            if (VERSION.SDK_INT < 9) {
                return e.commit();
            } else {
                e.apply();
            }
            return true;
        }
    }

    private byte[] de(String value) {
        if(!TextUtils.isEmpty(value)) {
            byte[] data = Base64.decode(value, 2);
            return data;
        } else {
            return null;
        }
    }

    private byte[] getBytes(String key) {
        String value = this.mSharedPreferences.getString(this.getPrefix() + key, (String)null);
        return this.de(value);
    }

    private byte[] getBytesAndCheck(int type, String key) {
        byte[] data = this.getBytes(key);
        if(data != null && data.length > 0) {
            if(data[0] != (byte)type) {
                throw new RuntimeException("类型不匹配");
            }

            byte[] result = new byte[data.length - 1];
            System.arraycopy(data, 1, result, 0, result.length);
            data = result;
        }

        return data;
    }

    public boolean putBytes(String key, byte[] value) {
        return this.putBytes(0, key, value);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        try {
            byte[] e = this.getBytesAndCheck(0, key);
            if(e == null || e.length == 0) {
                e = defaultValue;
            }

            return e;
        } catch (Throwable var4) {
            return defaultValue;
        }
    }

    public boolean putInt(String key, int value) {
        byte[] result = i2b(value);
        return this.putBytes(2, key, result);
    }

    public boolean putShort(String key, short value) {
        byte[] result = new byte[]{(byte)(value >> 8 & 255), (byte)(value & 255)};
        return this.putBytes(1, key, result);
    }

    public boolean putLong(String key, long value) {
        byte[] result = l2b(value);
        return this.putBytes(3, key, result);
    }

    public boolean putFloat(String key, float value) {
        byte[] result = i2b(Float.floatToIntBits(value));
        return this.putBytes(4, key, result);
    }

    public boolean putDouble(String key, double value) {
        byte[] result = l2b(Double.doubleToLongBits(value));
        return this.putBytes(5, key, result);
    }

    public boolean putString(String key, String value) {
        try {
            byte[] e = value == null?null:value.getBytes("UTF-8");
            return this.putBytes(7, key, e);
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException(var4);
        }
    }

    public boolean putBoolean(String key, boolean value) {
        byte b = (byte)(value?1:0);
        byte[] data = new byte[]{b};
        return this.putBytes(6, key, data);
    }

    public boolean putSerializable(String key, Serializable value) {
        byte[] data = null;
        if(value != null) {
            ByteArrayOutputStream bytestream = null;
            ObjectOutputStream objectstream = null;

            boolean var7;
            try {
                bytestream = new ByteArrayOutputStream();
                objectstream = new ObjectOutputStream(bytestream);
                objectstream.writeObject(value);
                objectstream.flush();
                data = bytestream.toByteArray();
                return this.putBytes(8, key, data);
            } catch (IOException var21) {
                var7 = false;
            } finally {
                if(objectstream != null) {
                    try {
                        objectstream.close();
                    } catch (IOException var20) {
                        ;
                    }
                }

                if(bytestream != null) {
                    try {
                        bytestream.close();
                    } catch (IOException var19) {
                        ;
                    }
                }

            }

            return var7;
        } else {
            return this.putBytes(8, key, data);
        }
    }

    public short getShort(String key, short defaultValue) {
        short result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(1, key);
            if(e != null) {
                result = 0;

                for(int i = 0; i < 2; ++i) {
                    int shift = (1 - i) * 8;
                    result = (short)(result + ((e[i] & 255) << shift));
                }
            }
        } catch (Throwable var7) {
            ;
        }

        return result;
    }

    public int getInt(String key, int defaultValue) {
        int result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(2, key);
            if(e != null) {
                result = b2i(e);
            }
        } catch (Throwable var5) {
            ;
        }

        return result;
    }

    public long getLong(String key, long defaultValue) {
        long result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(3, key);
            if(e != null) {
                result = b2l(e);
            }
        } catch (Throwable var7) {
            ;
        }

        return result;
    }

    public float getFloat(String key, float defaultValue) {
        float result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(4, key);
            if(e != null) {
                result = Float.intBitsToFloat(b2i(e));
            }
        } catch (Throwable var5) {
            ;
        }

        return result;
    }

    public double getDouble(String key, double defaultValue) {
        double result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(5, key);
            if(e != null) {
                result = Double.longBitsToDouble(b2l(e));
            }
        } catch (Throwable var7) {
            ;
        }

        return result;
    }

    public String getString(String key, String defaultValue) {
        String result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(7, key);
            if(e != null) {
                result = new String(e, "UTF-8");
            }
        } catch (Throwable var5) {
            ;
        }

        return result;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(6, key);
            if(e != null) {
                result = e[0] == 1;
            }
        } catch (Throwable var5) {
            ;
        }

        return result;
    }

    public <T extends Serializable> T getSerializable(String key, Class<T> clazz, T defaultValue) {
        Object bytestream = null;
        ObjectInputStream objectstream = null;
        Serializable result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(8, key);
            if(e != null) {
                objectstream = new ObjectInputStream(new ByteArrayInputStream(e));
                result = (Serializable)objectstream.readObject();
            }
        } catch (Throwable var20) {
            ;
        } finally {
            if(objectstream != null) {
                try {
                    objectstream.close();
                } catch (IOException var19) {
                    ;
                }
            }

            if(bytestream != null) {
                try {
                    ((ByteArrayInputStream)bytestream).close();
                } catch (IOException var18) {
                    ;
                }
            }

        }

        return (T) result;
    }

    public boolean remove(String key) {
        try {
            Editor e = this.mSharedPreferences.edit();
            e.remove(this.getPrefix() + key);
            return e.commit();
        } catch (Exception var3) {
            return false;
        }
    }

    public boolean contains(String key) {
        try {
            return this.mSharedPreferences.contains(this.getPrefix() + key);
        } catch (Exception var3) {
            return false;
        }
    }

    public List<String> getKeys() {
        ArrayList result = new ArrayList();
        Map all = this.mSharedPreferences.getAll();
        if(all != null && !all.isEmpty()) {
            String p = this.getPrefix();
            Iterator i$ = all.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                if(key.startsWith(p)) {
                    String newKey = key.substring(p.length());
                    result.add(newKey);
                }
            }
        }

        return result;
    }

    public Map<String, Object> getAll() {
        HashMap map = new HashMap();
        Map all = this.mSharedPreferences.getAll();
        String strValue;
        if(all != null && !all.isEmpty()) {
            String i$ = this.getPrefix();
            Iterator key = all.keySet().iterator();

            while(key.hasNext()) {
                strValue = (String)key.next();
                if(strValue.startsWith(i$)) {
                    String data = strValue.substring(i$.length());
                    map.put(data, all.get(strValue));
                }
            }
        }

        Object value;
        String var31;
        for(Iterator var30 = map.keySet().iterator(); var30.hasNext(); map.put(var31, value)) {
            var31 = (String)var30.next();
            strValue = String.valueOf(map.get(var31));
            byte[] var32 = this.de(strValue);
            value = null;
            if(var32 != null && var32.length > 0) {
                byte type = var32[0];
                byte[] rbytes = new byte[var32.length - 1];
                System.arraycopy(var32, 1, rbytes, 0, rbytes.length);
                var32 = rbytes;
                switch(type) {
                    case 0:
                        value = rbytes;
                        break;
                    case 1:
                        short result = 0;

                        for(int var33 = 0; var33 < 2; ++var33) {
                            int var34 = (1 - var33) * 8;
                            result = (short)(result + ((var32[var33] & 255) << var34));
                        }

                        value = Short.valueOf(result);
                        break;
                    case 2:
                        value = Integer.valueOf(b2i(rbytes));
                        break;
                    case 3:
                        value = Long.valueOf(b2l(rbytes));
                        break;
                    case 4:
                        value = Float.valueOf(Float.intBitsToFloat(b2i(rbytes)));
                        break;
                    case 5:
                        value = Double.valueOf(Double.longBitsToDouble(b2l(rbytes)));
                        break;
                    case 6:
                        value = Boolean.valueOf(rbytes[0] == 1);
                        break;
                    case 7:
                        try {
                            value = new String(var32, "UTF-8");
                        } catch (UnsupportedEncodingException var28) {
                            ;
                        }
                        break;
                    case 8:
                        Object bytestream = null;
                        ObjectInputStream objectstream = null;

                        try {
                            objectstream = new ObjectInputStream(new ByteArrayInputStream(var32));
                            value = objectstream.readObject();
                        } catch (Throwable var27) {
                            ;
                        } finally {
                            if(objectstream != null) {
                                try {
                                    objectstream.close();
                                } catch (IOException var26) {
                                    ;
                                }
                            }

                            if(bytestream != null) {
                                try {
                                    ((ByteArrayInputStream)bytestream).close();
                                } catch (IOException var25) {
                                    ;
                                }
                            }

                        }
                }
            }
        }

        return map;
    }

    public boolean cleanAllStorage() {
        Map all = this.mSharedPreferences.getAll();
        if(all != null && !all.isEmpty()) {
            ArrayList delList = new ArrayList();
            String p = this.getPrefix();
            Iterator editor = all.keySet().iterator();

            while(editor.hasNext()) {
                String i$ = (String)editor.next();
                if(i$.startsWith(p)) {
                    delList.add(i$);
                }
            }

            if(!delList.isEmpty()) {
                Editor editor1 = this.mSharedPreferences.edit();
                Iterator i$1 = delList.iterator();

                while(i$1.hasNext()) {
                    String key = (String)i$1.next();
                    editor1.remove(key);
                }

                return editor1.commit();
            }
        }

        return false;
    }

    private String getPrefix() {
        return hashKeyForDisk(this.prefix) + "_";
    }

    public static String hashKeyForDisk(String key) {
        if(key == null) {
            return null;
        } else {
            String cacheKey;
            try {
                MessageDigest e = MessageDigest.getInstance("MD5");
                e.update(key.getBytes());
                cacheKey = bytesToHexString(e.digest());
            } catch (NoSuchAlgorithmException var3) {
                cacheKey = String.valueOf(key.hashCode());
            }

            return cacheKey;
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(255 & bytes[i]);
            if(hex.length() == 1) {
                sb.append('0');
            }

            sb.append(hex);
        }

        return sb.toString();
    }
}
