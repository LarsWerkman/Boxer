package com.larswerkman.boxer.wrappers.android;

import android.os.Parcel;
import com.larswerkman.boxer.AbstractWrapperTest;
import com.larswerkman.boxer.Boxer;

/**
 * Created by lars on 22-04-15.
 */
public class ParcelWrapperTest extends AbstractWrapperTest {

    private Parcel parcel;

    @Override
    public Boxer getBoxer() {
        return new ParcelWrapper(parcel = Parcel.obtain());
    }

    @Override
    public void after() {
        parcel.recycle();
    }

    @Override
    public void between() {
        parcel.setDataPosition(0);
    }
}