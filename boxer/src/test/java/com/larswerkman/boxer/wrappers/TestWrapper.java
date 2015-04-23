package com.larswerkman.boxer.wrappers;

import android.os.Bundle;
import com.google.common.primitives.*;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by lars on 23-04-15.
 */
public class TestWrapper extends Boxer {

    private HashMap<String, Object> map;

    /**
     * Empty constructor, Can't be a generic type because of ClassNotFoundException
     *
     * @param object Serialization object
     */
    public TestWrapper(Object object) {
        super(object);
        this.map = (HashMap<String, Object>) object;
    }


    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        map.put(key, storeBoxable(getClass(), value, new HashMap<String, Object>()));
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {
        HashMap<String, Object> boxeables = new HashMap<String, Object>();
        boxeables.put("size", value.size());
        for(int i = 0; i < value.size(); i++){
            boxeables.put(String.valueOf(i), storeBoxable(getClass(), value.get(i), new HashMap<String, Objects>()));
        }
        map.put(key, boxeables);
    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {
        HashMap<String, Object> boxeables = new HashMap<String, Object>();
        boxeables.put("size", value.length);
        for(int i = 0; i < value.length; i++){
            boxeables.put(String.valueOf(i), storeBoxable(getClass(), value[i], new HashMap<String, Objects>()));
        }
        map.put(key, boxeables);
    }

    @Override
    public void addEnum(String key, Enum value) {
        map.put(key, value);
    }

    @Override
    public void addEnumArray(String key, Enum[] value) {
        map.put(key, value);
    }

    @Override
    public void addEnumList(String key, List<? extends Enum> value) {
        map.put(key, value);
    }

    @Override
    public void addString(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void addStringArray(String key, String[] value) {
        map.put(key, value);
    }

    @Override
    public void addStringList(String key, List<String> value) {
        map.put(key, value);
    }

    @Override
    public void addBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {
        map.put(key, value);
    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {
        map.put(key, value);
    }

    @Override
    public void addByte(String key, byte value) {
        map.put(key, value);
    }

    @Override
    public void addByteArray(String key, byte[] value) {
        map.put(key, value);
    }

    @Override
    public void addByteList(String key, List<Byte> value) {
        map.put(key, value);
    }

    @Override
    public void addChar(String key, char value) {
        map.put(key, value);
    }

    @Override
    public void addCharArray(String key, char[] value) {
        map.put(key, value);
    }

    @Override
    public void addCharList(String key, List<Character> value) {
        map.put(key, value);
    }

    @Override
    public void addShort(String key, short value) {
        map.put(key, value);
    }

    @Override
    public void addShortArray(String key, short[] value) {
        map.put(key, value);
    }

    @Override
    public void addShortList(String key, List<Short> value) {
        map.put(key, value);
    }

    @Override
    public void addInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public void addIntArray(String key, int[] value) {
        map.put(key, value);
    }

    @Override
    public void addIntList(String key, List<Integer> value) {
        map.put(key, value);
    }

    @Override
    public void addLong(String key, long value) {
        map.put(key, value);
    }

    @Override
    public void addLongArray(String key, long[] value) {
        map.put(key, value);
    }

    @Override
    public void addLongList(String key, List<Long> value) {
        map.put(key, value);
    }

    @Override
    public void addDouble(String key, double value) {
        map.put(key, value);
    }

    @Override
    public void addDoubleArray(String key, double[] value) {
        map.put(key, value);
    }

    @Override
    public void addDoubleList(String key, List<Double> value) {
        map.put(key, value);
    }

    @Override
    public void addFloat(String key, float value) {
        map.put(key, value);
    }

    @Override
    public void addFloatArray(String key, float[] value) {
        map.put(key, value);
    }

    @Override
    public void addFloatList(String key, List<Float> value) {
        map.put(key, value);
    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        return retrieveBoxable(getClass(), clazz, map.get(key));
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        HashMap<String, Object> values = (HashMap<String, Object>) map.get(key);
        int size = (Integer) values.get("size");
        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = retrieveBoxable(getClass(), clazz, values.get(String.valueOf(i)));
        }
        return boxables;
    }

    @Override
    public <T extends Boxable, E extends List<T>> E getBoxableList(String key, Class<T> clazz, Class<E> listtype) {
        HashMap<String, Object> values = (HashMap<String, Object>) map.get(key);
        int size = (Integer) values.get("size");
        E boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(retrieveBoxable(getClass(), clazz, values.get(String.valueOf(i))));
            }
        } catch (Exception e){/*Do Nothing*/};
        return boxables;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return (T) map.get(key);
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        return (T[]) map.get(key);
    }

    @Override
    public <T extends Enum, E extends List<T>> E getEnumList(String key, Class<T> clazz, Class<E> listtype) {
        return (E) map.get(key);
    }

    @Override
    public String getString(String key) {
        return (String) map.get(key);
    }

    @Override
    public String[] getStringArray(String key) {
        return (String[]) map.get(key);
    }

    @Override
    public <T extends List<String>> T getStringList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return (Boolean) map.get(key);
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        return (boolean[]) map.get(key);
    }

    @Override
    public <T extends List<Boolean>> T getBooleanList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public byte getByte(String key) {
        return (Byte) map.get(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return (byte[]) map.get(key);
    }

    @Override
    public <T extends List<Byte>> T getByteList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public char getChar(String key) {
        return (Character) map.get(key);
    }

    @Override
    public char[] getCharArray(String key) {
        return (char[]) map.get(key);
    }

    @Override
    public <T extends List<Character>> T getCharList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public short getShort(String key) {
        return (Short) map.get(key);
    }

    @Override
    public short[] getShortArray(String key) {
        return (short[]) map.get(key);
    }

    @Override
    public <T extends List<Short>> T getShortList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public int getInt(String key) {
        return (Integer) map.get(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return (int[]) map.get(key);
    }

    @Override
    public <T extends List<Integer>> T getIntList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public long getLong(String key) {
        return (Long) map.get(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return (long[]) map.get(key);
    }

    @Override
    public <T extends List<Long>> T getLongList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public double getDouble(String key) {
        return (Double) map.get(key);
    }

    @Override
    public double[] getDoubleArray(String key) {
        return (double[]) map.get(key);
    }

    @Override
    public <T extends List<Double>> T getDoubleList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }

    @Override
    public float getFloat(String key) {
        return (Float) map.get(key);
    }

    @Override
    public float[] getFloatArray(String key) {
        return (float[]) map.get(key);
    }

    @Override
    public <T extends List<Float>> T getFloatList(String key, Class<T> listtype) {
        return (T) map.get(key);
    }
}
