package com.larswerkman.boxer;

import com.larswerkman.boxer.models.Pack;
import org.junit.Assert;

/**
 * Created by lars on 25-11-14.
 */
public class BoxerTest {

    @org.junit.Test
    public void test(){
        Boxer boxer = new Boxer();

        Pack bigCodeChew = new Pack("Big Code Chew");
        bigCodeChew.fill(20);
        bigCodeChew.eat();

        boxer.add("chew", bigCodeChew);
        Pack storedChew = boxer.get("chew", Pack.class);

        Assert.assertEquals(bigCodeChew, storedChew);
    }
}