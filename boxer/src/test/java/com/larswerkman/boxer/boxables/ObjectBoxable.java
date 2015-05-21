package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.annotations.Box;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lars on 29-04-15.
 */
@Box
public class ObjectBoxable implements Boxable {

    public PrimaryBoxable primaryBoxable;
    public PrimaryBoxable[] primaryBoxableArray;
    public List<PrimaryBoxable> primaryBoxableList;

    public ObjectBoxable setup(){
        primaryBoxable = new PrimaryBoxable().setup();
        primaryBoxableArray = new PrimaryBoxable[]{
                new PrimaryBoxable().setup(),
                new PrimaryBoxable().setup()
        };
        primaryBoxableList = Arrays.asList(
                new PrimaryBoxable().setup(),
                new PrimaryBoxable().setup()
        );

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof ObjectBoxable))
                && ((obj == this)
                || (primaryBoxable.equals(((ObjectBoxable) obj).primaryBoxable)
                && primaryBoxableList.equals(((ObjectBoxable) obj).primaryBoxableList)
                && Arrays.deepEquals(primaryBoxableArray, ((ObjectBoxable) obj).primaryBoxableArray)));
    }
}
