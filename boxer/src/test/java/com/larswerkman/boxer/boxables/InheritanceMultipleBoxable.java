package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 28-04-15.
 */
@Box
public class InheritanceMultipleBoxable extends InheritanceAccessBoxable {

    public String multiple;

    public InheritanceMultipleBoxable setup(){
        super.setup();
        multiple = "Multiple";
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof InheritanceMultipleBoxable))
                && ((obj == this)
                || (super.equals(obj)
                && multiple.equals(((InheritanceMultipleBoxable) obj).multiple)));
    }
}