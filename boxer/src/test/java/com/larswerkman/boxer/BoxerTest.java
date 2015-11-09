package com.larswerkman.boxer;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.wearable.DataMap;
import com.larswerkman.boxer.boxables.InheritancePrimaryBoxable;
import com.larswerkman.boxer.boxables.PrimaryBoxable;
import com.larswerkman.boxer.boxables.TransientBoxable;
import com.larswerkman.boxer.wrappers.TestWrapper;
import com.larswerkman.boxer.wrappers.android.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;

/**
 * Created by lars on 22-04-15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BoxerTest {

    private SQLiteWrapperTest.SQLiteHelper sqLiteHelper = new SQLiteWrapperTest.SQLiteHelper(RuntimeEnvironment.application);

    @Before
    public void setup(){
        Boxer.registerWrapper(TestWrapper.class, HashMap.class);
    }

    @Test
    public void failObtainWrapper(){
        Assertions.assertThat(Boxer.from(null))
                .isNull();
    }

    @Test
    public void obtainDefaultWrappers(){
        Assertions.assertThat(Boxer.from(new Bundle()))
                .isOfAnyClassIn(BundleWrapper.class).isNotNull();
        Assertions.assertThat(Boxer.from(new DataMap()))
                .isOfAnyClassIn(DataMapWrapper.class).isNotNull();
        Assertions.assertThat(Boxer.from(Parcel.obtain()))
                .isOfAnyClassIn(ParcelWrapper.class).isNotNull();
        Assertions.assertThat(Boxer.from(sqLiteHelper.getReadableDatabase()))
                .isOfAnyClassIn(SQLiteWrapper.class).isNotNull();
    }

    @Test
    public void obtainTestWrapper(){
        Assertions.assertThat(Boxer.from(new HashMap<String, Object>()))
                .isOfAnyClassIn(TestWrapper.class).isNotNull();
    }

    @Test
    public void removeTestWrapper(){
        Assertions.assertThat(Boxer.removeWrapper(TestWrapper.class))
                .isSameAs(HashMap.class);
        Assertions.assertThat(Boxer.from(new HashMap<String, Object>()))
                .isNull();
    }

    @Test
    public void removeTestWrapperByType(){
        Assertions.assertThat(Boxer.removeWrapperForType(HashMap.class))
                .isSameAs(TestWrapper.class);
        Assertions.assertThat(Boxer.from(new HashMap<String, Object>()))
                .isNull();
    }

    @Test
    public void clearWrappers(){
        Boxer.clearWrappers();
        Assertions.assertThat(Boxer.from(new HashMap<String, Object>()))
                .isNull();
    }
}
