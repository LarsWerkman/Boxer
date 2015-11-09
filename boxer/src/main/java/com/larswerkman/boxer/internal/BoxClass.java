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

import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.Execution;
import com.larswerkman.boxer.TypeAdapter;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Created by lars on 27-05-15.
 */
final class BoxClass {

    private static final String BOXER_VARIABLE = "boxer";
    private static final String BOXABLE_VARIABLE = "object";
    private static final ParameterizedTypeName BOXER_CLASS = ParameterizedTypeName.get(
            ClassName.get(Boxer.class),
            WildcardTypeName.subtypeOf(Object.class)
    );

    private String className;
    private ClassName targetClass;

    private List<FieldBinding> fields;
    private List<MethodBinding> methods;

    public BoxClass(String className, ClassName targetClass, List<FieldBinding> fields, List<MethodBinding> methods){
        this.className = className;
        this.targetClass = targetClass;
        this.fields = fields;
        this.methods = methods;
    }

    public TypeSpec build(){
        return TypeSpec.classBuilder(className)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(TypeAdapter.class), targetClass))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", BoxerProcessor.PROCESSOR_NAME)
                        .build())
                .addMethod(serializeMethod())
                .addMethod(deserializeMethod())
                .build();
    }

    private MethodSpec serializeMethod() {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(BoxerProcessor.METHOD_SERIALIZE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(BOXER_CLASS, BOXER_VARIABLE)
                .addParameter(targetClass, BOXABLE_VARIABLE);
        for(MethodBinding method : methods){
            if(method.getMethod() == MethodBinding.Method.SERIALIZE
                    && method.getExecution() == Execution.BEFORE){
                builder.addCode(method.brew(BOXABLE_VARIABLE, BOXER_VARIABLE));
            }
        }
        for(FieldBinding field : fields){
            builder.addCode(field.serialize(BOXER_VARIABLE, BOXABLE_VARIABLE));
        }
        for(MethodBinding method : methods){
            if(method.getMethod() == MethodBinding.Method.SERIALIZE
                    && method.getExecution() == Execution.AFTER){
                builder.addCode(method.brew(BOXABLE_VARIABLE, BOXER_VARIABLE));
            }
        }
        return builder.build();
    }

    private MethodSpec deserializeMethod() {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(BoxerProcessor.METHOD_DESERIALIZE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(targetClass)
                .addParameter(BOXER_CLASS, BOXER_VARIABLE)
                .addStatement("$T $N = new $T()", targetClass, BOXABLE_VARIABLE, targetClass);
        for(MethodBinding method : methods){
            if(method.getMethod() == MethodBinding.Method.DESERIALIZE
                    && method.getExecution() == Execution.BEFORE){
                builder.addCode(method.brew(BOXABLE_VARIABLE, BOXER_VARIABLE));
            }
        }
        for(FieldBinding field : fields){
            builder.addCode(field.deserialize(BOXER_VARIABLE, BOXABLE_VARIABLE));
        }
        for(MethodBinding method : methods){
            if(method.getMethod() == MethodBinding.Method.DESERIALIZE
                    && method.getExecution() == Execution.AFTER){
                builder.addCode(method.brew(BOXABLE_VARIABLE, BOXER_VARIABLE));
            }
        }
        builder.addStatement("return $N", BOXABLE_VARIABLE);
        return builder.build();
    }
}