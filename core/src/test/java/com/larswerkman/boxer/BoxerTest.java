package com.larswerkman.boxer;

import android.os.Bundle;
import com.larswerkman.boxer.models.Chew;
import org.junit.Assert;

/**
 * Created by lars on 25-11-14.
 */
public class BoxerTest {

    @org.junit.Test
    public void test(){
        Boxer boxer = Boxer.from(new Bundle());

        /*Pack bigCodeChew = new Pack("Big Code Chew");
        bigCodeChew.fill(20);
        bigCodeChew.eat();*/

        /*boxer.addBoxable("chew", new Chew(2, 3, 2.5, Chew.Flavour.MINT));
        Chew storedChew = boxer.get("chew", Chew.class);

        Assert.assertEquals(new Chew(2, 3, 2.5, Chew.Flavour.MINT), storedChew);*/
    }
}