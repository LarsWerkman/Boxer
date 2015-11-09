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
