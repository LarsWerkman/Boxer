package com.larswerkman.boxer.wrappers.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Boxer;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Experimental wrapper to see if SQLite is possible.
 *
 * Created by lars on 24-04-15.
 */
public class SQLiteWrapper extends Boxer<SQLiteDatabase> {

    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_INDEX = "pos";

    private static final String TABLE_BOXABLE = "boxable_table_boxer";
    private static final String TABLE_STRING = "string_table_boxer";
    private static final String TABLE_STRING_ARRAY = "string_array_table_boxer";
    private static final String TABLE_INTEGER = "integer_table_boxer";
    private static final String TABLE_INTEGER_ARRAY = "integer_array_table_boxer";
    private static final String TABLE_REAL = "real_table_boxer";
    private static final String TABLE_REAL_ARRAY = "real_array_table_boxer";
    private static final String TABLE_BLOB = "blob_table_boxer";
    private static final String TABLE_ARRAY_SIZE = "array_size_table_boxer";

    private static final String ID_SEPARATOR = ".";
    private static final String INDEX_SEPARATOR = "*";

    private String identifier = "";
    private SQLiteDatabase database;

    public SQLiteWrapper(SQLiteDatabase database) {
        super(database);
        this.database = database;

        createTable(TABLE_STRING, "TEXT");
        createArrayTable(TABLE_STRING_ARRAY, "TEXT");
        createTable(TABLE_INTEGER, "INTEGER");
        createArrayTable(TABLE_INTEGER_ARRAY, "INTEGER");
        createTable(TABLE_REAL, "REAL");
        createArrayTable(TABLE_REAL_ARRAY, "REAL");
        createTable(TABLE_BLOB, "BLOB");
        createTable(TABLE_ARRAY_SIZE, "INTEGER");
        createTable(TABLE_BOXABLE, "INTEGER");
    }

    private SQLiteWrapper(SQLiteDatabase database, String identifier){
        this(database);
        this.identifier = identifier;
    }

    private void createTable(String name, String type){
        String query = "CREATE TABLE IF NOT EXISTS "
                + name + "(" + COLUMN_KEY + " TEXT PRIMARY KEY,"
                + COLUMN_VALUE + " " + type + ");";
        this.database.execSQL(query);
    }

    private void createArrayTable(String name, String type){
        String query = "CREATE TABLE IF NOT EXISTS "
                + name + "(" + COLUMN_KEY + " TEXT,"
                + COLUMN_VALUE + " " + type + ","
                + COLUMN_INDEX + " INTEGER);";
        this.database.execSQL(query);
    }

    private void addKeyValue(String key, ContentValues value, String table){
        value.put(COLUMN_KEY, key);
        database.insertWithOnConflict(table, COLUMN_KEY, value, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void addKeyValues(String key, ContentValues[] values, String table){
        database.beginTransaction();
        database.delete(table, COLUMN_KEY + "=?", new String[]{key});
        for(ContentValues value : values){
            value.put(COLUMN_KEY, key);
            database.insert(table, null, value);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public void add(String key, Object value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, 1);
        addKeyValue(getIdentifier(key), content, TABLE_BOXABLE);

        serialize(new SQLiteWrapper(database, getIdentifier(key)), value);
    }

    @Override
    public void addArray(String key, Object[] value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value.length);
        addKeyValue(getIdentifier(key), content, TABLE_ARRAY_SIZE);

        for(int i = 0; i < value.length; i++){
            serialize(new SQLiteWrapper(database, getIdentifier(key, i)), value[i]);
        }
    }

    @Override
    public void addList(String key, List<?> value) {
        addArray(key, value.toArray());
    }

    @Override
    public <T extends Boxable> void addBoxable(String key, T value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, 1);
        addKeyValue(getIdentifier(key), content, TABLE_BOXABLE);

        serializeBoxable(new SQLiteWrapper(database, getIdentifier(key)), value);
    }

    @Override
    public <T extends Boxable> void addBoxableArray(String key, T[] value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value.length);
        addKeyValue(getIdentifier(key), content, TABLE_ARRAY_SIZE);

        for(int i = 0; i < value.length; i++){
            serializeBoxable(new SQLiteWrapper(database, getIdentifier(key, i)), value[i]);
        }
    }

