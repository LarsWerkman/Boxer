/*
 * Copyright 2014 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.larswerkman.boxer.wrappers.android;

import android.os.Bundle;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.internal.BoxerProcessor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 * BundleWrapper defines how the Boxer class should write to a {@link android.os.Bundle}
 */
public class BundleWrapper extends Boxer {

    public Bundle bundle;

    public BundleWrapper(Object object) {
        super(object);
        bundle = (Bundle) object;
    }

    private <T extends Boxable> Bundle storeBoxable(T value){
        Bundle bundle = new Bundle();
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass(), Boxer.class);
            method.invoke(null, value, new BundleWrapper(bundle));
        } catch (Exception e){}
        return bundle;
    }

    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        this.bundle.putBundle(key, storeBoxable(value));
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {
        Bundle bundle = new Bundle();
        bundle.putInt("size", value.size());
        for(int i = 0; i < value.size(); i++){
            bundle.putBundle(String.valueOf(i), storeBoxable(value.get(i)));
        }
        this.bundle.putBundle(key, bundle);
    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {
        Bundle bundle = new Bundle();
        bundle.putInt("size", value.length);
        for(int i = 0; i < value.length; i++){
            bundle.putBundle(String.valueOf(i), storeBoxable(value[i]));
        }
        this.bundle.putBundle(key, bundle);
    }

    @Override
    public void addEnum(String key, Enum value) {
        this.bundle.putString(key, value.name());
    }

    @Override
    public void addEnumArray(String key, Enum[] value) {
        String[] strings = new String[value.length];
        for(int i = 0; i < strings.length; i++){
            strings[i] = value[i].name();
        }
        this.bundle.putStringArray(key, strings);
    }

    @Override
    public void addEnumList(String key, List<Enum> value) {
        String[] strings = new String[value.size()];
        for(int i = 0; i < value.size(); i++){
            strings[i] = value.get(i).name();
        }
        this.bundle.putStringArray(key, strings);
    }

    @Override
    public void addString(String key, String value) {
        this.bundle.putString(key, value);
    }

    @Override
    public void addStringArray(String key, String[] value) {
        this.bundle.putStringArray(key, value);
    }

    @Override
    public void addStringList(String key, List<String> value) {
        this.bundle.putStringArray(key, (String[]) value.toArray());
    }

    @Override
    public void addBoolean(String key, boolean value) {
        this.bundle.putBoolean(key, value);
    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {
        this.bundle.putBooleanArray(key, value);
    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {
        boolean[] bools = new boolean[value.size()];
        for(int i = 0; i < value.size(); i++){
            bools[i] = value.get(i);
        }
        this.bundle.putBooleanArray(key, bools);
    }

    @Override
    public void addByte(String key, byte value) {
        this.bundle.putByte(key, value);
    }

    @Override
    public void addByteArray(String key, byte[] value) {
        this.bundle.putByteArray(key, value);
    }

    @Override
    public void addByteList(String key, List<Byte> value) {
        byte[] bytes = new byte[value.size()];
        for(int i = 0; i < value.size(); i++){
            bytes[i] = value.get(i);
        }
        this.bundle.putByteArray(key, bytes);
    }

    @Override
    public void addChar(String key, char value) {
        this.bundle.putChar(key, value);
    }

    @Override
    public void addCharArray(String key, char[] value) {
        this.bundle.putCharArray(key, value);
    }

    @Override
    public void addCharList(String key, List<Character> value) {
        char[] chars = new char[value.size()];
        for(int i = 0; i < value.size(); i++){
            chars[i] = value.get(i);
        }
        this.bundle.putCharArray(key, chars);
    }

    @Override
    public void addShort(String key, short value) {
        this.bundle.putShort(key, value);
    }

    @Override
    public void addShortArray(String key, short[] value) {
        this.bundle.putShortArray(key, value);
    }

    @Override
    public void addShortList(String key, List<Short> value) {
        short[] shorts = new short[value.size()];
        for(int i = 0; i < value.size(); i++){
            shorts[i] = value.get(i);
        }
        this.bundle.putShortArray(key, shorts);
    }

    @Override
    public void addInt(String key, int value) {
         this.bundle.putInt(key, value);
    }

    @Override
    public void addIntArray(String key, int[] value) {
        this.bundle.putIntArray(key, value);
    }

    @Override
    public void addIntList(String key, List<Integer> value) {
        int[] ints = new int[value.size()];
        for(int i = 0; i < value.size(); i++){
            ints[i] = value.get(i);
        }
        this.bundle.putIntArray(key, ints);
    }

    @Override
    public void addLong(String key, long value) {
        this.bundle.putLong(key, value);
    }

    @Override
    public void addLongArray(String key, long[] value) {
        this.bundle.putLongArray(key, value);
    }

    @Override
    public void addLongList(String key, List<Long> value) {
        long[] longs = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            longs[i] = value.get(i);
        }
        this.bundle.putLongArray(key, longs);
    }

    @Override
    public void addDouble(String key, double value) {
        this.bundle.putDouble(key, value);
    }

    @Override
    public void addDoubleArray(String key, double[] value) {
        this.bundle.putDoubleArray(key, value);
    }

    @Override
    public void addDoubleList(String key, List<Double> value) {
        double[] doubles = new double[value.size()];
        for(int i = 0; i < value.size(); i++){
            doubles[i] = value.get(i);
        }
        this.bundle.putDoubleArray(key, doubles);
    }

    @Override
    public void addFloat(String key, float value) {
        this.bundle.putFloat(key, value);
    }

    @Override
    public void addFloatArray(String key, float[] value) {
        this.bundle.putFloatArray(key, value);
    }

    @Override
    public void addFloatList(String key, List<Float> value) {
        float[] floats = new float[value.size()];
        for(int i = 0; i < value.size(); i++){
            floats[i] = value.get(i);
        }
        this.bundle.putFloatArray(key, floats);
    }

    public <T extends Boxable> T retrieveBoxable(Bundle bundle, Class<T> clazz){
        T boxable = null;
        try {
            Class boxer = Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, Boxer.class);
            boxable = (T) method.invoke(null, new BundleWrapper(bundle));
        } catch (Exception e){};
        return boxable;
    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        return retrieveBoxable(this.bundle.getBundle(key), clazz);
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        Bundle bundle = this.bundle.getBundle(key);
        int size = bundle.getInt("size");
        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = retrieveBoxable(bundle.getBundle(String.valueOf(i)), clazz);
        }
        return boxables;
    }

    @Override
    public <T extends Boxable> List<T> getBoxableList(String key, Class<T> clazz, Class<? extends List> listtype) {
        Bundle bundle = this.bundle.getBundle(key);
        int size = bundle.getInt("size");
        List<T> boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(retrieveBoxable(bundle.getBundle(String.valueOf(i)), clazz));
            }
        } catch (Exception e){};
        return boxables;
    }

    public <T extends Enum> T retrieveEnum(String value, Class<T> clazz){
        T en = null;
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            en =  (T) method.invoke(null, value);
        } catch (Exception e){}
        return en;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return retrieveEnum(this.bundle.getString(key), clazz);
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        String[] values = this.bundle.getStringArray(key);
        T[] enums = (T[]) Array.newInstance(clazz, values.length);
        for(int i = 0; i < values.length; i++){
            enums[i] = retrieveEnum(values[i], clazz);
        }
        return enums;
    }

    @Override
    public <T extends Enum> List<T> getEnumList(String key, Class<T> clazz, Class<? extends List> listtype) {
        String[] values = this.bundle.getStringArray(key);
        List<T> enums = null;
        try {
            enums = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                enums.add(retrieveEnum(values[i], clazz));
            }
        } catch (Exception e){};
        return enums;
    }

    @Override
    public String getString(String key) {
        return this.bundle.getString(key);
    }

    @Override
    public String[] getStringArray(String key) {
        return this.bundle.getStringArray(key);
    }

    @Override
    public List<String> getStringList(String key, Class<? extends List> listtype) {
        String[] values = this.bundle.getStringArray(key);
        List<String> strings = null;
        try {
            strings = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                strings.add(values[i]);
            }
        } catch (Exception e){};
        return strings;
    }

    @Override
    public boolean getBoolean(String key) {
        return this.bundle.getBoolean(key);
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        return this.bundle.getBooleanArray(key);
    }

    @Override
    public List<Boolean> getBooleanList(String key, Class<? extends List> listtype) {
        boolean[] values = this.bundle.getBooleanArray(key);
        List<Boolean> booleans = null;
        try {
            booleans = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                booleans.add(values[i]);
            }
        } catch (Exception e){};
        return booleans;
    }

    @Override
    public byte getByte(String key) {
        return this.bundle.getByte(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return this.bundle.getByteArray(key);
    }

    @Override
    public List<Byte> getByteList(String key, Class<? extends List> listtype) {
        byte[] values = this.bundle.getByteArray(key);
        List<Byte> bytes = null;
        try {
            bytes = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                bytes.add(values[i]);
            }
        } catch (Exception e){};
        return bytes;
    }

    @Override
    public char getChar(String key) {
        return this.bundle.getChar(key);
    }

    @Override
    public char[] getCharArray(String key) {
        return this.bundle.getCharArray(key);
    }

    @Override
    public List<Character> getCharList(String key, Class<? extends List> listtype) {
        char[] values = this.bundle.getCharArray(key);
        List<Character> chars = null;
        try {
            chars = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                chars.add(values[i]);
            }
        } catch (Exception e){};
        return chars;
    }

    @Override
    public short getShort(String key) {
        return this.bundle.getShort(key);
    }

    @Override
    public short[] getShortArray(String key) {
        return this.bundle.getShortArray(key);
    }

    @Override
    public List<Short> getShortList(String key, Class<? extends List> listtype) {
        short[] values = this.bundle.getShortArray(key);
        List<Short> shorts = null;
        try {
            shorts = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                shorts.add(values[i]);
            }
        } catch (Exception e){};
        return shorts;
    }

    @Override
    public int getInt(String key) {
        return this.bundle.getInt(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return this.bundle.getIntArray(key);
    }

    @Override
    public List<Integer> getIntList(String key, Class<? extends List> listtype) {
        int[] values = this.bundle.getIntArray(key);
        List<Integer> ints = null;
        try {
            ints = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                ints.add(values[i]);
            }
        } catch (Exception e){};
        return ints;
    }

    @Override
    public long getLong(String key) {
        return this.bundle.getLong(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return this.bundle.getLongArray(key);
    }

    @Override
    public List<Long> getLongList(String key, Class<? extends List> listtype) {
        long[] values = this.bundle.getLongArray(key);
        List<Long> longs = null;
        try {
            longs = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                longs.add(values[i]);
            }
        } catch (Exception e){};
        return longs;
    }

    @Override
    public double getDouble(String key) {
        return this.bundle.getDouble(key);
    }

    @Override
    public double[] getDoubleArray(String key) {
        return this.bundle.getDoubleArray(key);
    }

    @Override
    public List<Double> getDoubleList(String key, Class<? extends List> listtype) {
        double[] values = this.bundle.getDoubleArray(key);
        List<Double> doubles = null;
        try {
            doubles = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                doubles.add(values[i]);
            }
        } catch (Exception e){};
        return doubles;
    }

    @Override
    public float getFloat(String key) {
        return this.bundle.getFloat(key);
    }

    @Override
    public float[] getFloatArray(String key) {
        return this.bundle.getFloatArray(key);
    }

    @Override
    public List<Float> getFloatList(String key, Class<? extends List> listtype) {
        float[] values = this.bundle.getFloatArray(key);
        List<Float> floats = null;
        try {
            floats = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                floats.add(values[i]);
            }
        } catch (Exception e){};
        return floats;
    }
}