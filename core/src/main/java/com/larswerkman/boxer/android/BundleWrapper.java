package com.larswerkman.boxer.android;

import android.os.Bundle;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.internal.BoxerProcessor;

import java.lang.reflect.Method;
import java.util.List;

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
        } catch (Exception e){}
        this.bundle.putBundle(key, bundle);
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {

    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {

    }

    @Override
    public void addEnum(String key, Enum value) {

    }

    @Override
    public void addEnumArray(String key, Enum[] value) {

    }

    @Override
    public void addEnumList(String key, List<Enum> value) {

    }

    @Override
    public void addBoolean(String key, boolean value) {

    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {

    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {

    }

    @Override
    public void addByte(String key, byte value) {

    }

    @Override
    public void addByteArray(String key, byte[] value) {

    }

    @Override
    public void addByteList(String key, List<Byte> value) {

    }

    @Override
    public void addChar(String key, char value) {

    }

    @Override
    public void addCharArray(String key, char[] value) {

    }

    @Override
    public void addCharList(String key, List<Character> value) {

    }

    @Override
    public void addShort(String key, short value) {

    }

    @Override
    public void addShortArray(String key, short[] value) {

    }

    @Override
    public void addShortList(String key, List<Short> value) {

    }

    @Override
    public void addInt(String key, int value) {

    }

    @Override
    public void addIntArray(String key, int[] value) {

    }

    @Override
    public void addIntList(String key, List<Integer> value) {

    }

    @Override
    public void addLong(String key, long value) {

    }

    @Override
    public void addLongArray(String key, long[] value) {

    }

    @Override
    public void addLongList(String key, List<Long> value) {

    }

    @Override
    public void addDouble(String key, double value) {

    }

    @Override
    public void addDoubleArray(String key, double[] value) {

    }

    @Override
    public void addDoubleList(String key, List<Double> value) {

    }

    @Override
    public void addFloat(String key, float value) {

    }

    @Override
    public void addFloatArray(String key, float[] value) {

    }

    @Override
    public void addFloatList(String key, List<Float> value) {

    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Boxable> List<T> getBoxableList(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Enum> List<T> getEnumList(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public String[] getStringArray(String key) {
        return new String[0];
    }

    @Override
    public List<String> getStringList(String key) {
        return null;
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        return new boolean[0];
    }

    @Override
    public List<Boolean> getBooleanList(String key) {
        return null;
    }

    @Override
    public byte getByte(String key) {
        return 0;
    }

    @Override
    public byte[] getByteArray(String key) {
        return new byte[0];
    }

    @Override
    public List<Byte> getByteList(String key) {
        return null;
    }

    @Override
    public char getChar(String key) {
        return 0;
    }

    @Override
    public char[] getCharArray(String key) {
        return new char[0];
    }

    @Override
    public List<Character> getCharList(String key) {
        return null;
    }

    @Override
    public short getShort(String key) {
        return 0;
    }

    @Override
    public short[] getShortArray(String key) {
        return new short[0];
    }

    @Override
    public List<Short> getShortList(String key) {
        return null;
    }

    @Override
    public int getInt(String key) {
        return 0;
    }

    @Override
    public int[] getIntArray(String key) {
        return new int[0];
    }

    @Override
    public List<Integer> getIntList(String key) {
        return null;
    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public long[] getLongArray(String key) {
        return new long[0];
    }

    @Override
    public List<Long> getLongList(String key) {
        return null;
    }

    @Override
    public double getDouble(String key) {
        return 0;
    }

    @Override
    public double[] getDoubleArray(String key) {
        return new double[0];
    }

    @Override
    public List<Double> getDoubleList(String key) {
        return null;
    }

    @Override
    public float getFloat(String key) {
        return 0;
    }

    @Override
    public float[] getFloatArray(String key) {
        return new float[0];
    }

    @Override
    public List<Float> getFloatList(String key) {
        return null;
    }

    /*
    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        Bundle bundle = new Bundle();
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass(), Boxer.class);
            method.invoke(null, value, new BundleWrapper(new Bundle()));
        } catch (Exception e){*//*TODO not empty constructor for T class*//*}
        this.bundle.putBundle(key, bundle);
    }

    @Override
    public void addEnum(String key, Enum value) {
        bundle.putString(key, value.name());
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
    }*/


}
