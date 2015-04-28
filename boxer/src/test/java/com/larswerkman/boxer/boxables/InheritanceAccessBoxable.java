package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 28-04-15.
 */
@Box
public class InheritanceAccessBoxable extends AccessBoxable {

    public String inheritance;

    public InheritanceAccessBoxable setup(){
        super.setup();
        inheritance = "Inheritance";
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof InheritanceAccessBoxable))
                && ((obj == this)
                || (super.equals(obj)
                && inheritance.equals(((InheritanceAccessBoxable) obj).inheritance)));
    }
}
