package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.TypeAdapter;

/**
 * Superclass for Auto-Generated class that returns the correct TypeAdapter.
 */
public abstract class GeneratedAdapters {

    public GeneratedAdapters(){

    }

    public abstract <T> TypeAdapter<T> getAdapter(Class<T> type);
}
