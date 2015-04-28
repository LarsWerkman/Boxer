package com.larswerkman.boxer.boxables;

import com.larswerkman.boxer.Boxable;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.annotations.Wrap;

import java.util.*;

/**
 * Created by lars on 23-04-15.
 */
@Box
public class ListBoxable implements Boxable {

    public List<Integer> defaultList;

    public ArrayList<Integer> arrayList;
    @Wrap(ArrayList.class)
    public List<Integer> arrayWrapList;

    public Stack<Integer> stackList;
    @Wrap(Stack.class)
    public List<Integer> stackWrapList;

    public LinkedList<Integer> linkedList;
    @Wrap(LinkedList.class)
    public List<Integer> linkedWrapList;

    public ListBoxable setup(){
        defaultList = new ArrayList<Integer>();
        defaultList.add(1);
        defaultList.add(2);
        defaultList.add(3);
        defaultList.add(4);
        defaultList.add(5);

        arrayList = new ArrayList<Integer>();
        arrayList.addAll(defaultList);

        arrayWrapList = new ArrayList<Integer>();
        arrayWrapList.addAll(defaultList);

        stackList = new Stack<Integer>();
        stackList.addAll(defaultList);

        stackWrapList = new Stack<Integer>();
        stackWrapList.addAll(defaultList);

        linkedList = new LinkedList<Integer>();
        linkedList.addAll(defaultList);

        linkedWrapList = new LinkedList<Integer>();
        linkedWrapList.addAll(defaultList);

        return this;
    }
}
