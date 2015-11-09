package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.TypeAdapter;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lars on 28-05-15.
 */
final class AdaptersClass {

    private static final String HASHMAP_ADAPTERS_VARIABLE = "adapters";
    private static final String TYPE_VARIABLE = "type";

    private TypeName hashMapType = ParameterizedTypeName.get(HashMap.class, Class.class, TypeAdapter.class);
    private TypeVariableName T = TypeVariableName.get("T");
    private List<AdapterBinding> adapters;

    public AdaptersClass(List<AdapterBinding> adapters){
        this.adapters = adapters;
    }

    public TypeSpec build(){
        return TypeSpec.classBuilder(BoxerProcessor.ADAPTER_CLASS_NAME)
                .superclass(GeneratedAdapters.class)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", BoxerProcessor.PROCESSOR_NAME)
                        .build())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(hashMapType, HASHMAP_ADAPTERS_VARIABLE, Modifier.PRIVATE)
                .addMethod(constructor())
                .addMethod(getMethod())
                .build();
    }

    private MethodSpec constructor(){
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addStatement("$N = new $T()", HASHMAP_ADAPTERS_VARIABLE, hashMapType);
        for(AdapterBinding adapter : adapters){
            builder.addStatement("$N.put($T.class, new $T())",
                    HASHMAP_ADAPTERS_VARIABLE, TypeName.get(adapter.getType()), adapter.getAdapter());
        }
        return builder.build();
    }

    private MethodSpec getMethod(){
        return MethodSpec
                .methodBuilder(BoxerProcessor.ADAPTER_METHOD_GET)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(T)
                .returns(ParameterizedTypeName.get(ClassName.get(TypeAdapter.class), T))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), T), TYPE_VARIABLE)
                .addStatement("return $N.get($N)", HASHMAP_ADAPTERS_VARIABLE, TYPE_VARIABLE)
                .build();
    }
}