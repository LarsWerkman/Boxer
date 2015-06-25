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
