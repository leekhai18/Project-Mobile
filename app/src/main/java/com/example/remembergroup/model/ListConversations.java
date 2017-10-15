package com.example.remembergroup.model;

import java.util.ArrayList;

/**
 * Created by Khai Lee on 10/15/2017.
 */

public class ListConversations {   //
    private static ListConversations instance;

    public static ListConversations getInstance() {
        if(instance == null)
            instance = new ListConversations();
        return instance;
    }

    //
    private ArrayList<Friend> array;

    public ListConversations(){
        array = new ArrayList<Friend>();
    }

    public ListConversations(ArrayList listF){
        array = new ArrayList<Friend>();
        array.addAll(listF);
    }

    public ArrayList<Friend> getArray(){return array;}

    public void add(Friend friend){
        array.add(friend);
    }

    public void remove(Friend friend){
        array.remove(friend);
    }
}
