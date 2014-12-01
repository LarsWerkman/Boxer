package com.larswerkman.boxer.android;

import android.os.Bundle;
import android.os.Parcel;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.internal.BoxerProcessor;

import java.lang.reflect.Method;

/**
 * Created by Lars Werkman
 *
 * Parcel should only be used when you don't
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
        Bundle bundle = new Bundle();
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass(), Boxer.class);
            method.invoke(null, value, this);
        } catch (Exception e){/*TODO not empty constructor for T class*/}
    }

    @Override
    public void addEnum(String key, Enum value) {
        parcel.writeString(value.name());
    }

    @Override
    public void addString(String key, String value) {
        parcel.writeString(value);
    }

    @Override
    public void addBoolean(String key, boolean value) {
        parcel.writeString(value ? "true" : "false");
    }

    @Override
    public void addByte(String key, byte value) {
        parcel.writeByte(value);
    }

    @Override
    public void addChar(String key, char value) {
        parcel.writeString(String.valueOf(value));
    }

    @Override
    public void addShort(String key, short value) {
        parcel.writeInt(value);
    }

    @Override
    public void addInt(String key, int value) {
        parcel.writeInt(value);
    }

    @Override
    public void addLong(String key, long value) {
        parcel.writeLong(value);
    }

    @Override
    public void addDouble(String key, double value) {
        parcel.writeDouble(value);
    }

    @Override
    public void addFloat(String key, float value) {
        parcel.writeFloat(value);
    }

    @Override
    public <T extends Boxable> T get(String key, Class<T> clazz) {
        try {
            Class boxer = Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, Boxer.class);
            return (T) method.invoke(null, this);
        } catch (Exception e){};
        return null;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            return (T) method.invoke(null, parcel.readString());
        } catch (Exception e){}
        return null;
    }

    @Override
    public String getString(String key) {
        return parcel.readString();
    }

    @Override
    public boolean getBoolean(String key) {
        return parcel.readString().equals("true");
    }

    @Override
    public byte getByte(String key) {
        return parcel.readByte();
    }

    @Override
    public char getChar(String key) {
        return parcel.readString().charAt(0);
    }

    @Override
    public short getShort(String key) {
        return (short) parcel.readInt();
    }

    @Override
    public int getInt(String key) {
        return parcel.readInt();
    }

    @Override
    public long getLong(String key) {
        return parcel.readLong();
    }

    @Override
    public double getDouble(String key) {
        return parcel.readDouble();
    }

    @Override
    public float getFloat(String key) {
        return parcel.readFloat();
    }
}
