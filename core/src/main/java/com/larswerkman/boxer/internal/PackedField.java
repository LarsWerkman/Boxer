package com.larswerkman.boxer.internal;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 25-11-14.
 */
class PackedField {

    private TypeMirror type;
    private String name;
    private Modifier modifier;

    public PackedField(String name, TypeMirror type, Modifier modifier){
        this.type = type;
        this.name = name;
        this.modifier = modifier;
    }

    public String name(){
        return name;
    }

    public TypeMirror type(){
        return type;
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
