package com.larswerkman.boxer;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import com.google.android.gms.wearable.DataMap;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.models.Chew;
import com.larswerkman.boxer.models.Pack;
import org.junit.Assert;

/**
 * Created by lars on 25-11-14.
 */
public class BoxerTest {

    @org.junit.Test
    public void test(){
        Boxer boxer = Boxer.from(new DataMap());

        Pack bigCodeChew = new Pack("Big Code Chew");
        bigCodeChew.fill(20);
        bigCodeChew.eat();

        boxer.addBoxable("pack", bigCodeChew);
        Pack newBigCodeChew = boxer.getBoxable("pack", Pack.class);

        Assert.assertEquals(bigCodeChew, newBigCodeChew);
    }
}