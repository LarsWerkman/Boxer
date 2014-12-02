package com.larswerkman.boxer;

import com.larswerkman.boxer.android.BundleWrapper;
import com.larswerkman.boxer.android.ParcelWrapper;
import java.util.List;

/**
 * Created by lars on 25-11-14.
 */
public abstract class Boxer {

    protected Boxer(Object object){
        //Empty constructor for wrapper class
    }

    public static Boxer from(Object object){
        try{
            if(Class.forName("android.os.Bundle")
                    .isAssignableFrom(object.getClass())){
                return new BundleWrapper(object);
            }
        } catch (ClassNotFoundException e){/*Do nothing*/}
        try{
            if(Class.forName("android.os.Parcel")
                    .isAssignableFrom(object.getClass())){
                return new ParcelWrapper(object);
            }
        } catch (ClassNotFoundException e){/*Do nothing*/}
        return null;
    }

    /*
        Add methods
     */
    public abstract <T extends Boxable> void addBoxable(String key, T value);
    public abstract <T extends Boxable> void addBoxableList(String key, List<T> value);
    public abstract <T extends Boxable> void addBoxableArray(String key, T[] value);

    public abstract void addEnum(String key, Enum value);
    public abstract void addEnumArray(String key, Enum[] value);
    public abstract void addEnumList(String key, List<Enum> value);

    public abstract void addBoolean(String key, boolean value);
    public abstract void addBooleanArray(String key, boolean[] value);
    public abstract void addBooleanList(String key, List<Boolean> value);

    public abstract void addByte(String key, byte value);
    public abstract void addByteArray(String key, byte[] value);
    public abstract void addByteList(String key, List<Byte> value);

    public abstract void addChar(String key, char value);
    public abstract void addCharArray(String key, char[] value);
    public abstract void addCharList(String key, List<Character> value);

    public abstract void addShort(String key, short value);
    public abstract void addShortArray(String key, short[] value);
    public abstract void addShortList(String key, List<Short> value);

    public abstract void addInt(String key, int value);
    public abstract void addIntArray(String key, int[] value);
    public abstract void addIntList(String key, List<Integer> value);

    public abstract void addLong(String key, long value);
    public abstract void addLongArray(String key, long[] value);
    public abstract void addLongList(String key, List<Long> value);

    public abstract void addDouble(String key, double value);
    public abstract void addDoubleArray(String key, double[] value);
    public abstract void addDoubleList(String key, List<Double> value);

    public abstract void addFloat(String key, float value);
    public abstract void addFloatArray(String key, float[] value);
    public abstract void addFloatList(String key, List<Float> value);

    /*
        Get methods
     */
    public abstract <T extends Boxable> T getBoxable(String key, Class<T> clazz);
    public abstract <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz);
    public abstract <T extends Boxable> List<T> getBoxableList(String key, Class<T> clazz, Class<? extends List> listtype);

    public abstract <T extends Enum> T getEnum(String key, Class<T> clazz);
    public abstract <T extends Enum> T[] getEnumArray(String key, Class<T> clazz);
    public abstract <T extends Enum> List<T> getEnumList(String key, Class<T> clazz, Class<? extends List> listtype);

    public abstract String getString(String key);
    public abstract String[] getStringArray(String key);
    public abstract List<String> getStringList(String key, Class<? extends List> listtype);

    public abstract boolean getBoolean(String key);
    public abstract boolean[] getBooleanArray(String key);
    public abstract List<Boolean> getBooleanList(String key, Class<? extends List> listtype);

    public abstract byte getByte(String key);
    public abstract byte[] getByteArray(String key);
    public abstract List<Byte> getByteList(String key, Class<? extends List> listtype);

    public abstract char getChar(String key);
    public abstract char[] getCharArray(String key);
    public abstract List<Character> getCharList(String key, Class<? extends List> listtype);

    public abstract short getShort(String key);
    public abstract short[] getShortArray(String key);
    public abstract List<Short> getShortList(String key, Class<? extends List> listtype);

    public abstract int getInt(String key);
    public abstract int[] getIntArray(String key);
    public abstract List<Integer> getIntList(String key, Class<? extends List> listtype);

    public abstract long getLong(String key);
    public abstract long[] getLongArray(String key);
    public abstract List<Long> getLongList(String key, Class<? extends List> listtype);

    public abstract double getDouble(String key);
    public abstract double[] getDoubleArray(String key);
    public abstract List<Double> getDoubleList(String key, Class<? extends List> listtype);

    public abstract float getFloat(String key);
    public abstract float[] getFloatArray(String key);
    public abstract List<Float> getFloatList(String key, Class<? extends List> listtype);
}
