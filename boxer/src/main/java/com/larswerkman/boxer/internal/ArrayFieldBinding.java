package com.larswerkman.boxer.internal;

import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 29-05-15.
 */
class ArrayFieldBinding extends FieldBinding {

    public ArrayFieldBinding(String name, TypeMirror type, String method, boolean isPrivate) {
        super(name, type, method, isPrivate);
    }

    @Override
    public String method() {
        return super.method() + "Array";
    }
}
