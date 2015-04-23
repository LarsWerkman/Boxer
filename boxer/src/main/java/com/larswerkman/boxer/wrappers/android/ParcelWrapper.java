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
import android.os.Parcel;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * ParcelWrapper defines how the Boxer class should write to a {@link android.os.Parcel}
 *
 * ParcelWrapper should only be used when you don't
 * change the order you read and write to it.
 */
public class ParcelWrapper extends Boxer {

    Parcel parcel;

    public ParcelWrapper(Object object){
        super(object);
        parcel = (Parcel) object;
    }

    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        this.parcel.writeBundle(storeBoxable(BundleWrapper.class, value, new Bundle()));
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {
        this.parcel.writeInt(value.size());
        for(T boxable : value) {
            this.parcel.writeBundle(storeBoxable(BundleWrapper.class, boxable, new Bundle()));
        }
    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {
        this.parcel.writeInt(value.length);
        for(T boxable : value) {
            this.parcel.writeBundle(storeBoxable(BundleWrapper.class, boxable, new Bundle()));
        }
    }

    @Override
    public void addEnum(String key, Enum value) {
        this.parcel.writeString(value.name());
    }

    @Override
    public void addEnumArray(String key, Enum[] value) {
        String[] strings = new String[value.length];
        for(int i = 0; i < value.length; i++){
            strings[i] = value[i].name();
        }
        this.parcel.writeStringArray(strings);
    }

    @Override
    public void addEnumList(String key, List<? extends Enum> value) {
        String[] strings = new String[value.size()];
        for(int i = 0; i < value.size(); i++){
            strings[i] = value.get(i).name();
        }
        this.parcel.writeStringArray(strings);
    }

    @Override
    public void addString(String key, String value) {
        this.parcel.writeString(value);
    }

    @Override
    public void addStringArray(String key, String[] value) {
        this.parcel.writeStringArray(value);
    }

    @Override
    public void addStringList(String key, List<String> value) {
        this.parcel.writeStringList(value);
    }

    @Override
    public void addBoolean(String key, boolean value) {
        this.parcel.writeByte(value ? Byte.MAX_VALUE : Byte.MIN_VALUE);
    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {
        this.parcel.writeBooleanArray(value);
    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {
        boolean[] bools = new boolean[value.size()];
        for(int i = 0; i < value.size(); i++){
            bools[i] = value.get(i);
        }
        this.parcel.writeBooleanArray(bools);
    }

    @Override
    public void addByte(String key, byte value) {
        this.parcel.writeByte(value);
    }

    @Override
    public void addByteArray(String key, byte[] value) {
        this.parcel.writeByteArray(value);
    }

    @Override
    public void addByteList(String key, List<Byte> value) {
        byte[] bytes = new byte[value.size()];
        for(int i = 0; i < value.size(); i++){
            bytes[i] = value.get(i);
        }
        this.parcel.writeByteArray(bytes);
    }

    @Override
    public void addChar(String key, char value) {
        this.parcel.writeString(String.valueOf(value));
    }

    @Override
    public void addCharArray(String key, char[] value) {
        this.parcel.writeCharArray(value);
    }

    @Override
    public void addCharList(String key, List<Character> value) {
        char[] chars = new char[value.size()];
        for(int i = 0; i < value.size(); i++){
            chars[i] = value.get(i);
        }
        this.parcel.writeCharArray(chars);
    }

    @Override
    public void addShort(String key, short value) {
        this.parcel.writeInt((int) value);
    }

    @Override
    public void addShortArray(String key, short[] value) {
        int[] shorts = new int[value.length];
        for(int i = 0; i < value.length; i++){
            shorts[i] = (int) value[i];
        }
        this.parcel.writeIntArray(shorts);
    }

    @Override
    public void addShortList(String key, List<Short> value) {
        int[] shorts = new int[value.size()];
        for(int i = 0; i < value.size(); i++){
            shorts[i] = (int) value.get(i);
        }
        this.parcel.writeIntArray(shorts);
    }

    @Override
    public void addInt(String key, int value) {
        this.parcel.writeInt(value);
    }

    @Override
    public void addIntArray(String key, int[] value) {
        this.parcel.writeIntArray(value);
    }

    @Override
    public void addIntList(String key, List<Integer> value) {
        int[] ints = new int[value.size()];
        for(int i = 0; i < value.size(); i++){
            ints[i] = value.get(i);
        }
        this.parcel.writeIntArray(ints);
    }

    @Override
    public void addLong(String key, long value) {
        this.parcel.writeLong(value);
    }

    @Override
    public void addLongArray(String key, long[] value) {
        this.parcel.writeLongArray(value);
    }

    @Override
    public void addLongList(String key, List<Long> value) {
        long[] longs = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            longs[i] = value.get(i);
        }
        this.parcel.writeLongArray(longs);
    }

    @Override
    public void addDouble(String key, double value) {
        this.parcel.writeDouble(value);
    }

    @Override
    public void addDoubleArray(String key, double[] value) {
        this.parcel.writeDoubleArray(value);
    }

    @Override
    public void addDoubleList(String key, List<Double> value) {
        double[] doubles = new double[value.size()];
        for(int i = 0; i < value.size(); i++){
            doubles[i] = value.get(i);
        }
        this.parcel.writeDoubleArray(doubles);
    }

    @Override
    public void addFloat(String key, float value) {
        this.parcel.writeFloat(value);
    }

    @Override
    public void addFloatArray(String key, float[] value) {
        this.parcel.writeFloatArray(value);
    }

    @Override
    public void addFloatList(String key, List<Float> value) {
        float[] floats = new float[value.size()];
        for(int i = 0; i < value.size(); i++){
            floats[i] = value.get(i);
        }
        this.parcel.writeFloatArray(floats);
    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        return retrieveBoxable(BundleWrapper.class, clazz, this.parcel.readBundle());
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        int size = this.parcel.readInt();
        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = retrieveBoxable(BundleWrapper.class, clazz, this.parcel.readBundle());
        }
        return boxables;
    }

    @Override
    public <T extends Boxable, E extends List<T>> E getBoxableList(String key, Class<T> clazz, Class<E> listtype) {
        int size = this.parcel.readInt();
        E boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(retrieveBoxable(BundleWrapper.class, clazz, this.parcel.readBundle()));
            }
        } catch (Exception e){};
        return boxables;
    }

    public <T extends Enum> T retrieveEnum(String value, Class<T> clazz){
        T en = null;
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            en = (T) method.invoke(null, value);
        } catch (Exception e){}
        return en;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return retrieveEnum(this.parcel.readString(), clazz);
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        String[] values = this.parcel.createStringArray();
        T[] enums = (T[]) Array.newInstance(clazz, values.length);
        for(int i = 0; i < values.length; i++){
            enums[i] = retrieveEnum(values[i], clazz);
        }
        return enums;
    }

    @Override
    public <T extends Enum, E extends List<T>> E getEnumList(String key, Class<T> clazz, Class<E> listtype) {
        String[] values = this.parcel.createStringArray();
        E enums = null;
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
        return this.parcel.readString();
    }

    @Override
    public String[] getStringArray(String key) {
        return this.parcel.createStringArray();
    }

    @Override
    public <T extends List<String>> T getStringList(String key, Class<T> listtype) {
        T list = null;
        try {
            list = listtype.newInstance();
        } catch (Exception e){}
        this.parcel.readStringList(list);
        return list;
    }

    @Override
    public boolean getBoolean(String key) {
        return this.parcel.readByte() > Byte.MIN_VALUE;
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        return this.parcel.createBooleanArray();
    }

    @Override
    public <T extends List<Boolean>> T getBooleanList(String key, Class<T> listtype) {
        boolean[] values = this.parcel.createBooleanArray();
        T booleans = null;
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
        return this.parcel.readByte();
    }

    @Override
    public byte[] getByteArray(String key) {
        return this.parcel.createByteArray();
    }

    @Override
    public <T extends List<Byte>> T getByteList(String key, Class<T> listtype) {
        byte[] values = this.parcel.createByteArray();
        T bytes = null;
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
        return this.parcel.readString().charAt(0);
    }

    @Override
    public char[] getCharArray(String key) {
        return this.parcel.createCharArray();
    }

    @Override
    public <T extends List<Character>> T getCharList(String key, Class<T> listtype) {
        char[] values = this.parcel.createCharArray();
        T chars = null;
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
        return (short) this.parcel.readInt();
    }

    @Override
    public short[] getShortArray(String key) {
        int[] values = this.parcel.createIntArray();
        short[] shorts = new short[values.length];
        for(int i = 0; i < values.length; i++){
            shorts[i] = (short) values[i];
        }
        return shorts;
    }

    @Override
    public <T extends List<Short>> T getShortList(String key, Class<T> listtype) {
        int[] values = this.parcel.createIntArray();
        T shorts = null;
        try {
            shorts = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                shorts.add((short) values[i]);
            }
        } catch (Exception e){};
        return shorts;
    }

    @Override
    public int getInt(String key) {
        return this.parcel.readInt();
    }

    @Override
    public int[] getIntArray(String key) {
        return this.parcel.createIntArray();
    }

    @Override
    public <T extends List<Integer>> T getIntList(String key, Class<T> listtype) {
        int[] values = this.parcel.createIntArray();
        T ints = null;
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
        return this.parcel.readLong();
    }

    @Override
    public long[] getLongArray(String key) {
        return this.parcel.createLongArray();
    }

    @Override
    public <T extends List<Long>> T getLongList(String key, Class<T> listtype) {
        long[] values = this.parcel.createLongArray();
        T longs = null;
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
        return this.parcel.readDouble();
    }

    @Override
    public double[] getDoubleArray(String key) {
        return this.parcel.createDoubleArray();
    }

    @Override
    public <T extends List<Double>> T getDoubleList(String key, Class<T> listtype) {
        double[] values = this.parcel.createDoubleArray();
        T doubles = null;
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
        return this.parcel.readFloat();
    }

    @Override
    public float[] getFloatArray(String key) {
        return this.parcel.createFloatArray();
    }

    @Override
    public <T extends List<Float>> T getFloatList(String key, Class<T> listtype) {
        float[] values = this.parcel.createFloatArray();
        T floats = null;
        try {
            floats = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                floats.add(values[i]);
            }
        } catch (Exception e){};
        return floats;
    }
}
