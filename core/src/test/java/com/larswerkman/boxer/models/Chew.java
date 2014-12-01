package com.larswerkman.boxer.models;

import com.larswerkman.boxer.Box;
import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.Packet;

/**
 * Created by lars on 25-11-14.
 */
@Box
public class Chew implements Boxable {

    public enum Flavour {
        STRAWBERRY, MINT, WATERMELON
    }

    @Packet
    private int width;
    @Packet
    private int height;

    @Packet
    public Double calories;

    public Flavour flavour;

    public Chew(){

    }

    public Chew(int width, int height, double calories, Flavour flavour){
        this.width = width;
        this.height = height;
        this.calories = calories;
        this.flavour = flavour;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(!(obj instanceof Chew))
            return false;

        return width == ((Chew) obj).width
                && height == ((Chew) obj).height
                && calories.equals(((Chew) obj).calories);
                //&& flavour == ((Chew) obj).flavour;
    }
}
