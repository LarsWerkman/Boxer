package com.larswerkman.boxer;

import com.larswerkman.boxer.android.BundleWrapper;

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
        return null;
    }

    public abstract <T extends Boxable> void addBoxable(String key, T value);
    public abstract <T extends Boxable> void addBoxableArray(String key, List<T> value);
    public abstract <T extends Boxable> void addBoxableArray(String key, T[] value);

    public abstract void addEnum(String key, Enum value);
    public abstract void addString(String key, String value);

    public abstract void addBoolean(String key, boolean value);
    public abstract void addByte(String key, byte value);
    public abstract void addChar(String key, char value);
    public abstract void addShort(String key, short value);
    public abstract void addInt(String key, int value);
    public abstract void addLong(String key, long value);
    public abstract void addDouble(String key, double value);
    public abstract void addFloat(String key, float value);

    public abstract <T extends Boxable> T get(String key, Class<T> clazz);
    public abstract <T extends Enum> T getEnum(String key, Class<T> clazz);

    public abstract String getString(String key);

    public abstract boolean getBoolean(String key);
    public abstract byte getByte(String key);
    public abstract char getChar(String key);
    public abstract short getShort(String key);
    public abstract int getInt(String key);
    public abstract long getLong(String key);
    public abstract double getDouble(String key);
    public abstract float getFloat(String key);
}
