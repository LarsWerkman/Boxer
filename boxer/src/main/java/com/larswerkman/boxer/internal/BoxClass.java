package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.Boxer;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by lars on 27-05-15.
 */
final class BoxClass {

    private static final String BOXER_VARIABLE = "boxer";
    private static final String BOXABLE_VARIABLE = "boxable";
    private static final ParameterizedTypeName BOXER_CLASS = ParameterizedTypeName.get(
            ClassName.get(Boxer.class),
            WildcardTypeName.subtypeOf(Object.class)
    );

    private String className;
    private ClassName targetClass;

    private List<FieldBinding> fields;

    public BoxClass(String className, ClassName targetClass, List<FieldBinding> fields){
        this.className = className;
        this.targetClass = targetClass;
        this.fields = fields;
    }

    public TypeSpec build(){
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(serializeMethod())
                .addMethod(deserializeMethod())
                .build();
    }

    private MethodSpec serializeMethod() {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(BoxerProcessor.METHOD_SERIALIZE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(targetClass, BOXABLE_VARIABLE)
                .addParameter(BOXER_CLASS, BOXER_VARIABLE);
        for(FieldBinding field : fields){
            builder.addCode(field.serialize(BOXER_VARIABLE, BOXABLE_VARIABLE));
        }
        return builder.build();
    }

    private MethodSpec deserializeMethod() {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(BoxerProcessor.METHOD_DESERIALIZE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(targetClass)
                .addParameter(BOXER_CLASS, BOXER_VARIABLE)
                .addStatement("$T $N = new $T()", targetClass, BOXABLE_VARIABLE, targetClass);
        for(FieldBinding field : fields){
            builder.addCode(field.deserialize(BOXER_VARIABLE, BOXABLE_VARIABLE));
        }
        builder.addStatement("return $N", BOXABLE_VARIABLE);
        return builder.build();
    }
}