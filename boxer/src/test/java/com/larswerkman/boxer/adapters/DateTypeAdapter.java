package com.larswerkman.boxer.adapters;

import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.TypeAdapter;
import com.larswerkman.boxer.annotations.Adapter;
import com.larswerkman.boxer.boxables.PrimaryBoxable;

import java.util.Date;

/**
 * Created by lars on 24-05-15.
 */
@Adapter
public class DateTypeAdapter extends TypeAdapter<Date> {

    private static final String TIME_KEY = "time_key";

    @Override
    public void serialize(Boxer<?> boxer, Date object) {
        boxer.addLong(TIME_KEY, object.getTime());
    }

    @Override
    public Date deserialize (Boxer<?> boxer) {
        return new Date(boxer.getLong(TIME_KEY));
    }
}