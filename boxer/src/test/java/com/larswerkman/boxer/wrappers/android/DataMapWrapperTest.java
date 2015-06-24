package com.larswerkman.boxer.wrappers.android;

import com.google.android.gms.wearable.DataMap;
import com.larswerkman.boxer.AbstractWrapperTest;
import com.larswerkman.boxer.Boxer;

/**
 * Created by lars on 23-04-15.
 */
public class DataMapWrapperTest extends AbstractWrapperTest {

    @Override
    public Boxer<?> getBoxer() {
        return new DataMapWrapper(new DataMap());
    }
}