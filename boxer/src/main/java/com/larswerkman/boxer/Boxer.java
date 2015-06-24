/*
 * Copyright 2015 Lars Werkman
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
import com.larswerkman.boxer.wrappers.android.SQLiteWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Abstract wrapper class, used to deserialize wrapper.
 *
 * <p>
 *     Use {@link #from(Object)} to deserialize a boxer instance.
 * </p>
 *
 * @param <S> Type of wrapper
 */
public abstract class Boxer<S> {

    private static HashMap<Class, Class<? extends Boxer>> wrappers = new HashMap<Class, Class<? extends Boxer>>();
    private static HashMap<String, Class<? extends Boxer>> defaultWrappers = new HashMap<String, Class<? extends Boxer>>();

    static {
        defaultWrappers.put("android.os.Bundle", BundleWrapper.class);
        defaultWrappers.put("android.os.Parcel", ParcelWrapper.class);
        defaultWrappers.put("com.google.android.gms.wearable.DataMap", DataMapWrapper.class);
        defaultWrappers.put("android.database.sqlite.SQLiteDatabase", SQLiteWrapper.class);
    }

    private S instance;

    /**
     * Default constructor for initialization
     *
     * @param instance to serialize to and deserialize from
     */
    public Boxer(S instance) {
        this.instance = instance;
    }

