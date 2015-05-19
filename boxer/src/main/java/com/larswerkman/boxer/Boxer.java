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
package com.larswerkman.boxer;

import com.larswerkman.boxer.internal.BoxerProcessor;
import com.larswerkman.boxer.wrappers.android.BundleWrapper;
import com.larswerkman.boxer.wrappers.android.DataMapWrapper;
import com.larswerkman.boxer.wrappers.android.ParcelWrapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Boxer class used for serialization.
 *
 * <p>
 *     Use {@link #from(Object)} to retrieve the correct Wrapper for serialization
 * </p>
 */
public abstract class Boxer {

    private static HashMap<Class, Class<? extends Boxer>> wrappers = new HashMap<Class, Class<? extends Boxer>>();

    /**
     * Empty constructor, Can't be a generic type because of ClassNotFoundException
     * @param object Serialization object
     */
    public Boxer(Object object) {
        //Ensure the wrapper classes have a default constructor
    }

    /**
     * Retrieve correct wrapper of object for serialization
     * @param object find wrapper for following objects:
     *               <p>
     *               {@link android.os.Bundle}. <br>
     *               {@link android.os.Parcel}. <br>
     *               {@link com.google.android.gms.wearable.DataMap}
     *               </p>
     * @return instance of {@link com.larswerkman.boxer.Boxer}
     * or Null if there's no wrapper class known.
     */
    public static Boxer from(Object object) {
        try {
            if (Class.forName("android.os.Bundle")
                    .isAssignableFrom(object.getClass())) {
                return new BundleWrapper(object);
            }
        } catch (ClassNotFoundException e) {/*Do nothing*/}
        try {
            if (Class.forName("android.os.Parcel")
                    .isAssignableFrom(object.getClass())) {
                return new ParcelWrapper(object);
            }
        } catch (ClassNotFoundException e) {/*Do nothing*/}
        try {
            if (Class.forName("com.google.android.gms.wearable.DataMap")
                    .isAssignableFrom(object.getClass())) {
                return new DataMapWrapper(object);
            }
        } catch (ClassNotFoundException e) {/*Do nothing*/}
        try {
            Class<? extends Boxer> wrapper = null;
            if(wrappers.containsKey(object.getClass())){
                wrapper = wrappers.get(object.getClass());
            } else {
                for(Class wrapperClass : wrappers.keySet()){
                    if(wrapperClass.isAssignableFrom(object.getClass())){
                        wrapper = wrappers.get(object.getClass());
                        break;
                    }
                }
            }
            if(wrapper != null) {
                return wrapper.getDeclaredConstructor(Object.class).newInstance(object);
            }
        } catch (Exception e){/*Do nothing*/}
        return null;
    }

    /**
     * Globally register {@link com.larswerkman.boxer.Boxer} implementations
     * to a specified {@code Class}.
     *
     * @param wrapper The class definition of the wrapper to be registered
     * @param clazz The class definition ot which the wrapper will be bound.
     */
    public static void registerWrapper(Class<? extends Boxer> wrapper, Class clazz){
        if(wrapper == null){
            throw new IllegalArgumentException("When adding a wrapper, the wrapper can't be null");
        } else if(clazz == null){
            throw new IllegalArgumentException("When adding a wrapper, the class can't be null");
        }
        wrappers.put(clazz, wrapper);
    }

    /**
     * Globally remove a registered wrapper of the same type.
     *
     * @param clazz The class definition of the wrapper that needs to be removed.
     * @return The class definition to which the wrapper was registered,
     *         will return null if no wrapper is removed
     */
    public static Class removeWrapper(Class<? extends Boxer> clazz){
        if(wrappers.containsValue(clazz)){
            for(Map.Entry<Class, Class<? extends Boxer>> entry : wrappers.entrySet()){
                if(entry.getValue() == clazz) {
                    wrappers.remove(entry.getKey());
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Globally remove a registered wrapper that is registered for a certain type.
     *
     * @param clazz The class definition to which the wrapper is bound that needs to be removed
     * @return The class definition of the wrapper that has been removed;
     */
    public static Class<? extends Boxer> removeWrapperForType(Class clazz){
        return wrappers.remove(clazz);
    }

    /**
     * Globally clears all wrappers.
     */
    public static void clearWrappers(){
        wrappers.clear();
    }

    /**
     * Stores a filled {@link com.larswerkman.boxer.Boxable} object.
     *
     * @param sub Subclass of the abstract {@link com.larswerkman.boxer.Boxer} class
     * @param boxable object to be stored
     * @param object Object to store the boxable in
     * @param <A> Subclass of {@link com.larswerkman.boxer.Boxer}
     * @param <B> Should implement the {@link com.larswerkman.boxer.Boxable} interface
     * @param <T> Generic serialization object
     * @return A the filled instance of the T object that is given
     */
    protected <A extends Boxer, B extends Boxable, T> T storeBoxable(Class<A> sub, B boxable, T object){
        try {
            Class boxer = Class.forName(boxable.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, boxable.getClass(), Boxer.class);
            A wrapper = sub.getDeclaredConstructor(Object.class).newInstance(object);
            method.invoke(null, boxable, wrapper);
        } catch (Exception e){}
        return object;
    }

    /**
     * Retrieves a filled {@link com.larswerkman.boxer.Boxable} object.
     *
     * @param sub Subclass of the abstract {@link com.larswerkman.boxer.Boxer} class
     * @param boxable Type of stored boxable
     * @param object Object to retrieve data from.
     * @param <A> Subclass of {@link com.larswerkman.boxer.Boxer}
     * @param <B> Should implement the {@link com.larswerkman.boxer.Boxable} interface
     * @param <T> Generic serialization object
     * @return A restored {@link com.larswerkman.boxer.Boxable} object of type A
     */
    protected <A extends Boxer, B extends Boxable, T> B retrieveBoxable(Class<A> sub, Class<B> boxable, T object){
        B value = null;
        try {
            Class boxer = Class.forName(boxable.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, Boxer.class);
            A wrapper = sub.getDeclaredConstructor(Object.class).newInstance(object);
            value = (B) method.invoke(null, wrapper);
        } catch (Exception e){};
        return value;
    }

    /*
        Add methods
     */

    /**
     * Inserts a Boxable value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boxable object, or null
     * @param <T> should extend a Boxable
     */
    public abstract <T extends Boxable> void addBoxable(String key, T value);

    /**
     * Inserts a Boxable List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boxable List object, or null
     * @param <T> should implement a Boxable interface
     */
    public abstract <T extends Boxable> void addBoxableList(String key, List<T> value);

    /**
     * Inserts a Boxable array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boxable array object, or null
     * @param <T> should implement a Boxable interface
     */
    public abstract <T extends Boxable> void addBoxableArray(String key, T[] value);

    /**
     * Inserts an Enum value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Enum object, or null
     */
    public abstract void addEnum(String key, Enum value);

    /**
     * Inserts an Enum array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Enum array object, or null
     */
    public abstract void addEnumArray(String key, Enum[] value);

    /**
     * Inserts an Enum List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Enum List object, or null
     */
    public abstract void addEnumList(String key, List<? extends Enum> value);

    /**
     * Inserts an String value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an String object, or null
     */
    public abstract void addString(String key, String value);

    /**
     * Inserts a String array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a String array object, or null
     */
    public abstract void addStringArray(String key, String[] value);

    /**
     * Inserts a String List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a String List object, or null
     */
    public abstract void addStringList(String key, List<String> value);

    /**
     * Inserts a Boolean value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boolean, or null
     */
    public abstract void addBoolean(String key, boolean value);

    /**
     * Inserts a Boolean array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boolean array object, or null
     */
    public abstract void addBooleanArray(String key, boolean[] value);

    /**
     * Inserts a Boolean List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Boolean List object, or null
     */
    public abstract void addBooleanList(String key, List<Boolean> value);

    /**
     * Inserts a Byte value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Byte, or null
     */
    public abstract void addByte(String key, byte value);

    /**
     * Inserts a Byte array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Byte array object, or null
     */
    public abstract void addByteArray(String key, byte[] value);

    /**
     * Inserts a Byte List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Byte List object, or null
     */
    public abstract void addByteList(String key, List<Byte> value);

    /**
     * Inserts a Character value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Character, or null
     */
    public abstract void addChar(String key, char value);

    /**
     * Inserts a Character array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Character array object, or null
     */
    public abstract void addCharArray(String key, char[] value);

    /**
     * Inserts a Character List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Character List object, or null
     */
    public abstract void addCharList(String key, List<Character> value);

    /**
     * Inserts a Short value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Short, or null
     */
    public abstract void addShort(String key, short value);

    /**
     * Inserts a Short array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Short array object, or null
     */
    public abstract void addShortArray(String key, short[] value);

    /**
     * Inserts a Short List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Short List object, or null
     */
    public abstract void addShortList(String key, List<Short> value);

    /**
     * Inserts an Integer value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Integer, or null
     */
    public abstract void addInt(String key, int value);

    /**
     * Inserts an Integer array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Integer array object, or null
     */
    public abstract void addIntArray(String key, int[] value);

    /**
     * Inserts an Integer List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value an Integer List object, or null
     */
    public abstract void addIntList(String key, List<Integer> value);

    /**
     * Inserts a Long value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Long, or null
     */
    public abstract void addLong(String key, long value);

    /**
     * Inserts a Long array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Long array object, or null
     */
    public abstract void addLongArray(String key, long[] value);

    /**
     * Inserts a Long List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Long List object, or null
     */
    public abstract void addLongList(String key, List<Long> value);

    /**
     * Inserts a Double value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Double, or null
     */
    public abstract void addDouble(String key, double value);

    /**
     * Inserts a Double array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Double array object, or null
     */
    public abstract void addDoubleArray(String key, double[] value);

    /**
     * Inserts a Double List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Double List object, or null
     */
    public abstract void addDoubleList(String key, List<Double> value);

    /**
     * Inserts a Float value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Float, or null
     */
    public abstract void addFloat(String key, float value);

    /**
     * Inserts a Float array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Float array object, or null
     */
    public abstract void addFloatArray(String key, float[] value);

    /**
     * Inserts a Float List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key a String
     * @param value a Float List object, or null
     */
    public abstract void addFloatList(String key, List<Float> value);

    /*
        Get methods
     */

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param <T> should implement a Boxable interface
     * @param key a String
     * @param clazz type of Boxable expected class
     * @return a Boxable value, or null
     */
    public abstract <T extends Boxable> T getBoxable(String key, Class<T> clazz);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param clazz type of Boxable expected class
     * @param <T> should implement a Boxable interface
     * @return a Boxable[] value, or null
     */
    public abstract <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param clazz type of Boxable expected class
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> should implement a Boxable interface
     * @param <E> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends Boxable, E extends List<T>> E getBoxableList(String key, Class<T> clazz, Class<E> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param clazz type of Enum expected class
     * @param <T> should extend the Enum class
     * @return an Enum value, or null
     */
    public abstract <T extends Enum> T getEnum(String key, Class<T> clazz);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param clazz type of Enum expected class
     * @param <T> should extend the Enum class
     * @return an Enum[] value, or null
     */
    public abstract <T extends Enum> T[] getEnumArray(String key, Class<T> clazz);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param <T> should extend the Enum class
     * @param <E> Type of the List that will be instantiated
     * @param key a String
     * @param clazz type of Enum expected class
     * @param listtype type of expected List, should have a no-args constructor.
     * @return an List value, or null
     */
    public abstract <T extends Enum, E extends List<T>> List<T> getEnumList(String key, Class<T> clazz, Class<E> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a String value, or null
     */
    public abstract String getString(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a String[] value, or null
     */
    public abstract String[] getStringArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<String>> T getStringList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Boolean value, or null
     */
    public abstract boolean getBoolean(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Boolean value, or null
     */
    public abstract boolean[] getBooleanArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Boolean>> T getBooleanList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Byte value, or null
     */
    public abstract byte getByte(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Byte[] value, or null
     */
    public abstract byte[] getByteArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Byte>> T getByteList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Character value, or null
     */
    public abstract char getChar(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Character[] value, or null
     */
    public abstract char[] getCharArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Character>> T getCharList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Short value, or null
     */
    public abstract short getShort(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Short[] value, or null
     */
    public abstract short[] getShortArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Short>> T getShortList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return an Integer value, or null
     */
    public abstract int getInt(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return an Integer[] value, or null
     */
    public abstract int[] getIntArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Integer>> T getIntList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Long value, or null
     */
    public abstract long getLong(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Long[] value, or null
     */
    public abstract long[] getLongArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Long>> T getLongList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Double value, or null
     */
    public abstract double getDouble(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Double[] value, or null
     */
    public abstract double[] getDoubleArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Double>> T getDoubleList(String key, Class<T> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Float value, or null
     */
    public abstract float getFloat(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @return a Float[] value, or null
     */
    public abstract float[] getFloatArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Float>> T getFloatList(String key, Class<T> listtype);
}
