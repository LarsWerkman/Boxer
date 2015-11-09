/*
 * Copyright 2015 Lars Werkman
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
