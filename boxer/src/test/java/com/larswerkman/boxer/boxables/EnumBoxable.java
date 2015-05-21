package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.enums.PrimaryEnum;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lars on 21-05-15.
 */
@Box
public class EnumBoxable implements Boxable {

    public PrimaryEnum primaryEnum;
    public PrimaryEnum[] primaryEnumArray;
    public List<PrimaryEnum> primaryEnumList;

    public EnumBoxable setup(){
        primaryEnum = PrimaryEnum.TO_BE;
        primaryEnumArray = PrimaryEnum.values();
        primaryEnumList = Arrays.asList(PrimaryEnum.values());

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof EnumBoxable))
                && ((obj == this)
                || (primaryEnum.equals(((EnumBoxable) obj).primaryEnum)
                && primaryEnumList.equals(((EnumBoxable) obj).primaryEnumList)
                && Arrays.deepEquals(primaryEnumArray, ((EnumBoxable) obj).primaryEnumArray)));
    }
}