    /**
     * Retrieve correct wrapper of object for serialization
     * @param object Object to find wrapper for
     *
     * @return instance of {@link com.larswerkman.boxer.Boxer}
     * or Null if there's no wrapper class known.
     */
    @SuppressWarnings("unchecked")
    public static <T> Boxer<T> from(T object) {
        for(String target : defaultWrappers.keySet()){
            try{
                if(Class.forName(target).isAssignableFrom(object.getClass())){
                    return defaultWrappers.get(target).getDeclaredConstructor(object.getClass()).newInstance(object);
                }
            } catch (Exception ignored){}
        }

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
                return wrapper.getDeclaredConstructor(object.getClass()).newInstance(object);
            }
        } catch (Exception ignored){}
        return null;
    }

    /**
     * Returns the fields of a specific class which will be serialized.
     *
     * @param clazz The boxbale class you want to inspect
     * @return An {@link java.util.List} of fields, will return an empty list if there are none.
     */
    public static List<Field> getBoxableFields(Class<? extends Boxable> clazz){
        List<Field> fields = new ArrayList<Field>();
        for(Field field : clazz.getDeclaredFields()){
            if(!Modifier.isTransient(field.getModifiers())){
                fields.add(field);
            }
        }

        Class superClass = clazz.getSuperclass();
        while(superClass != null){
            for(Field field : superClass.getDeclaredFields()){
                if(!fields.contains(field) && !Modifier.isTransient(field.getModifiers())){
                    fields.add(field);
                }
            }
            superClass = superClass.getSuperclass();
        }
        return fields;
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
     * Helper method used to serialize an object which has a
     * {@link TypeAdapter} to the specified wrapper.
     *
     * @param wrapper {@link Boxer} instance to serialize to.
     * @param value Object which has a {@link TypeAdapter}
     *
     * @return The wrappers instance to which the value has been serialized.
     */
    @SuppressWarnings("unchecked")
    protected <T, U> U serialize(Boxer<U> wrapper, T value){
        getTypeAdapter((Class<T>) value.getClass()).serialize(wrapper, value);
        return wrapper.instance;
    }

    /**
     * Helper method to serialize a {@link Boxable} class to the specified wrapper.
     *
     * @param wrapper {@link Boxer} instance to serialize to.
     * @param boxable Object of type {@link Boxable} to be serialized.
     *
     * @return The wrappers {@link #instance} to which the value has been serialized.
     */
    @SuppressWarnings("unchecked")
    protected <B extends Boxable, U> U serializeBoxable(Boxer<U> wrapper, B boxable){
        try {
            Class boxer = boxableClass(boxable.getClass());
            Method method = boxer.getMethod(BoxerProcessor.METHOD_SERIALIZE, boxable.getClass(), Boxer.class);
            method.invoke(null, boxable, wrapper);
        } catch (Exception ignored){}
        return wrapper.instance;
    }

    /**
     * Helper method to deserialize a specific type which has a {@link TypeAdapter}
     * from a specific wrapper.
     *
     * @param wrapper {@link Boxer} instance to deserialize from.
     * @param type Type which has a {@link TypeAdapter}.
     *
     * @return a deserialized instance of the give type.
     */
    protected <T> T deserialize(Boxer wrapper, Class<T> type){
        return getTypeAdapter(type).deserialize(wrapper);
    }

    /**
     * Helper method to deserialize a {@link Boxable} class from a specific wrapper.
     *
     * @param wrapper {@link Boxer} instance to deserialize from.
     * @param boxable Type of {@link Boxable} to be deserialized
     *
     * @return a deserialized instance of the given type.
     */
    @SuppressWarnings("unchecked")
    protected  <B extends Boxable> B deserializeBoxable(Boxer wrapper, Class<B> boxable){
        B value = null;
        try{
            Class boxer = boxableClass(boxable);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_DESERIALIZE, Boxer.class);
            value = (B) method.invoke(null, wrapper);
        } catch (Exception ignored){}
        return value;
    }

    /**
     * Retrieve instance of {@link TypeAdapter} for serialization and deserialization.
     *
     * @param clazz Type which has a {@link TypeAdapter}.
     *
     * @return instance of {@link TypeAdapter} will throw an
     * {@link IllegalArgumentException} if there's no {@link TypeAdapter} found.
     */
    @SuppressWarnings("unchecked")
    private static <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) {
        TypeAdapter<T> adapter = null;
        try{
            Class adapters = Class.forName(BoxerProcessor.ADAPTER_PACKAGE_NAME + "." + BoxerProcessor.ADAPTER_CLASS_NAME);
            Method method = adapters.getMethod(BoxerProcessor.ADAPTER_METHOD_GET, Class.class);
            adapter = (TypeAdapter<T>) method.invoke(null, clazz);
        } catch (Exception ignored){}

        if(adapter == null){
            throw new IllegalArgumentException(
                    String.format("No TypeAdapter found for the class %s", clazz.getCanonicalName()));
        }
        return adapter;
    }

    /**
     * Returns a Class object of {@link com.larswerkman.boxer.annotations.Box}
     * class of a {@link Boxable} class.
     *
     * @param clazz {@link Boxable} class.
     *
     * @return {@link Class} for the {@link com.larswerkman.boxer.annotations.Box} class of a {@link Boxable}.
     */
    private static <T extends Boxable> Class<?> boxableClass(Class<T> clazz) throws ClassNotFoundException {
        return Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
    }

    /**************************
        Abstract Add methods
     **************************/

    /**
     * Insert an Object value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * Will throw an {@link IllegalArgumentException} if there's no {@link TypeAdapter}
     * for the value Class.
     *
     * @param key an unique identifier
     * @param value a Object which has an {@link TypeAdapter}, or null.
     */
    public abstract void add(String key, Object value);

    /**
     * Insert an Object array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * Will throw an {@link IllegalArgumentException} if there's no {@link TypeAdapter}
     * for the value Class.
     *
     * @param key an unique identifier
     * @param value a Object array which has an {@link TypeAdapter}, or null.
     */
    public abstract void addArray(String key, Object[] value);

    /**
     * Insert an Object List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * Will throw an {@link IllegalArgumentException} if there's no {@link TypeAdapter}
     * for the value Class.
     *
     * @param key an unique identifier
     * @param value a List array which has an {@link TypeAdapter}, or null.
     */
    public abstract void addList(String key, List<?> value);

    /**
     * Inserts a Boxable value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boxable object, or null
     * @param <T> should extend a Boxable
     */
    public abstract <T extends Boxable> void addBoxable(String key, T value);

    /**
     * Inserts a Boxable array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boxable array object, or null
     * @param <T> should implement a Boxable interface
     */
    public abstract <T extends Boxable> void addBoxableArray(String key, T[] value);

    /**
     * Inserts a Boxable List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boxable List object, or null
     * @param <T> should implement a Boxable interface
     */
    public abstract <T extends Boxable> void addBoxableList(String key, List<T> value);

    /**
     * Inserts an Enum value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Enum object, or null
     */
    public abstract void addEnum(String key, Enum value);

    /**
     * Inserts an Enum array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Enum array object, or null
     */
    public abstract void addEnumArray(String key, Enum[] value);

    /**
     * Inserts an Enum List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Enum List object, or null
     */
    public abstract void addEnumList(String key, List<? extends Enum> value);

    /**
     * Inserts an String value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an String object, or null
     */
    public abstract void addString(String key, String value);

    /**
     * Inserts a String array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a String array object, or null
     */
    public abstract void addStringArray(String key, String[] value);

    /**
     * Inserts a String List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a String List object, or null
     */
    public abstract void addStringList(String key, List<String> value);

    /**
     * Inserts a Boolean value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boolean, or null
     */
    public abstract void addBoolean(String key, boolean value);

    /**
     * Inserts a Boolean array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boolean array object, or null
     */
    public abstract void addBooleanArray(String key, boolean[] value);

    /**
     * Inserts a Boolean List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Boolean List object, or null
     */
    public abstract void addBooleanList(String key, List<Boolean> value);

    /**
     * Inserts a Byte value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Byte, or null
     */
    public abstract void addByte(String key, byte value);

    /**
     * Inserts a Byte array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Byte array object, or null
     */
    public abstract void addByteArray(String key, byte[] value);

    /**
     * Inserts a Byte List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Byte List object, or null
     */
    public abstract void addByteList(String key, List<Byte> value);

    /**
     * Inserts a Character value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Character, or null
     */
    public abstract void addChar(String key, char value);

    /**
     * Inserts a Character array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Character array object, or null
     */
    public abstract void addCharArray(String key, char[] value);

    /**
     * Inserts a Character List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Character List object, or null
     */
    public abstract void addCharList(String key, List<Character> value);

    /**
     * Inserts a Short value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Short, or null
     */
    public abstract void addShort(String key, short value);

    /**
     * Inserts a Short array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Short array object, or null
     */
    public abstract void addShortArray(String key, short[] value);

    /**
     * Inserts a Short List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Short List object, or null
     */
    public abstract void addShortList(String key, List<Short> value);

    /**
     * Inserts an Integer value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Integer, or null
     */
    public abstract void addInt(String key, int value);

    /**
     * Inserts an Integer array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Integer array object, or null
     */
    public abstract void addIntArray(String key, int[] value);

    /**
     * Inserts an Integer List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value an Integer List object, or null
     */
    public abstract void addIntList(String key, List<Integer> value);

    /**
     * Inserts a Long value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Long, or null
     */
    public abstract void addLong(String key, long value);

    /**
     * Inserts a Long array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Long array object, or null
     */
    public abstract void addLongArray(String key, long[] value);

    /**
     * Inserts a Long List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Long List object, or null
     */
    public abstract void addLongList(String key, List<Long> value);

    /**
     * Inserts a Double value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Double, or null
     */
    public abstract void addDouble(String key, double value);

    /**
     * Inserts a Double array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Double array object, or null
     */
    public abstract void addDoubleArray(String key, double[] value);

    /**
     * Inserts a Double List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Double List object, or null
     */
    public abstract void addDoubleList(String key, List<Double> value);

    /**
     * Inserts a Float value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Float, or null
     */
    public abstract void addFloat(String key, float value);

    /**
     * Inserts a Float array value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Float array object, or null
     */
    public abstract void addFloatArray(String key, float[] value);

    /**
     * Inserts a Float List value into the mapping of the Boxer,
     * replacing any existing value for the given key.
     *
     * @param key an unique identifier
     * @param value a Float List object, or null
     */
    public abstract void addFloatList(String key, List<Float> value);

    /**************************
        Abstract Get methods
     **************************/

    /**
     * Returns an object of the given type associated with the given key,
     * or null if no mapping of the desired type exists for the given key.
     *
     * @param key an unique identifier
     * @param clazz type to retrieve
     *
     * @return an instance of given type, or null
     */
    public abstract <T> T get(String key, Class<T> clazz);

    /**
     * Returns an array of objects of the given type associated with the given key,
     * or null if no mapping of the desired type exists for the given key.
     *
     * @param key an unique identifier
     * @param clazz type to retrieve
     *
     * @return an array instance of the given type, or null
     */
    public abstract <T> T[] getArray(String key, Class<T> clazz);

    /**
     * Retruns a list of objects of the given type associated wit the given key,
     * or null if no mapping of the desired type exists for the given key.
     *
     * @param key an unique identifier
     * @param clazz type to retrieve
     * @param listtype instance of list type to store in
     *
     * @return an list instance filled with instance of the given type, or null.
     */
    public abstract <T, E extends List<T>> E getList(String key, Class<T> clazz, Class<E> listtype);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param <T> should implement a Boxable interface
     * @param key an unique identifier
     * @param clazz type of Boxable expected class
     * @return a Boxable value, or null
     */
    public abstract <T extends Boxable> T getBoxable(String key, Class<T> clazz);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
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
     * @param key an unique identifier
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
     * @param key an unique identifier
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
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a String value, or null
     */
    public abstract String getString(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a String[] value, or null
     */
    public abstract String[] getStringArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Boolean value, or null
     */
    public abstract boolean getBoolean(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Boolean value, or null
     */
    public abstract boolean[] getBooleanArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Byte value, or null
     */
    public abstract byte getByte(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Byte[] value, or null
     */
    public abstract byte[] getByteArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Character value, or null
     */
    public abstract char getChar(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Character[] value, or null
     */
    public abstract char[] getCharArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Short value, or null
     */
    public abstract short getShort(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Short[] value, or null
     */
    public abstract short[] getShortArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return an Integer value, or null
     */
    public abstract int getInt(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return an Integer[] value, or null
     */
    public abstract int[] getIntArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Long value, or null
     */
    public abstract long getLong(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Long[] value, or null
     */
    public abstract long[] getLongArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Double value, or null
     */
    public abstract double getDouble(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Double[] value, or null
     */
    public abstract double[] getDoubleArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
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
     * @param key an unique identifier
     * @return a Float value, or null
     */
    public abstract float getFloat(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @return a Float[] value, or null
     */
    public abstract float[] getFloatArray(String key);

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key an unique identifier
     * @param listtype type of expected List, should have a no-args constructor.
     * @param <T> Type of the List that will be instantiated
     * @return a List value, or null
     */
    public abstract <T extends List<Float>> T getFloatList(String key, Class<T> listtype);
}
