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

import com.larswerkman.boxer.Execution;
import com.squareup.javapoet.CodeBlock;

/**
 * Created by lars on 24-06-15.
 */
class MethodBinding {

    public enum Method {
        SERIALIZE, DESERIALIZE
    }

    private String name;
    private Method method;
    private Execution execution;
    private boolean hasArgument;

    public MethodBinding(String name, Method method, Execution execution, boolean hasArgument){
        this.name = name;
        this.method = method;
        this.execution = execution;
        this.hasArgument = hasArgument;
    }

    public Method getMethod() {
        return method;
    }

    public Execution getExecution() {
        return execution;
    }

    public CodeBlock brew(String boxable, String boxer){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(hasArgument){
            builder.addStatement("$N.$N($N)", boxable, name, boxer);
        } else {
            builder.addStatement("$N.$N()", boxable, name);
        }
        return builder.build();
    }
}
