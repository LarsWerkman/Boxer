package com.larswerkman.boxer;

import android.os.Bundle;
import com.google.common.primitives.*;
import com.larswerkman.boxer.boxables.*;
import com.larswerkman.boxer.enums.PrimaryEnum;
import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.*;

/**
 * Created by lars on 22-04-15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public abstract class AbstractWrapperTest {

    public Boxer<?> boxer;

    private static final String KEY = "key";

    private static final Date DATE_ADAPTER = new Date();
    private static final Date[] DATE_ADAPTER_ARRAY = {new Date(), new Date()};

    private static final PrimaryBoxable BOXABLE = new PrimaryBoxable().setup();
    private static final PrimaryBoxable[] BOXABLE_ARRAY = {new PrimaryBoxable().setup(), new PrimaryBoxable().setup()};

    private static final PrimaryEnum ENUM = PrimaryEnum.TO_BE;
    private static final PrimaryEnum[] ENUM_ARRAY = {PrimaryEnum.TO_BE, PrimaryEnum.NOT_TO_BE};

    private static final String STRING = "string";
    private static final String[] STRING_ARRAY = {"first", "second"};

    private static final boolean BOOLEAN = true;
    private static final boolean[] BOOLEAN_ARRAY = {true, false};

    private static final byte BYTE = 0xA;
    private static final byte[] BYTE_ARRAY = {0xA, 0xB};

    private static final char CHARACTER = 'A';
    private static final char[] CHARACTER_ARRAY = {'A', 'B'};

    private static final short SHORT = 99;
    private static final short[] SHORT_ARRAY = {98, 99};

    private static final int INTEGER = 99;
    private static final int[] INTEGER_ARRAY = {98, 99};

    private static final long LONG = 99l;
    private static final long[] LONG_ARRAY = {98l, 99l};

    private static final double DOUBLE = 0.99;
    private static final double[] DOUBLE_ARRAY = {0.98, 0.99};

    private static final float FLOAT = 0.99f;
    private static final float[] FLOAT_ARRAY = {0.98f, 0.99f};

    private static final ListBoxable LIST_BOXABLE = new ListBoxable().setup();
    private static final TransientBoxable TRANSIENT_BOXABLE = new TransientBoxable().setup();
    private static final AccessBoxable ACCESS_BOXABLE = new AccessBoxable().setup();
    private static final InheritancePrimaryBoxable INHERITANCE_PRIMARY_BOXABLE = new InheritancePrimaryBoxable().setup();
    private static final InheritanceAccessBoxable INHERITANCE_ACCESS_BOXABLE = new InheritanceAccessBoxable().setup();
    private static final InheritanceMultipleBoxable INHERITANCE_MULTIPLE_BOXABLE = new InheritanceMultipleBoxable().setup();
    private static final ObjectBoxable OBJECT_BOXABLE = new ObjectBoxable().setup();
    private static final EnumBoxable ENUM_BOXABLE = new EnumBoxable().setup();

    public abstract Boxer<?> getBoxer();

    @Before
    public void setup() {
        boxer = getBoxer();
        before();
    }

    public void before() {
        //Empty implementation
    }

    @After
    public void after() {
        //Empty implementation
    }

    public void between() {
        //Empty implementation
    }

    @Test
    public void adapter(){
        Assertions.assertThat(boxer.get(KEY, Date.class))
                .isNull();
        between();
        boxer.add(KEY, DATE_ADAPTER);
        between();
        Assertions.assertThat(boxer.get(KEY, Date.class))
                .isEqualTo(DATE_ADAPTER);
    }

    @Test
    public void adapterArray(){
        Assertions.assertThat(boxer.getArray(KEY, Date.class))
                .isNull();
        between();
        boxer.addArray(KEY, DATE_ADAPTER_ARRAY);
        between();
        Assertions.assertThat(boxer.getArray(KEY, Date.class))
                .isEqualTo(DATE_ADAPTER_ARRAY);
    }

    @Test
    public void adapterList(){
        Assertions.assertThat(boxer.getList(KEY, Date.class, ArrayList.class))
                .isNull();
        between();
        boxer.addList(KEY, Arrays.asList(DATE_ADAPTER_ARRAY));
        between();
        Assertions.assertThat(boxer.getList(KEY, Date.class, ArrayList.class))
                .isEqualTo(Arrays.asList(DATE_ADAPTER_ARRAY));
    }

    @Test
    public void boxable(){
        Assertions.assertThat(boxer.getBoxable(KEY, PrimaryBoxable.class))
                .isNull();
        between();
        boxer.addBoxable(KEY, BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, PrimaryBoxable.class))
                .isEqualTo(BOXABLE);
    }

    @Test
    public void boxableArray(){
        Assertions.assertThat(boxer.getBoxableArray(KEY, PrimaryBoxable.class))
                .isNull();
        between();
        boxer.addBoxableArray(KEY, BOXABLE_ARRAY);
        between();
        Assertions.assertThat(boxer.getBoxableArray(KEY, PrimaryBoxable.class))
                .isEqualTo(BOXABLE_ARRAY);
    }

    @Test
    public void boxableList(){
        Assertions.assertThat(boxer.getBoxableList(KEY, PrimaryBoxable.class, ArrayList.class))
                .isNull();
        between();
        boxer.addBoxableList(KEY, Arrays.asList(BOXABLE_ARRAY));
        between();
        Assertions.assertThat(boxer.getBoxableList(KEY, PrimaryBoxable.class, ArrayList.class))
                .isEqualTo(Arrays.asList(BOXABLE_ARRAY));
    }

    @Test
    public void Enum(){
        Assertions.assertThat(boxer.getEnum(KEY, PrimaryEnum.class))
                .isNull();
        between();
        boxer.addEnum(KEY, ENUM);
        between();
        Assertions.assertThat(boxer.getEnum(KEY, PrimaryEnum.class))
                .isEqualTo(ENUM);
    }

    @Test
    public void enumArray(){
        Assertions.assertThat(boxer.getEnumArray(KEY, PrimaryEnum.class))
                .isNull();
        between();
        boxer.addEnumArray(KEY, ENUM_ARRAY);
        between();
        Assertions.assertThat(boxer.getEnumArray(KEY, PrimaryEnum.class))
                .isEqualTo(ENUM_ARRAY);
    }

    @Test
    public void enumList(){
        Assertions.assertThat(boxer.getEnumList(KEY, PrimaryEnum.class, ArrayList.class))
                .isNull();
        between();
        boxer.addEnumList(KEY, Arrays.asList(ENUM_ARRAY));
        between();
        Assertions.assertThat(boxer.getEnumList(KEY, PrimaryEnum.class, ArrayList.class))
                .isEqualTo(Arrays.asList(ENUM_ARRAY));
    }

    @Test
    public void string() {
        Assertions.assertThat(boxer.getString(KEY))
                .isNull();
        between();
        boxer.addString(KEY, STRING);
        between();
        Assertions.assertThat(boxer.getString(KEY))
                .isEqualTo(STRING);
    }

    @Test
    public void stringArray() {
        Assertions.assertThat(boxer.getStringArray(KEY))
                .isNull();
        between();
        boxer.addStringArray(KEY, STRING_ARRAY);
        between();
        Assertions.assertThat(boxer.getStringArray(KEY))
                .isEqualTo(STRING_ARRAY);
    }

    @Test
    public void stringList() {
        Assertions.assertThat(boxer.getStringList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addStringList(KEY, Arrays.asList(STRING_ARRAY));
        between();
        Assertions.assertThat(boxer.getStringList(KEY, ArrayList.class))
                .isEqualTo(Arrays.asList(STRING_ARRAY));
    }

    @Test
    public void Boolean(){
        Assertions.assertThat(boxer.getBoolean(KEY))
                .isFalse();
        between();
        boxer.addBoolean(KEY, BOOLEAN);
        between();
        Assertions.assertThat(boxer.getBoolean(KEY))
                .isEqualTo(BOOLEAN);
    }

    @Test
    public void booleanArray(){
        Assertions.assertThat(boxer.getBooleanArray(KEY))
                .isNull();
        between();
        boxer.addBooleanArray(KEY, BOOLEAN_ARRAY);
        between();
        Assertions.assertThat(boxer.getBooleanArray(KEY))
                .isEqualTo(BOOLEAN_ARRAY);
    }

    @Test
    public void booleanList(){
        Assertions.assertThat(boxer.getBooleanList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addBooleanList(KEY, Booleans.asList(BOOLEAN_ARRAY));
        between();
        Assertions.assertThat(boxer.getBooleanList(KEY, ArrayList.class))
                .isEqualTo(Booleans.asList(BOOLEAN_ARRAY));
    }

    @Test
    public void Byte(){
        Assertions.assertThat(boxer.getByte(KEY))
                .isZero();
        between();
        boxer.addByte(KEY, BYTE);
        between();
        Assertions.assertThat(boxer.getByte(KEY))
                .isEqualTo(BYTE);
    }

    @Test
    public void byteArray(){
        Assertions.assertThat(boxer.getByteArray(KEY))
                .isNull();
        between();
        boxer.addByteArray(KEY, BYTE_ARRAY);
        between();
        Assertions.assertThat(boxer.getByteArray(KEY))
                .isEqualTo(BYTE_ARRAY);
    }

    @Test
    public void byteList(){
        Assertions.assertThat(boxer.getByteList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addByteList(KEY, Bytes.asList(BYTE_ARRAY));
        between();
        Assertions.assertThat(boxer.getByteList(KEY, ArrayList.class))
                .isEqualTo(Bytes.asList(BYTE_ARRAY));
    }

    @Test
    public void character(){
        //Work around because of weird behaviour of AssertJ
        assert boxer.getChar(KEY) == 0;

        between();
        boxer.addChar(KEY, CHARACTER);
        between();
        Assertions.assertThat(boxer.getChar(KEY))
                .isEqualTo(CHARACTER);
    }

    @Test
    public void characterArray(){
        Assertions.assertThat(boxer.getCharArray(KEY))
                .isNull();
        between();
        boxer.addCharArray(KEY, CHARACTER_ARRAY);
        between();
        Assertions.assertThat(boxer.getCharArray(KEY))
                .isEqualTo(CHARACTER_ARRAY);
    }

    @Test
    public void characterList(){
        Assertions.assertThat(boxer.getCharList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addCharList(KEY, Chars.asList(CHARACTER_ARRAY));
        between();
        Assertions.assertThat(boxer.getCharList(KEY, ArrayList.class))
                .isEqualTo(Chars.asList(CHARACTER_ARRAY));
    }

    @Test
    public void Short(){
        Assertions.assertThat(boxer.getShort(KEY))
                .isZero();
        between();
        boxer.addShort(KEY, SHORT);
        between();
        Assertions.assertThat(boxer.getShort(KEY))
                .isEqualTo(SHORT);
    }

    @Test
    public void shortArray(){
        Assertions.assertThat(boxer.getShortArray(KEY))
                .isNull();
        between();
        boxer.addShortArray(KEY, SHORT_ARRAY);
        between();
        Assertions.assertThat(boxer.getShortArray(KEY))
                .isEqualTo(SHORT_ARRAY);
    }

    @Test
    public void shortList(){
        Assertions.assertThat(boxer.getShortList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addShortList(KEY, Shorts.asList(SHORT_ARRAY));
        between();
        Assertions.assertThat(boxer.getShortList(KEY, ArrayList.class))
                .isEqualTo(Shorts.asList(SHORT_ARRAY));
    }

    @Test
    public void integer() {
        Assertions.assertThat(boxer.getInt(KEY))
                .isZero();
        between();
        boxer.addInt(KEY, INTEGER);
        between();
        Assertions.assertThat(boxer.getInt(KEY))
                .isEqualTo(INTEGER);
    }

    @Test
    public void integerArray() {
        Assertions.assertThat(boxer.getIntArray(KEY))
                .isNull();
        between();
        boxer.addIntArray(KEY, INTEGER_ARRAY);
        between();
        Assertions.assertThat(boxer.getIntArray(KEY))
                .isEqualTo(INTEGER_ARRAY);
    }

    @Test
    public void integerList() {
        Assertions.assertThat(boxer.getIntList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addIntList(KEY, Ints.asList(INTEGER_ARRAY));
        between();
        Assertions.assertThat(boxer.getIntList(KEY, ArrayList.class))
                .isEqualTo(Ints.asList(INTEGER_ARRAY));
    }

    @Test
    public void Long(){
        Assertions.assertThat(boxer.getLong(KEY))
                .isZero();
        between();
        boxer.addLong(KEY, LONG);
        between();
        Assertions.assertThat(boxer.getLong(KEY))
                .isEqualTo(LONG);
    }

    @Test
    public void longArray(){
        Assertions.assertThat(boxer.getLongArray(KEY))
                .isNull();
        between();
        boxer.addLongArray(KEY, LONG_ARRAY);
        between();
        Assertions.assertThat(boxer.getLongArray(KEY))
                .isEqualTo(LONG_ARRAY);
    }

    @Test
    public void longList(){
        Assertions.assertThat(boxer.getLongList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addLongList(KEY, Longs.asList(LONG_ARRAY));
        between();
        Assertions.assertThat(boxer.getLongList(KEY, ArrayList.class))
                .isEqualTo(Longs.asList(LONG_ARRAY));
    }

    @Test
    public void Double(){
        Assertions.assertThat(boxer.getDouble(KEY))
                .isZero();
        between();
        boxer.addDouble(KEY, DOUBLE);
        between();
        Assertions.assertThat(boxer.getDouble(KEY))
                .isEqualTo(DOUBLE);
    }

    @Test
    public void doubleArray(){
        Assertions.assertThat(boxer.getDoubleArray(KEY))
                .isNull();
        between();
        boxer.addDoubleArray(KEY, DOUBLE_ARRAY);
        between();
        Assertions.assertThat(boxer.getDoubleArray(KEY))
                .isEqualTo(DOUBLE_ARRAY);
    }

    @Test
    public void doubleList(){
        Assertions.assertThat(boxer.getDoubleList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addDoubleList(KEY, Doubles.asList(DOUBLE_ARRAY));
        between();
        Assertions.assertThat(boxer.getDoubleList(KEY, ArrayList.class))
                .isEqualTo(Doubles.asList(DOUBLE_ARRAY));
    }

    @Test
    public void Float(){
        Assertions.assertThat(boxer.getFloat(KEY))
                .isZero();
        between();
        boxer.addFloat(KEY, FLOAT);
        between();
        Assertions.assertThat(boxer.getFloat(KEY))
                .isEqualTo(FLOAT);
    }

    @Test
    public void floatArray(){
        Assertions.assertThat(boxer.getFloatArray(KEY))
                .isNull();
        between();
        boxer.addFloatArray(KEY, FLOAT_ARRAY);
        between();
        Assertions.assertThat(boxer.getFloatArray(KEY))
                .isEqualTo(FLOAT_ARRAY);
    }

    @Test
    public void floatList(){
        Assertions.assertThat(boxer.getFloatList(KEY, ArrayList.class))
                .isNull();
        between();
        boxer.addFloatList(KEY, Floats.asList(FLOAT_ARRAY));
        between();
        Assertions.assertThat(boxer.getFloatList(KEY, ArrayList.class))
                .isEqualTo(Floats.asList(FLOAT_ARRAY));
    }

    /**
     * Special cases
     */

    @Test
    public void listBoxable(){
        boxer.addBoxable(KEY, LIST_BOXABLE);
        between();

        ListBoxable listBoxable = boxer.getBoxable(KEY, ListBoxable.class);
        Assertions.assertThat(listBoxable.defaultList)
                .isOfAnyClassIn(ArrayList.class)
                .isEqualTo(LIST_BOXABLE.defaultList);
        Assertions.assertThat(listBoxable.arrayList)
                .isOfAnyClassIn(ArrayList.class)
                .isEqualTo(LIST_BOXABLE.arrayList);
        Assertions.assertThat(listBoxable.arrayWrapList)
                .isOfAnyClassIn(ArrayList.class)
                .isEqualTo(LIST_BOXABLE.arrayWrapList);
        Assertions.assertThat(listBoxable.stackList)
                .isOfAnyClassIn(Stack.class)
                .isEqualTo(LIST_BOXABLE.stackList);
        Assertions.assertThat(listBoxable.stackWrapList)
                .isOfAnyClassIn(Stack.class)
                .isEqualTo(LIST_BOXABLE.stackWrapList);
        Assertions.assertThat(listBoxable.linkedList)
                .isOfAnyClassIn(LinkedList.class)
                .isEqualTo(LIST_BOXABLE.linkedList);
        Assertions.assertThat(listBoxable.linkedWrapList)
                .isOfAnyClassIn(LinkedList.class)
                .isEqualTo(LIST_BOXABLE.linkedWrapList);
    }

    @Test
    public void transientBoxable(){
        boxer.addBoxable(KEY, TRANSIENT_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, TransientBoxable.class))
                .isNotEqualTo(TRANSIENT_BOXABLE);
    }

    @Test
    public void AccessModifiers(){
        boxer.addBoxable(KEY, ACCESS_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, AccessBoxable.class))
                .isEqualTo(ACCESS_BOXABLE);
    }

    @Test
    public void inheritencePrimaryBoxable(){
        boxer.addBoxable(KEY, INHERITANCE_PRIMARY_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, InheritancePrimaryBoxable.class))
                .isEqualTo(INHERITANCE_PRIMARY_BOXABLE);
    }

    @Test
    public void inheritenceAccessBoxable(){
        boxer.addBoxable(KEY, INHERITANCE_ACCESS_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, InheritanceAccessBoxable.class))
                .isEqualTo(INHERITANCE_ACCESS_BOXABLE);
    }

    @Test
    public void inheritenceMultipleBoxable(){
        boxer.addBoxable(KEY, INHERITANCE_MULTIPLE_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, InheritanceMultipleBoxable.class))
                .isEqualTo(INHERITANCE_MULTIPLE_BOXABLE);
    }

    @Test
    public void objectBoxable(){
        boxer.addBoxable(KEY, OBJECT_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, ObjectBoxable.class))
                .isEqualTo(OBJECT_BOXABLE);
    }

    @Test
    public void enumBoxable(){
        boxer.addBoxable(KEY, ENUM_BOXABLE);
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, EnumBoxable.class))
                .isEqualTo(ENUM_BOXABLE);
    }

    @Test
    public void replaceBoxable(){
        boxer.addBoxable(KEY, BOXABLE);
        between();
        boxer.addBoxable(KEY, new PrimaryBoxable());
        between();
        Assertions.assertThat(boxer.getBoxable(KEY, PrimaryBoxable.class))
                .isEqualTo(new PrimaryBoxable());
    }

    @Test
    public void replaceList(){
        boxer.addIntList(KEY, Arrays.asList(1, 2, 3, 4 , 5));
        between();
        boxer.addIntList(KEY, Ints.asList(INTEGER_ARRAY));
        between();
        Assertions.assertThat(boxer.getIntList(KEY, ArrayList.class))
                .isEqualTo(Ints.asList(INTEGER_ARRAY));
    }

    @Test
    public void replaceArray() {
        boxer.addIntArray(KEY, new int[]{1, 2, 3, 4, 5});
        between();
        boxer.addIntArray(KEY, INTEGER_ARRAY);
        between();
        Assertions.assertThat(boxer.getIntArray(KEY))
                .isEqualTo(INTEGER_ARRAY);
    }
}
