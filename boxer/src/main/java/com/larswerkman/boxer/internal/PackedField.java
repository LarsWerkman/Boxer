/*
 * Copyright 2014 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.larswerkman.boxer.internal;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.lang.reflect.Type;

/**
 * Helper class that has all the data for how a field should be stored and retrieved
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
