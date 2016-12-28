package com.example.houlinjiang.baseandroid.storage;

import android.content.Context;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by houlin.jiang on 2016/12/27.
 */

public class FileStorage implements IStorage {
    private File file;
    private JSONObject jsonObject;

    private FileStorage(Context context, File file) {
        if(file == null) {
            new RuntimeException("file is null!");
        }

        this.file = file;
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            this.jsonObject = new JSONObject();
        } else {
            if(file.exists() && file.isDirectory()) {
                throw new RuntimeException("无法创建文件!已存在同名的文件夹!");
            }

            this.jsonObject = this.getJSONObject();
        }

    }

    public static IStorage newInstance(Context context, File file) {
        return new FileStorage(context, file);
    }

    private JSONObject getJSONObject() {
        JSONObject jsonObject = null;
        if(this.file.exists()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileInputStream fis = null;

            try {
                byte[] e = new byte[4096];
                boolean len = true;
                fis = new FileInputStream(this.file);

                int len1;
                while((len1 = fis.read(e)) != -1) {
                    out.write(e, 0, len1);
                }

                jsonObject = new JSONObject(new String(out.toByteArray(), "UTF-8"));
            } catch (Exception var14) {
                ;
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (IOException var13) {
                        ;
                    }
                }

            }
        }

        if(jsonObject == null) {
            jsonObject = new JSONObject();
        }

        return jsonObject;
    }

    private void saveJsonObject(JSONObject jsonObject) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(this.file);
            byte[] e = jsonObject.toString().getBytes("UTF-8");
            fos.write(e);
        } catch (Exception var12) {
            ;
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException var11) {
                    ;
                }
            }

        }

    }

    private void put(String key, String value) throws JSONException {
        File var3 = this.file;
        synchronized(this.file) {
            this.jsonObject.put(key, value);
            this.saveJsonObject(this.jsonObject);
        }
    }

    private String get(String key) throws JSONException {
        File var2 = this.file;
        synchronized(this.file) {
            return this.jsonObject.optString(key, (String)null);
        }
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
                byte[] e = new byte[value.length + 1];
                e[0] = (byte)type;
                System.arraycopy(value, 0, e, 1, value.length);
                String v = Base64.encodeToString(value, 2);
                this.put(key, v);
                return true;
            } catch (Throwable var6) {
                return false;
            }
        } else {
            try {
                this.put(key, "");
                return true;
            } catch (JSONException var7) {
                return false;
            }
        }
    }

    private byte[] de(String value) {
        if(value != null) {
            byte[] data = Base64.decode(value, 2);
            return data;
        } else {
            return null;
        }
    }

    private byte[] getBytes(String key) throws JSONException {
        String value = this.get(key);
        return this.de(value);
    }

    private byte[] getBytesAndCheck(int type, String key) throws JSONException {
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
        Object byteStream = null;
        ObjectInputStream objectStream = null;
        Serializable result = defaultValue;

        try {
            byte[] e = this.getBytesAndCheck(8, key);
            if(e != null) {
                objectStream = new ObjectInputStream(new ByteArrayInputStream(e));
                result = (Serializable)objectStream.readObject();
            }
        } catch (Throwable var20) {
            ;
        } finally {
            if(objectStream != null) {
                try {
                    objectStream.close();
                } catch (IOException var19) {
                    ;
                }
            }

            if(byteStream != null) {
                try {
                    ((ByteArrayInputStream)byteStream).close();
                } catch (IOException var18) {
                    ;
                }
            }

        }

        return (T) result;
    }

    public boolean remove(String key) {
        try {
            File e = this.file;
            synchronized(this.file) {
                Object oldValue = this.jsonObject.remove(key);
                if(oldValue != null) {
                    this.saveJsonObject(this.jsonObject);
                }

                return oldValue != null;
            }
        } catch (Exception var6) {
            return false;
        }
    }

    public boolean contains(String key) {
        File var2 = this.file;
        synchronized(this.file) {
            boolean var10000;
            try {
                var10000 = this.jsonObject.has(key);
            } catch (Exception var5) {
                return false;
            }

            return var10000;
        }
    }

    public List<String> getKeys() {
        ArrayList result = new ArrayList();
        File var2 = this.file;
        synchronized(this.file) {
            Iterator keys = this.jsonObject.keys();

            while(keys.hasNext()) {
                String key = (String)keys.next();
                result.add(key);
            }

            return result;
        }
    }

    public Map<String, Object> getAll() {
        File var1 = this.file;
        synchronized(this.file) {
            HashMap map = new HashMap();

            String key;
            Object value;
            try {
                for(Iterator e = this.jsonObject.keys(); e.hasNext(); map.put(key, value)) {
                    key = (String)e.next();
                    String strValue = String.valueOf(this.jsonObject.get(key));
                    byte[] data = this.de(strValue);
                    value = null;
                    if(data != null && data.length > 0) {
                        byte type = data[0];
                        byte[] rbytes = new byte[data.length - 1];
                        System.arraycopy(data, 1, rbytes, 0, rbytes.length);
                        data = rbytes;
                        switch(type) {
                            case 0:
                                value = rbytes;
                                break;
                            case 1:
                                short result = 0;

                                for(int var35 = 0; var35 < 2; ++var35) {
                                    int var36 = (1 - var35) * 8;
                                    result = (short)(result + ((data[var35] & 255) << var36));
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
                                    value = new String(data, "UTF-8");
                                } catch (UnsupportedEncodingException var31) {
                                    ;
                                }
                                break;
                            case 8:
                                Object bytestream = null;
                                ObjectInputStream objectstream = null;

                                try {
                                    objectstream = new ObjectInputStream(new ByteArrayInputStream(data));
                                    value = objectstream.readObject();
                                } catch (Throwable var30) {
                                    ;
                                } finally {
                                    if(objectstream != null) {
                                        try {
                                            objectstream.close();
                                        } catch (IOException var29) {
                                            ;
                                        }
                                    }

                                    if(bytestream != null) {
                                        try {
                                            ((ByteArrayInputStream)bytestream).close();
                                        } catch (IOException var28) {
                                            ;
                                        }
                                    }

                                }
                        }
                    }
                }
            } catch (Throwable var33) {
                var33.printStackTrace();
            }

            return map;
        }
    }

    public boolean cleanAllStorage() {
        File var1 = this.file;
        synchronized(this.file) {
            this.saveJsonObject(this.jsonObject);
            return true;
        }
    }

}
