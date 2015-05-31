package com.larswerkman.boxer.internal;

/**
 * Created by lars on 27-05-15.
 */
class FieldBinding {

    String name;
    String method;
    String type;
    boolean isPrivate;

    public FieldBinding(String name, String method, String type, boolean isPrivate){
        this.name = name;
        this.method = method;
        this.type = type;
        this.isPrivate = isPrivate;
    }

    public String method(){
        return method;
    }

    public String serialize(String boxer, String boxable){
        if(isPrivate){
            return String.format("%s.add%s(\"%s\", %s.get%s())",
                    boxer, method(), name, boxable, BoxerProcessor.capitalize(name));
        }
        return String.format("%s.add%s(\"%s\", %s.%s)",
                boxer, method(), name, boxable, name);
    }

    public String deserialize(String boxer, String boxable){
        if(isPrivate){
            if(type == null){
                return String.format("%s.set%s(%s.get%s(\"%s\"))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name);
            } else {
                return String.format("%s.set%s(%s.get%s(\"%s\", %s.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, type);
            }
        }
        if(type == null){
            return String.format("%s.%s = %s.get%s(\"%s\")",
                    boxable, name, boxer, method(), name);
        } else {
            return String.format("%s.%s = %s.get%s(\"%s\", %s.class)",
                    boxable, name, boxer, method(), name, type);
        }
    }
}