package com.larswerkman.boxer.internal;

import com.squareup.javapoet.ClassName;

import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 05-06-15.
 */
class AdapterBinding {

    private ClassName adapter;
    private TypeMirror type;

    public AdapterBinding(ClassName adapter, TypeMirror type){
        this.adapter = adapter;
        this.type = type;
    }

    public ClassName getAdapter() {
        return adapter;
    }

    public TypeMirror getType() {
        return type;
    }
}
