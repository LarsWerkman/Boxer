package com.larswerkman.boxer.models;

import com.larswerkman.boxer.Box;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Packet;
import com.larswerkman.boxer.Wrap;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by lars on 25-11-14.
 */
@Box
public class Pack implements Boxable {

    @Wrap(Stack.class)
    @Packet
    public List<Chew> chews;

    @Packet
    public String name;

    public Pack(){

    }

    public Pack(String name){
        this.name = name;
        this.chews = new Stack<Chew>();
    }

    public void fill(int amount){
        for(int i = 0; i < amount; i++){
            chews.add(new Chew(3, 3, 9.8, Chew.Flavour.WATERMELON));
        }
    }

    public Chew eat(){
        return ((Stack<Chew>) chews).pop();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(!(obj instanceof Pack))
            return false;

        return Arrays.equals(chews.toArray(), ((Pack) obj).chews.toArray()) && name.equals(((Pack) obj).name);
    }
}
