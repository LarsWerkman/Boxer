package com.larswerkman.boxer;

import org.junit.Assert;

import java.util.HashMap;

/**
 * Created by lars on 25-11-14.
 */
public class Test {

    @org.junit.Test
    public void test(){
        Boxer boxer = new Boxer();

        PackOfGum oldPackOfGum = new PackOfGum();
        PackOfGum newPackOfGum = null;

        boxer.add("pack", oldPackOfGum);
        newPackOfGum = boxer.get("pack", PackOfGum.class);

        Assert.assertEquals(oldPackOfGum, newPackOfGum);
    }
}
