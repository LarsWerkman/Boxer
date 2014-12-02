package com.larswerkman.boxer.wrappers.android;

import com.google.android.gms.wearable.DataMap;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.internal.BoxerProcessor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by klm62637 on 12/2/2014.
 */
public class DataMapWrapper extends Boxer {

    private DataMap dataMap;

    protected DataMapWrapper(Object object) {
        super(object);
        dataMap = (DataMap) object;
    }

    private <T extends Boxable> DataMap storeBoxable(T value){
        DataMap dataMap = new DataMap();
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass(), Boxer.class);
            method.invoke(null, value, new DataMapWrapper(dataMap));
        } catch (Exception e){}
        return dataMap;
    }

    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        this.dataMap.putDataMap(key, storeBoxable(value));
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {
        ArrayList<DataMap> dataMaps = new ArrayList<DataMap>();
        for(T box : value){
            dataMaps.add(storeBoxable(box));
        }
        this.dataMap.putDataMapArrayList(key, dataMaps);
    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {
        ArrayList<DataMap> dataMaps = new ArrayList<DataMap>();
        for(T box : value){
            dataMaps.add(storeBoxable(box));
        }
        this.dataMap.putDataMapArrayList(key, dataMaps);
    }

    @Override
    public void addEnum(String key, Enum value) {
        this.dataMap.putString(key, value.name());
    }

    @Override
    public void addEnumArray(String key, Enum[] value) {
        String[] strings = new String[value.length];
        for(int i = 0; i < strings.length; i++){
            strings[i] = value[i].name();
        }
        this.dataMap.putStringArray(key, strings);
    }

    @Override
    public void addEnumList(String key, List<Enum> value) {
        String[] strings = new String[value.size()];
        for(int i = 0; i < value.size(); i++){
            strings[i] = value.get(i).name();
        }
        this.dataMap.putStringArray(key, strings);
    }

    @Override
    public void addString(String key, String value) {
        this.dataMap.putString(key, value);
    }

    @Override
    public void addStringArray(String key, String[] value) {
        this.dataMap.putStringArray(key, value);
    }

    @Override
    public void addStringList(String key, List<String> value) {
        this.dataMap.putStringArray(key, (String[]) value.toArray());
    }

    @Override
    public void addBoolean(String key, boolean value) {
        this.dataMap.putBoolean(key, value);
    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {
        byte[] bytes = new byte[value.length];
        for(int i = 0; i < value.length; i++){
            bytes[i] = value[i] ? Byte.MAX_VALUE : Byte.MIN_VALUE;
        }
        this.dataMap.putByteArray(key, bytes);
    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {
        byte[] bytes = new byte[value.size()];
        for(int i = 0; i < value.size(); i++){
            bytes[i] = value.get(i) ? Byte.MAX_VALUE : Byte.MIN_VALUE;
        }
        this.dataMap.putByteArray(key, bytes);
    }

    @Override
    public void addByte(String key, byte value) {
        this.dataMap.putByte(key, value);
    }

    @Override
    public void addByteArray(String key, byte[] value) {
        this.dataMap.putByteArray(key, value);
    }

    @Override
    public void addByteList(String key, List<Byte> value) {
        byte[] bytes = new byte[value.size()];
        for(int i = 0; i < value.size(); i++){
            bytes[i] = value.get(i);
        }
        this.dataMap.putByteArray(key, bytes);
    }

    @Override
    public void addChar(String key, char value) {
        this.dataMap.putString(key, Character.toString(value));
    }

    @Override
    public void addCharArray(String key, char[] value) {
        this.dataMap.putString(key, String.valueOf(value));
    }

    @Override
    public void addCharList(String key, List<Character> value) {
        char[] chars = new char[value.size()];
        for(int i = 0; i < value.size(); i++){
            value.get(i).toString();
            chars[i] = value.get(i);
        }
        this.dataMap.putString(key, String.valueOf(chars));
    }

    @Override
    public void addShort(String key, short value) {
        this.dataMap.putInt(key, value);
    }

    @Override
    public void addShortArray(String key, short[] value) {
        long[] chars = new long[value.length];
        for(int i = 0; i < value.length; i++){
            chars[i] = value[i];
        }
        this.dataMap.putLongArray(key, chars);
    }

    @Override
    public void addShortList(String key, List<Short> value) {
        long[] chars = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            chars[i] = value.get(i);
        }
        this.dataMap.putLongArray(key, chars);
    }

    @Override
    public void addInt(String key, int value) {
        this.dataMap.putInt(key, value);
    }

    @Override
    public void addIntArray(String key, int[] value) {
        long[] chars = new long[value.length];
        for(int i = 0; i < value.length; i++){
            chars[i] = value[i];
        }
        this.dataMap.putLongArray(key, chars);
    }

    @Override
    public void addIntList(String key, List<Integer> value) {
        long[] chars = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            chars[i] = value.get(i);
        }
        this.dataMap.putLongArray(key, chars);
    }

    @Override
    public void addLong(String key, long value) {
        this.dataMap.putLong(key, value);
    }

    @Override
    public void addLongArray(String key, long[] value) {
        this.dataMap.putLongArray(key, value);
    }

    @Override
    public void addLongList(String key, List<Long> value) {
        long[] longs = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            longs[i] = value.get(i);
        }
        this.dataMap.putLongArray(key, longs);
    }

    @Override
    public void addDouble(String key, double value) {
        this.dataMap.putDouble(key, value);
    }

    @Override
    public void addDoubleArray(String key, double[] value) {
        float[] floats = new float[value.length];
        for(int i = 0; i < value.length; i++){
            floats[i] = (float) value[i];
        }
        this.dataMap.putFloatArray(key, floats);
    }

    @Override
    public void addDoubleList(String key, List<Double> value) {
        float[] floats = new float[value.size()];
        for(int i = 0; i < value.size(); i++){
            floats[i] = value.get(i).floatValue();
        }
        this.dataMap.putFloatArray(key, floats);
    }

    @Override
    public void addFloat(String key, float value) {
        this.dataMap.putFloat(key, value);
    }

    @Override
    public void addFloatArray(String key, float[] value) {
        this.dataMap.putFloatArray(key, value);
    }

    @Override
    public void addFloatList(String key, List<Float> value) {
        float[] floats = new float[value.size()];
        for(int i = 0; i < value.size(); i++){
            floats[i] = value.get(i);
        }
        this.dataMap.putFloatArray(key, floats);
    }

    public <T extends Boxable> T retrieveBoxable(DataMap dataMap, Class<T> clazz){
        try {
            Class boxer = Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, Boxer.class);
            return (T) method.invoke(null, new DataMapWrapper(dataMap));
        } catch (Exception e){};
        return null;
    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        return retrieveBoxable(this.dataMap.getDataMap(key), clazz);
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        DataMap dataMap = this.dataMap.getDataMap(key);
        int size = dataMap.getInt("size");
        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = retrieveBoxable(dataMap.getDataMap(String.valueOf(i)), clazz);
        }
        return boxables;
    }

    @Override
    public <T extends Boxable> List<T> getBoxableList(String key, Class<T> clazz, Class<? extends List> listtype) {
        DataMap dataMap = this.dataMap.getDataMap(key);
        int size = dataMap.getInt("size");
        List<T> boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(retrieveBoxable(dataMap.getDataMap(String.valueOf(i)), clazz));
            }
        } catch (Exception e){};
        return boxables;
    }

    public <T extends Enum> T retrieveEnum(String value, Class<T> clazz){
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            return (T) method.invoke(null, value);
        } catch (Exception e){}
        return null;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return retrieveEnum(this.dataMap.getString(key), clazz);
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        String[] values = this.dataMap.getStringArray(key);
        T[] enums = (T[]) Array.newInstance(clazz, values.length);
        for(int i = 0; i < values.length; i++){
            enums[i] = retrieveEnum(values[i], clazz);
        }
        return enums;
    }

    @Override
    public <T extends Enum> List<T> getEnumList(String key, Class<T> clazz, Class<? extends List> listtype) {
        String[] values = this.dataMap.getStringArray(key);
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
        return this.dataMap.getString(key);
    }

    @Override
    public String[] getStringArray(String key) {
        return this.dataMap.getStringArray(key);
    }

    @Override
    public List<String> getStringList(String key, Class<? extends List> listtype) {
        String[] values = this.dataMap.getStringArray(key);
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
        return this.dataMap.getBoolean(key);
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        byte[] bytes = this.dataMap.getByteArray(key);
        boolean[] bools = new boolean[bytes.length];
        for(int i = 0; i < bytes.length; i++){
            bools[i] = bytes[i] > Byte.MIN_VALUE;
        }
        return bools;
    }

    @Override
    public List<Boolean> getBooleanList(String key, Class<? extends List> listtype) {
        byte[] bytes= this.dataMap.getByteArray(key);
        List<Boolean> booleans = null;
        try {
            booleans = listtype.newInstance();
            for (int i = 0; i < bytes.length; i++) {
                booleans.add(bytes[i] > Byte.MIN_VALUE);
            }
        } catch (Exception e){};
        return booleans;
    }

    @Override
    public byte getByte(String key) {
        return this.dataMap.getByte(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return this.dataMap.getByteArray(key);
    }

    @Override
    public List<Byte> getByteList(String key, Class<? extends List> listtype) {
        byte[] values = this.dataMap.getByteArray(key);
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
        return this.dataMap.getString(key).charAt(0);
    }

    @Override
    public char[] getCharArray(String key) {
        return this.dataMap.getString(key).toCharArray();
    }

    @Override
    public List<Character> getCharList(String key, Class<? extends List> listtype) {
        char[] values = this.dataMap.getString(key).toCharArray();
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
        return (short) this.dataMap.getInt(key);
    }

    @Override
    public short[] getShortArray(String key) {
        long[] values = this.dataMap.getLongArray(key);
        short[] shorts = new short[values.length];
        for(int i = 0; i < values.length; i++){
            shorts[i] = (short) values[i];
        }
        return shorts;
    }

    @Override
    public List<Short> getShortList(String key, Class<? extends List> listtype) {
        long[] values = this.dataMap.getLongArray(key);
        List<Short> shorts = null;
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
        return this.dataMap.getInt(key);
    }

    @Override
    public int[] getIntArray(String key) {
        long[] values = this.dataMap.getLongArray(key);
        int[] ints = new int[values.length];
        for(int i = 0; i < ints.length; i++){
            ints[i] = (int) values[i];
        }
        return ints;
    }

    @Override
    public List<Integer> getIntList(String key, Class<? extends List> listtype) {
        long[] values = this.dataMap.getLongArray(key);
        List<Integer> ints = null;
        try {
            ints = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                ints.add((int) values[i]);
            }
        } catch (Exception e){};
        return ints;
    }

    @Override
    public long getLong(String key) {
        return this.dataMap.getLong(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return this.dataMap.getLongArray(key);
    }

    @Override
    public List<Long> getLongList(String key, Class<? extends List> listtype) {
        long[] values = this.dataMap.getLongArray(key);
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
        return this.dataMap.getDouble(key);
    }

    @Override
    public double[] getDoubleArray(String key) {
        float[] values = this.dataMap.getFloatArray(key);
        double[] doubles = new double[values.length];
        for(int i = 0; i < doubles.length; i++){
            doubles[i] = (double) values[i];
        }
        return doubles;
    }

    @Override
    public List<Double> getDoubleList(String key, Class<? extends List> listtype) {
        float[] values = this.dataMap.getFloatArray(key);
        List<Double> doubles = null;
        try {
            doubles = listtype.newInstance();
            for (int i = 0; i < values.length; i++) {
                doubles.add(new Double(values[i]));
            }
        } catch (Exception e){};
        return doubles;
    }

    @Override
    public float getFloat(String key) {
        return this.dataMap.getFloat(key);
    }

    @Override
    public float[] getFloatArray(String key) {
        return this.dataMap.getFloatArray(key);
    }

    @Override
    public List<Float> getFloatList(String key, Class<? extends List> listtype) {
        float[] values = this.dataMap.getFloatArray(key);
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
