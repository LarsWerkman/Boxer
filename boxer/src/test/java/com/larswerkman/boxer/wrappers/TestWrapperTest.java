package com.larswerkman.boxer.wrappers;

import com.larswerkman.boxer.AbstractWrapperTest;
import com.larswerkman.boxer.Boxer;

import java.util.HashMap;

/**
 * Created by lars on 23-04-15.
 */
public class TestWrapperTest extends AbstractWrapperTest {

    @Override
    public Boxer<?> getBoxer() {
        return new TestWrapper(new HashMap<String, Object>());
    }
}