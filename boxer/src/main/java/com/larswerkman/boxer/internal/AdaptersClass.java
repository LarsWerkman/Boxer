package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.TypeAdapter;
import com.squareup.javapoet.*;

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
    private List<AdapterBinding> adapters;

    public AdaptersClass(List<AdapterBinding> adapters){
        this.adapters = adapters;
    }

    public TypeSpec build(){
        return TypeSpec.classBuilder(BoxerProcessor.ADAPTER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(hashMapType, HASHMAP_ADAPTERS_VARIABLE, Modifier.PRIVATE, Modifier.STATIC)
                .addStaticBlock(staticInitialization())
                .addMethod(getMethod())
                .build();
    }

    private CodeBlock staticInitialization(){
        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("$N = new $T()", HASHMAP_ADAPTERS_VARIABLE, hashMapType);
        for(AdapterBinding adapter : adapters){
            block.addStatement("$N.put($T.class, new $T())",
                    HASHMAP_ADAPTERS_VARIABLE, TypeName.get(adapter.getType()), TypeName.get(adapter.getAdapter()));
        }
        return block.build();
    }

    private MethodSpec getMethod(){
        return MethodSpec
                .methodBuilder(BoxerProcessor.ADAPTER_METHOD_GET)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeAdapter.class)
                .addParameter(Class.class, TYPE_VARIABLE)
                .addStatement("return $N.get($N)", HASHMAP_ADAPTERS_VARIABLE, TYPE_VARIABLE)
                .build();
    }
}