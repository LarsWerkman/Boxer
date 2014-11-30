package com.larswerkman.boxer;

/**
 * Created by lars on 25-11-14.
 */
@Box
public class Gum implements Boxable {

    @Packet
    private int width = 3;
    @Packet
    private int height = 1;

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

        if(!(obj instanceof Gum))
            return false;

        return width == ((Gum) obj).width && height == ((Gum) obj).height;
    }
}
