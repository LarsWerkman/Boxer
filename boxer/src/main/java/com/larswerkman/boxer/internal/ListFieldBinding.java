package com.larswerkman.boxer.internal;

/**
 * Created by lars on 29-05-15.
 */
class ListFieldBinding extends FieldBinding {

    private String listType;

    public ListFieldBinding(String name, String method, String type, String listType, boolean isPrivate) {
        super(name, method, type, isPrivate);
        this.listType = listType;
    }

    @Override
    public String method() {
        return super.method() + "List";
    }

    @Override
    public String deserialize(String boxer, String boxable) {
        if(isPrivate){
            if(type == null){
                return String.format("%s.set%s(%s.get%s(\"%s\", %s.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, listType);
            } else {
                return String.format("%s.set%s(%s.get%s(\"%s\", %s.class, %s.class))",
                        boxable, BoxerProcessor.capitalize(name), boxer, method(), name, type, listType);
            }
        }
        if(type == null){
            return String.format("%s.%s = %s.get%s(\"%s\", %s.class)",
                    boxable, name, boxer, method(), name, listType);
        } else {
            return String.format("%s.%s = %s.get%s(\"%s\", %s.class, %s.class)",
                    boxable, name, boxer, method(), name, type, listType);
        }
    }
}
