package com.larswerkman.boxer.internal;

/**
 * Created by lars on 29-05-15.
 */
class ArrayFieldBinding extends FieldBinding {

    public ArrayFieldBinding(String name, String method, String type, boolean isPrivate) {
        super(name, method, type, isPrivate);
    }

    @Override
    public String method() {
        return super.method() + "Array";
    }
}
