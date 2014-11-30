package com.larswerkman.boxer;

import com.larswerkman.boxer.internal.BoxerProcessor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by lars on 25-11-14.
 */
public class Boxer {

    public HashMap<String, Object> map;

    public Boxer(){
        map = new HashMap<String, Object>();
    }

    public <T extends Boxable> void add(String key, T value){
        try {
            Class boxer = Class.forName(value.getClass().getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_WRITE, value.getClass());
            map.put(key, (HashMap) method.invoke(null, value));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public <T extends Boxable> T get(String key, Class<T> clazz){
        try {
            Class boxer = Class.forName(clazz.getCanonicalName() + BoxerProcessor.CLASS_EXTENSION);
            Method method = boxer.getMethod(BoxerProcessor.METHOD_READ, HashMap.class);
            return (T) method.invoke(null, map.get(key));
        } catch (Exception e){
            System.out.println(e.getMessage());
        };
        return null;
    }
}
