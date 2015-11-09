package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.Execution;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.annotations.Deserialize;
import com.larswerkman.boxer.annotations.Serialize;
import org.assertj.core.api.Assertions;

/**
 * Created by lars on 25-05-15.
 */
@Box
public class SerializeBoxable {

    private transient boolean serialized = false;
    private transient boolean deserialized = false;

    private transient String aString;

    public SerializeBoxable setup(){
        aString = "String";

        return this;
    }

    @Serialize(Execution.BEFORE)
    public void beforeSerialization(Boxer<?> boxer){
        if(serialized){
            Assertions.fail("@Serialize(BEFORE) should be called before (AFTER)");
        }
        serialized = true;

        boxer.addString("KEY", aString);
    }

    @Serialize(Execution.AFTER)
    public void afterSerialization(Boxer<?> boxer) {
        if(!serialized){
            Assertions.fail("@Serialize(AFTER) should be called after (BEFORE)");
        }
        serialized = false;
    }

    @Deserialize(Execution.BEFORE)
    public void beforeDeserialization(Boxer<?> boxer) {
        if(deserialized){
            Assertions.fail("@Deserialize(BEFORE) should be called before (AFTER)");
        }
        deserialized = true;
    }

    @Deserialize(Execution.AFTER)
    public void afterDeserialization(Boxer<?> boxer){
        if(!deserialized){
            Assertions.fail("@Deserialize(AFTER) should be called after (BEFORE)");
        }
        deserialized = false;

        aString = boxer.getString("KEY");
    }

    @Serialize
    public void noArgument(){

    }

    @Serialize
    public void noWildCardArgument(Boxer boxer){

    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof SerializeBoxable))
                && ((obj == this)
                || (aString.equals(((SerializeBoxable) obj).aString)));
    }
}
