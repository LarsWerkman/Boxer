package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 23-04-15.
 */
@Box
public class TransientBoxable implements Boxable {

    public transient String string;
    public transient int integer;

    public TransientBoxable setup(){
        string = "String";
        integer = 99;

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof TransientBoxable))
                && ((obj == this)
                || (string != null
                && string.equals(((TransientBoxable) obj).string)
                && integer == ((TransientBoxable) obj).integer));
    }
}
