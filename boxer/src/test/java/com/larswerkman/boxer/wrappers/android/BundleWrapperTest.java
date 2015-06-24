package com.larswerkman.boxer.wrappers.android;

import android.os.Bundle;
import com.larswerkman.boxer.AbstractWrapperTest;
import com.larswerkman.boxer.Boxer;

/**
 * Created by lars on 22-04-15.
 */
public class BundleWrapperTest extends AbstractWrapperTest {

    @Override
    public Boxer<?> getBoxer() {
        return new BundleWrapper(new Bundle());
    }
}