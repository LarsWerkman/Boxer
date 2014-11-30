package com.larswerkman.boxer;

import org.junit.Assert;

/**
 * Created by lars on 25-11-14.
 */
@Box
public class PackOfGum implements Boxable {

    @Packet
    public Gum gum;

    public PackOfGum(){
        gum = new Gum();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(!(obj instanceof PackOfGum))
            return false;

        return gum.equals(((PackOfGum) obj).gum);
    }
}
