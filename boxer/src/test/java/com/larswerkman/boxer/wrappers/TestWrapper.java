package com.larswerkman.boxer.wrappers;

import com.larswerkman.boxer.Boxer;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lars on 23-04-15.
 */
public class TestWrapper extends Boxer<HashMap<String, Object>> {

    private HashMap<String, Object> map;

    /**
     * Empty constructor, Can't be a generic type because of ClassNotFoundException
     *
     * @param map Serialization object
     */
    public TestWrapper(HashMap<String, Object> map) {
        super(map);
        this.map = map;
    }

    @Override
    public void add(String key, Object value) {
        map.put(key, serialize(new TestWrapper(new HashMap<String, Object>()), value));
    }

    @Override
    public void addArray(String key, Object[] value) {
        HashMap<String, Object> boxeables = new HashMap<String, Object>();
        boxeables.put("size", value.length);
        for(int i = 0; i < value.length; i++){
            boxeables.put(String.valueOf(i), serialize(new TestWrapper(new HashMap<String, Object>()), value[i]));
        }
        map.put(key, boxeables);
    }

    @Override
    public void addList(String key, List<?> value) {
        addArray(key, value.toArray());
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
    public <T> T get(String key, Class<T> clazz) {
        HashMap<String, Object> map = (HashMap<String, Object>) this.map.get(key);
        if(map == null){
            return null;
        }
        return deserialize(new TestWrapper(map), clazz);
    }

    @Override
    public <T> T[] getArray(String key, Class<T> clazz) {
        HashMap<String, Object> values = (HashMap<String, Object>) map.get(key);
        if(values == null){
            return null;
        }

        int size = (Integer) values.get("size");
        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = deserialize(new TestWrapper((HashMap<String, Object>) values.get(String.valueOf(i))), clazz);
        }
        return boxables;
    }

    @Override
    public <T, E extends List<T>> E getList(String key, Class<T> clazz, Class<E> listtype) {
        HashMap<String, Object> values = (HashMap<String, Object>) map.get(key);
        if(values == null){
            return null;
        }

        int size = (Integer) values.get("size");
        E boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(deserialize(new TestWrapper((HashMap<String, Object>) values.get(String.valueOf(i))), clazz));
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
        Object value = map.get(key);
        if(value == null){
            return false;
        }

        return (Boolean) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Byte) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Character) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Short) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Integer) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Long) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Double) value;
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
        Object value = map.get(key);
        if(value == null){
            return 0;
        }

        return (Float) value;
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
