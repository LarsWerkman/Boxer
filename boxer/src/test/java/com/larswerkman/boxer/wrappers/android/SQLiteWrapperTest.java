package com.larswerkman.boxer.wrappers.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.larswerkman.boxer.AbstractWrapperTest;
import com.larswerkman.boxer.Boxer;
import org.robolectric.RuntimeEnvironment;


/**
 * Created by lars on 29-04-15.
 */
public class SQLiteWrapperTest extends AbstractWrapperTest {

    public static class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context){
            super(context, "DATABASE", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private SQLiteHelper helper = new SQLiteHelper(RuntimeEnvironment.application);
    private SQLiteDatabase database = helper.getReadableDatabase();

    @Override
    public Boxer<?> getBoxer() {
        return new SQLiteWrapper(database);
    }
}