    @Override
    public <T extends Boxable> void addBoxableList(String key, List<T> value) {
        addBoxableArray(key, (T[]) value.toArray());
    }

    @Override
    public void addEnum(String key, Enum value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value.name());
        addKeyValue(getIdentifier(key), content, TABLE_STRING);
    }

    @Override
    public void addEnumArray(String key, Enum[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i].name());
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_STRING_ARRAY);
    }

    @Override
    public void addEnumList(String key, List<? extends Enum> value) {
        addEnumArray(key, (Enum[]) value.toArray());
    }

    @Override
    public void addString(String key, String value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_STRING);
    }

    @Override
    public void addStringArray(String key, String[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_STRING_ARRAY);
    }

    @Override
    public void addStringList(String key, List<String> value) {
        addStringArray(key, (String[]) value.toArray());
    }

    @Override
    public void addBoolean(String key, boolean value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addBooleanArray(String key, boolean[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addBooleanList(String key, List<Boolean> value) {
        boolean[] booleans = new boolean[value.size()];
        for(int i = 0; i < value.size(); i++){
            booleans[i] = value.get(i);
        }
        addBooleanArray(key, booleans);
    }

    @Override
    public void addByte(String key, byte value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addByteArray(String key, byte[] value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_BLOB);
    }

    @Override
    public void addByteList(String key, List<Byte> value) {
        byte[] bytes = new byte[value.size()];
        for(int i = 0; i < value.size(); i++){
            bytes[i] = value.get(i);
        }
        addByteArray(key, bytes);
    }

    @Override
    public void addChar(String key, char value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, (int) value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addCharArray(String key, char[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, (int) value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addCharList(String key, List<Character> value) {
        char[] chars = new char[value.size()];
        for(int i = 0; i < value.size(); i++){
            chars[i] = value.get(i);
        }
        addCharArray(key, chars);
    }

    @Override
    public void addShort(String key, short value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addShortArray(String key, short[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addShortList(String key, List<Short> value) {
        short[] shorts = new short[value.size()];
        for(int i = 0; i < value.size(); i++){
            shorts[i] = value.get(i);
        }
        addShortArray(key, shorts);
    }

    @Override
    public void addInt(String key, int value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addIntArray(String key, int[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addIntList(String key, List<Integer> value) {
        int[] ints = new int[value.size()];
        for(int i = 0; i < value.size(); i++){
            ints[i] = value.get(i);
        }
        addIntArray(key, ints);
    }

    @Override
    public void addLong(String key, long value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addLongArray(String key, long[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addLongList(String key, List<Long> value) {
        long[] longs = new long[value.size()];
        for(int i = 0; i < value.size(); i++){
            longs[i] = value.get(i);
        }
        addLongArray(key, longs);
    }

    @Override
    public void addDouble(String key, double value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_INTEGER);
    }

    @Override
    public void addDoubleArray(String key, double[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_INTEGER_ARRAY);
    }

    @Override
    public void addDoubleList(String key, List<Double> value) {
        double[] doubles = new double[value.size()];
        for(int i = 0; i < value.size(); i++){
            doubles[i] = value.get(i);
        }
        addDoubleArray(key, doubles);
    }

    @Override
    public void addFloat(String key, float value) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_VALUE, value);
        addKeyValue(getIdentifier(key), content, TABLE_REAL);
    }

    @Override
    public void addFloatArray(String key, float[] value) {
        ContentValues[] contents = new ContentValues[value.length];
        for(int i = 0; i < value.length; i++){
            ContentValues content = new ContentValues();
            content.put(COLUMN_VALUE, value[i]);
            content.put(COLUMN_INDEX, i);
            contents[i] = content;
        }

        addKeyValues(getIdentifier(key), contents, TABLE_REAL_ARRAY);
    }

    @Override
    public void addFloatList(String key, List<Float> value) {
        float[] floats = new float[value.size()];
        for(int i = 0; i < value.size(); i++){
            floats[i] = value.get(i);
        }
        addFloatArray(key, floats);
    }

    private Cursor getKeyValue(String key, String table){
        return database.query(table, new String[]{COLUMN_KEY, COLUMN_VALUE}, COLUMN_KEY + "=?", new String[]{key}, null, null, null);
    }

    private Cursor getKeyValues(String key, String table){
        return database.query(table, new String[]{COLUMN_KEY, COLUMN_VALUE, COLUMN_INDEX}, COLUMN_KEY + "=?", new String[]{key}, null, null, COLUMN_INDEX);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_BOXABLE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        return deserialize(new SQLiteWrapper(database, getIdentifier(key)), clazz);
    }

    @Override
    public <T> T[] getArray(String key, Class<T> clazz) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_ARRAY_SIZE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        int size = cursor.getInt(1);

        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = deserialize(new SQLiteWrapper(database, getIdentifier(key, i)), clazz);
        }
        return boxables;
    }

    @Override
    public <T, E extends List<T>> E getList(String key, Class<T> clazz, Class<E> listtype) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_ARRAY_SIZE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        int size = cursor.getInt(1);

        E boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(deserialize(new SQLiteWrapper(database, getIdentifier(key, i)), clazz));
            }
        } catch (Exception e){};
        return boxables;
    }

    @Override
    public <T extends Boxable> T getBoxable(String key, Class<T> clazz) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_BOXABLE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        return deserializeBoxable(new SQLiteWrapper(database, getIdentifier(key)), clazz);
    }

    @Override
    public <T extends Boxable> T[] getBoxableArray(String key, Class<T> clazz) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_ARRAY_SIZE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        int size = cursor.getInt(1);

        T[] boxables = (T[]) Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++){
            boxables[i] = deserializeBoxable(new SQLiteWrapper(database, getIdentifier(key, i)), clazz);
        }
        return boxables;
    }

    @Override
    public <T extends Boxable, E extends List<T>> E getBoxableList(String key, Class<T> clazz, Class<E> listtype) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_ARRAY_SIZE);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        int size = cursor.getInt(1);

        E boxables = null;
        try {
            boxables = listtype.newInstance();
            for (int i = 0; i < size; i++) {
                boxables.add(deserializeBoxable(new SQLiteWrapper(database, getIdentifier(key, i)), clazz));
            }
        } catch (Exception e){};
        return boxables;
    }

    private <T extends Enum> T retrieveEnum(String value, Class<T> clazz){
        T en = null;
        try{
            Method method = clazz.getMethod("valueOf", String.class);
            en = (T) method.invoke(null, value);
        } catch (Exception e){}
        return en;
    }

    @Override
    public <T extends Enum> T getEnum(String key, Class<T> clazz) {
        return retrieveEnum(getString(key), clazz);
    }

    @Override
    public <T extends Enum> T[] getEnumArray(String key, Class<T> clazz) {
        String[] values = getStringArray(key);
        if(values == null){
            return null;
        }

        T[] enums = (T[]) Array.newInstance(clazz, values.length);
        for(int i = 0; i < values.length; i++){
            enums[i] = retrieveEnum(values[i], clazz);
        }
        return enums;
    }

    @Override
    public <T extends Enum, E extends List<T>> E getEnumList(String key, Class<T> clazz, Class<E> listtype) {
        String[] values = getStringArray(key);
        if(values == null){
            return null;
        }
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
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_STRING);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        cursor.moveToFirst();
        return cursor.getString(1);
    }

    @Override
    public String[] getStringArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_STRING_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        String[] values = new String[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getString(1);
        }
        return values;
    }

    @Override
    public <T extends List<String>> T getStringList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_STRING_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T strings = null;
        try {
            strings = listtype.newInstance();
            while(cursor.moveToNext()) {
                strings.add(cursor.getString(1));
            }
        } catch (Exception e){};
        return strings;
    }

    @Override
    public boolean getBoolean(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return false;
        }

        cursor.moveToFirst();
        return cursor.getInt(1) > 0;
    }

    @Override
    public boolean[] getBooleanArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        boolean[] values = new boolean[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getInt(1) > 0;
        }
        return values;
    }

    @Override
    public <T extends List<Boolean>> T getBooleanList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T booleans = null;
        try {
            booleans = listtype.newInstance();
            while(cursor.moveToNext()) {
                booleans.add(cursor.getInt(1) > 0);
            }
        } catch (Exception e){};
        return booleans;
    }

    @Override
    public byte getByte(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return (byte) cursor.getInt(1);
    }

    @Override
    public byte[] getByteArray(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_BLOB);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        cursor.moveToFirst();
        return cursor.getBlob(1);
    }

    @Override
    public <T extends List<Byte>> T getByteList(String key, Class<T> listtype) {
        byte[] values = getByteArray(key);
        if(values == null){
            return null;
        }

        T bytes = null;
        try {
            bytes = listtype.newInstance();
            for(byte b : values){
                bytes.add(b);
            }
        } catch (Exception e){};
        return bytes;
    }

    @Override
    public char getChar(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return (char) cursor.getInt(1);
    }

    @Override
    public char[] getCharArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        char[] values = new char[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = (char) cursor.getInt(1);
        }
        return values;
    }

    @Override
    public <T extends List<Character>> T getCharList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T chars = null;
        try {
            chars = listtype.newInstance();
            while(cursor.moveToNext()) {
                chars.add((char) cursor.getInt(1));
            }
        } catch (Exception e){};
        return chars;
    }

    @Override
    public short getShort(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return cursor.getShort(1);
    }

    @Override
    public short[] getShortArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        short[] values = new short[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getShort(1);
        }
        return values;
    }

    @Override
    public <T extends List<Short>> T getShortList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T shorts = null;
        try {
            shorts = listtype.newInstance();
            while(cursor.moveToNext()) {
                shorts.add(cursor.getShort(1));
            }
        } catch (Exception e){};
        return shorts;
    }

    @Override
    public int getInt(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return cursor.getInt(1);
    }

    @Override
    public int[] getIntArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        int[] values = new int[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getInt(1);
        }
        return values;
    }

    @Override
    public <T extends List<Integer>> T getIntList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T ints = null;
        try {
            ints = listtype.newInstance();
            while(cursor.moveToNext()) {
                ints.add(cursor.getInt(1));
            }
        } catch (Exception e){};
        return ints;
    }

    @Override
    public long getLong(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return cursor.getLong(1);
    }

    @Override
    public long[] getLongArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        long[] values = new long[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getLong(1);
        }
        return values;
    }

    @Override
    public <T extends List<Long>> T getLongList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T longs = null;
        try {
            longs = listtype.newInstance();
            while(cursor.moveToNext()) {
                longs.add(cursor.getLong(1));
            }
        } catch (Exception e){};
        return longs;
    }

    @Override
    public double getDouble(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_INTEGER);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return cursor.getDouble(1);
    }

    @Override
    public double[] getDoubleArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        double[] values = new double[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getDouble(1);
        }
        return values;
    }

    @Override
    public <T extends List<Double>> T getDoubleList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_INTEGER_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T doubles = null;
        try {
            doubles = listtype.newInstance();
            while(cursor.moveToNext()) {
                doubles.add(cursor.getDouble(1));
            }
        } catch (Exception e){};
        return doubles;
    }

    @Override
    public float getFloat(String key) {
        Cursor cursor = getKeyValue(getIdentifier(key), TABLE_REAL);
        if(cursor == null || cursor.getCount() == 0){
            return 0;
        }

        cursor.moveToFirst();
        return cursor.getFloat(1);
    }

    @Override
    public float[] getFloatArray(String key) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_REAL_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        float[] values = new float[cursor.getCount()];
        while(cursor.moveToNext()){
            values[cursor.getPosition()] = cursor.getFloat(1);
        }
        return values;
    }

    @Override
    public <T extends List<Float>> T getFloatList(String key, Class<T> listtype) {
        Cursor cursor = getKeyValues(getIdentifier(key), TABLE_REAL_ARRAY);
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        T floats = null;
        try {
            floats = listtype.newInstance();
            while(cursor.moveToNext()) {
                floats.add(cursor.getFloat(1));
            }
        } catch (Exception e){};
        return floats;
    }

    private String getIdentifier(String key){
        return identifier + omitSeparators(key) + ID_SEPARATOR;
    }

    private String getIdentifier(String key, int position){
        return identifier + omitSeparators(key) + INDEX_SEPARATOR + position + ID_SEPARATOR;
    }

    private String omitSeparators(String key){
        key = key.replace(ID_SEPARATOR, "?.?");
        key = key.replace(INDEX_SEPARATOR, "?*?");
        return key;
    }
}
