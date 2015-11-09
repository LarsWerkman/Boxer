package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.annotations.Box;

/**
 * Created by lars on 12-09-15.
 */
@Box
public class NestedBoxable {

    @Box
    public static class StaticFirst {

        @Box
        public static class StaticSecond {
            public String string;

            public StaticSecond setup() {
                string = "string";
                return this;
            }

            @Override
            public boolean equals(Object obj) {
                return !(obj == null
                        || !(obj instanceof StaticSecond))
                        && ((obj == this)
                        || string.equals(((StaticSecond) obj).string));
            }
        }

        public StaticSecond second;

        public StaticFirst setup() {
            second = new StaticSecond().setup();
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return !(obj == null
                    || !(obj instanceof StaticFirst))
                    && ((obj == this)
                    || second.equals(((StaticFirst) obj).second));
        }
    }

    public StaticFirst staticFirst;

    public NestedBoxable setup() {
        staticFirst = new StaticFirst().setup();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null
                || !(obj instanceof NestedBoxable))
                && ((obj == this)
                || staticFirst.equals(((NestedBoxable) obj).staticFirst));
    }
}
