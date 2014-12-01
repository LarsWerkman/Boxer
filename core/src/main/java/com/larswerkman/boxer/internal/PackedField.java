package com.larswerkman.boxer.internal;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.lang.reflect.Type;

/**
 * Created by lars on 25-11-14.
 */
class PackedField {

    private TypeMirror type;
    private TypeMirror wrapper;
    private String name;
    private Modifier modifier;
    private boolean array;

    public PackedField(String name, TypeMirror type, Modifier modifier, boolean array, TypeMirror wrapper){
        this.type = type;
        this.name = name;
        this.modifier = modifier;
        this.array = array;
        this.wrapper = wrapper;
    }

    public String name(){
        return name;
    }

    public TypeMirror type(){
        return type;
    }

    public TypeMirror wrapper() {
        return wrapper;
    }

    public boolean isArray(){
        return array;
    }

    public String getter(){
        if(modifier == Modifier.PRIVATE){
            return "get" + name.substring(0,1).toUpperCase() + name.substring(1) + "()";
        }
        return name;
    }

    public String setter(String value){
        if(modifier == Modifier.PRIVATE){
            return "set" + name.substring(0,1).toUpperCase() + name.substring(1) + "(" + value + ")";
        }
        return name + " = " + value;
    }
}
