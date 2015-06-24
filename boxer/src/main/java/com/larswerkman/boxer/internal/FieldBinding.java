package com.larswerkman.boxer.internal;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.TypeMirror;

/**
 * Created by lars on 27-05-15.
 */
class FieldBinding {

    String name;
    String method;
    TypeMirror type;
    boolean isPrivate;

    public FieldBinding(String name, TypeMirror type, String method, boolean isPrivate){
        this.name = name;
        this.method = method;
        this.type = type;
        this.isPrivate = isPrivate;
    }

    public String method(){
        return method;
    }

    public CodeBlock serialize(String boxer, String boxable){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(isPrivate){
            builder.addStatement("$N.add$N($S, $N.get$N())",
                    boxer, method(), name, boxable, BoxerProcessor.capitalize(name));
        } else {
            builder.addStatement("$N.add$N($S, $N.$N)",
                    boxer, method(), name, boxable, name);
        }
        return builder.build();
    }

    public CodeBlock deserialize(String boxer, String boxable){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(isPrivate){
            if(type == null){
                builder.addStatement("$N.set$N($N.get$N($S))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name);
            } else {
                builder.addStatement("$N.set$N($N.get$N($S, $T.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, type);
            }
        } else {
            if (type == null) {
                builder.addStatement("$N.$N = $N.get$N($S)",
                        boxable, name, boxer, method(), name);
            } else {
                builder.addStatement("$N.$N = $N.get$N($S, $T.class)",
                        boxable, name, boxer, method(), name, type);
            }
        }
        return builder.build();
    }
}