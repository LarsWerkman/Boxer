package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

import java.util.Date;

/**
 * Created by lars on 05-06-15.
 */
@Box
public class AdapterBoxable {

    public Date date;

    public AdapterBoxable setup(){
        date = new Date();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof AdapterBoxable))
                && ((obj == this)
                || (date.equals(((AdapterBoxable) obj).date)));
    }
}
