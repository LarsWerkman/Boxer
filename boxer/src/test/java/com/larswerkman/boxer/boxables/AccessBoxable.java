package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 23-04-15.
 */
@Box
public class AccessBoxable implements Boxable {

    String defaultMod;
    public String publicMod;
    private String privateMod;
    protected String protectedMod;

    public AccessBoxable setup(){
        defaultMod = "default";
        publicMod = "public";
        privateMod = "private";
        protectedMod = "protected";

        return this;
    }

    public void setPrivateMod(String privateMod) {
        this.privateMod = privateMod;
    }

    public String getPrivateMod() {
        return privateMod;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof AccessBoxable))
                && ((obj == this)
                || (defaultMod.equals(((AccessBoxable) obj).defaultMod)
                && (publicMod.equals(((AccessBoxable) obj).publicMod))
                && (privateMod.equals(((AccessBoxable) obj).privateMod))
                && (protectedMod.equals(((AccessBoxable) obj).protectedMod))));
    }
}
