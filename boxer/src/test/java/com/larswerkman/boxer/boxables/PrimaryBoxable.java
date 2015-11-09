package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 23-04-15.
 */
@Box
public class PrimaryBoxable {

    public String aString = "";
    public boolean aBoolean;
    public byte aByte;
    public char aChar;
    public short aShort;
    public int anInt;
    public long aLong;
    public double aDouble;
    public float aFloat;

    public PrimaryBoxable setup(){
        aString = "string";
        aBoolean = true;
        aByte = 0xA;
        aChar = 'A';
        aShort = 99;
        anInt = 99;
        aLong = 99l;
        aDouble = 0.99;
        aFloat = 0.99f;

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof PrimaryBoxable))
                && ((obj == this)
                || (aString.equals(((PrimaryBoxable) obj).aString)
                && aBoolean == ((PrimaryBoxable) obj).aBoolean
                && aByte == ((PrimaryBoxable) obj).aByte
                && aChar == ((PrimaryBoxable) obj).aChar
                && aShort == ((PrimaryBoxable) obj).aShort
                && anInt == ((PrimaryBoxable) obj).anInt
                && aLong == ((PrimaryBoxable) obj).aLong
                && aDouble == ((PrimaryBoxable) obj).aDouble
                && aFloat == ((PrimaryBoxable) obj).aFloat));
    }
}
