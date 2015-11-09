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
package com.larswerkman.boxer;

/**
 * Serialize and deserialize Java objects.
 * {@link TypeAdapter} is a type-specific adapter.
 *
 * For example, a {@code TypeAdapter<Date> }
 * can serialize or deserialize {@code Date} instances.
 *
 * {@link com.larswerkman.boxer.annotations.Adapter} needs to be
 * placed on the {@link TypeAdapter} class to get the class to be
 * recognized by the annotation processor as a {@link TypeAdapter}.
 *
 * @param <T> Type that needs to be serialized or deserialized.
 */
public abstract class TypeAdapter<T> {

    public TypeAdapter(){}

    /**
     * Serialize an object of type {@code T}
     * to a generic {@code Boxer<?>} object
     *
     * @param boxer The {@link Boxer} wrapper to serialize to
     * @param object the value to be serialized
     */
    public abstract void serialize(Boxer<?> boxer, T object);

    /**
     * Deserialize an object of type {@code T}
     * from a generic {@code Boxer<?>} object
     *
     * @param boxer The {@link Boxer} wrapper to deserialize from
     * @return deserialized instance of type {@code T}
     */
    public abstract T deserialize(Boxer<?> boxer);
}