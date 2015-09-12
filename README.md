Boxer
=====

Annotation based serialization library for Java and Android frameworks,
by generating boilerplate code.

* Define classes as serializable with the <code>@Box</code> annotation.
And by implementing the <code>Boxable</code> interface.
* ~~Serialize fields with the <code>@Packet</code> annotation~~ Depracted
* By default all fields will be serialized, except fields with the transient modifier
* the <code>@Wrap</code> annotation will wrap a <code>List</code> fields into the appropriate Subclass. (Subclass needs to have a no-args constructor)
* Default non-class serialization possible

```java
@Box
public class Example implements Boxable {

    private int height;
    private transient int width;
    public double weight;
    
    @Wrap(Stack.class)
    public List<Example> stack;

    //Empty constructor for Injection
    public Example(){

    }

    //Getters and Setters for private fields
}
```

Retrieving a <code>Boxer</code> instance, and serializing and de-serializing your data.

Example using the <code>Bundle</code> class:

```java
public class ExampleActivity extends Activity {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Boxer boxer = Boxer.from(outState);
        boxer.addBoxable("Example", new Example());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boxer boxer = Boxer.from(savedInstanceState);
        boxer.getBoxable("Example", Example.class);
    }
}
```

To register your own Boxer wrapper for a specific class:

```java
//Registering a wrapper class for a specific Target class
Boxer.registerWrapper(Wrapper.class, Target.class);

//Removing a wrapper for a specific wrapper class
Boxer.removeWrapper(Wrapper.class);

//Removing a wrapper for a target class
Boxer.removeWrapperForType(Target.class);

//Clearing all the wrappers
Boxer.clearWrappers();
```

TypeAdapters define how an non-Boxable class should be serialized and deserialized. TypeAdapter will need to be registered with the <code>@Adapter</code> annotation.
The typeparameter of the TypeAdapter should be the class you want to serialize / deserialize.

```java
@Adapter
public class DateTypeAdapter extends TypeAdapter<Date> {

    private static final String TIME_KEY = "time_key";

    @Override
    public void serialize(Boxer<?> boxer, Date object) {
        boxer.addLong(TIME_KEY, object.getTime());
    }

    @Override
    public Date deserialize (Boxer<?> boxer) {
        return new Date(boxer.getLong(TIME_KEY));
    }
}
```

<code>@Serialize</code> and <code>@Deserialize</code> are used to annotate methods that will be called after or before serialization or deserialization inside <code>Boxable</code> classes.

Both annotations take an <code>Execution</code> enum as parameter to specify when they will be executed.
Default executation behaviour will be after serialization or deserialization.

The method can have a <code>Boxer</code> parameter or be empty.

```java
@Serialize
public void serialization(Boxer<?> boxer){
	//Do something
}

@Deserialize(Execution.BEFORE)
public void deserialization(Boxer<?> boxer){
	//Do something
}
```


Current supported supported classes:

* (Android) Bundle
* (Android) Parcel
* (Android) SQLiteDatabase (Experimental)
* (Java/Android) DataMap

__Beta phase!__

Proguard
----------
```groovy
-dontwarn com.larswerkman.boxer.internal.**
-dontwarn com.larswerkman.boxer.wrappers.**
-keep class **$$Boxer { *; }
-keep class **$$Box { *; }
-keepnames class * { @com.larswerkman.boxer.annotations.Box *;}
-keepnames class *
{ @com.larswerkman.boxer.annotations.Adapter *;}
-keepclasseswithmembernames class * {
    @com.larswerkman.boxer.annotations.* <methods>;
}
```

Dependency
----------
Adding it as a dependency to your project.

```xml
<dependency>
  <groupId>com.larswerkman</groupId>
  <artifactId>boxer</artifactId>
  <version>0.3.0</version>
</dependency>
```

```groovy
dependencies {
    compile 'com.larswerkman:boxer:0.3.0'
}
```
License
-------

    Copyright 2015 Lars Werkman

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

<h2>Devoleped By</h2>
**Lars Werkman**