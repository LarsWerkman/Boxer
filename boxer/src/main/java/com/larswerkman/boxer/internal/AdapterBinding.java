package com.larswerkman.boxer.internal;

import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 05-06-15.
 */
class AdapterBinding {

    private TypeMirror adapter;
    private TypeMirror type;

    public AdapterBinding(TypeMirror adapter, TypeMirror type){
        this.adapter = adapter;
        this.type = type;
    }

    public TypeMirror getAdapter() {
        return adapter;
    }

    public TypeMirror getType() {
        return type;
    }
}
