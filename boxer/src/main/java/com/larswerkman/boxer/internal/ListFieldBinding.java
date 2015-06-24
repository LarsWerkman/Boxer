package com.larswerkman.boxer.internal;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 29-05-15.
 */
class ListFieldBinding extends FieldBinding {

    TypeName listType;

    public ListFieldBinding(String name, TypeMirror type, String method, TypeName listType, boolean isPrivate) {
        super(name, type, method , isPrivate);
        this.listType = listType;
    }

    @Override
    public String method() {
        return super.method() + "List";
    }

    @Override
    public CodeBlock deserialize(String boxer, String boxable) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if(isPrivate){
            if(type == null){
                builder.addStatement("$N.set$N($N.get$N($S, $T.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, listType);
            } else {
                builder.addStatement("$N.set$N($N.get$N($S, $T.class, $T.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, type, listType);
            }
        } else {
            if (type == null) {
                builder.addStatement("$N.$N = $N.get$N($S, $T.class)",
                        boxable, name, boxer, method(), name, listType);
            } else {
                builder.addStatement("$N.$N = $N.get$N($S, $T.class, $T.class)",
                        boxable, name, boxer, method(), name, type, listType);
            }
        }
        return builder.build();
    }
}