package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 28-04-15.
 */
@Box
public class InheritancePrimaryBoxable extends PrimaryBoxable {

    public String inheritance;

    public InheritancePrimaryBoxable setup(){
        super.setup();
        inheritance = "Inheritance";
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof InheritancePrimaryBoxable))
                && ((obj == this)
                || (super.equals(obj)
                && inheritance.equals(((InheritancePrimaryBoxable) obj).inheritance)));
    }
}
