package com.larswerkman.boxer.android;

import android.os.Bundle;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.internal.BoxerProcessor;

import java.lang.reflect.Method;

/**
 * Created by klm62637 on 12/1/2014.
 */
public class BundleWrapper extends Boxer {

    public Bundle bundle;

    public BundleWrapper(Object object) {
        super(object);
        bundle = (Bundle) object;
    }

    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        Bundle bundle = new Bundle();
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass(), Boxer.class);
            method.invoke(null, value, new BundleWrapper(new Bundle()));
        } catch (Exception e){/*TODO not empty constructor for T class*/}
        this.bundle.putBundle(key, bundle);
    }

    @Override
    public void addEnum(String key, Enum value) {
        bundle.putString(key, value.name());
    }

    @Override
    public void addString(String key, String value) {
        bundle.putString(key, value);
    }

    @Override
    public void addBoolean(String key, boolean value) {
        bundle.putBoolean(key, value);
    }

    @Override
    public void addByte(String key, byte value) {
        bundle.putByte(key, value);
    }

    @Override
    public void addChar(String key, char value) {
        bundle.putChar(key, value);
    }

    @Override
    public void addShort(String key, short value) {
        bundle.putShort(key, value);
    }

    @Override
    public void addInt(String key, int value) {
        bundle.putInt(key, value);
    }

    @Override
    public void addLong(String key, long value) {
        bundle.putLong(key, value);
    }

    @Override
    public void addDouble(String key, double value) {
        bundle.putDouble(key, value);
    }

    @Override
    public void addFloat(String key, float value) {
        bundle.putFloat(key, value);
    }

    @Override
    public <T extends Boxable> T get(String key, Class<T> clazz) {
        try {
            Class boxer = Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, Boxer.class);
            return (T) method.invoke(null, new BundleWrapper(bundle.getBundle(key)));
        } catch (Exception e){};
        return null;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            return (T) method.invoke(null, bundle.getString(key));
        } catch (Exception e){}
        return null;
    }

    @Override
    public String getString(String key) {
        return bundle.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return bundle.getBoolean(key);
    }

    @Override
    public byte getByte(String key) {
        return bundle.getByte(key);
    }

    @Override
    public char getChar(String key) {
        return bundle.getChar(key);
    }

    @Override
    public short getShort(String key) {
        return bundle.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return bundle.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return bundle.getLong(key);
    }

    @Override
    public double getDouble(String key) {
        return bundle.getDouble(key);
    }

    @Override
    public float getFloat(String key) {
        return bundle.getFloat(key);
    }
}
