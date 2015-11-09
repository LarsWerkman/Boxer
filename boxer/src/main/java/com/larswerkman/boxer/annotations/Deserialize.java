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
package com.larswerkman.boxer.annotations;

import com.larswerkman.boxer.Execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that will be executed during deserialization of a
 * class annotated with an {@link Box} annotation.
 *
 * Methods annotated with the {@link Deserialize} annotation
 * have can have an argument of the type {@link com.larswerkman.boxer.Boxer}
 *
 * {@code @Deserialize public void deserialization(Boxer<?> boxer){}}
 * {@code @Deserialize public void deserialization(Boxer boxer){}}
 * {@code @Deserialize public void deserialization(){}}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Deserialize {
    /**
     * Determines when the execution of the method will be called.
     * {@code Execution.BEFORE} will be called before deserialization.
     * {@code Execution.AFTER} will be called after deserialization
     *
     * Default behaviour will be to be called after deserialization.
     *
     * @return Execution type
     */
    Execution value() default Execution.AFTER;
}