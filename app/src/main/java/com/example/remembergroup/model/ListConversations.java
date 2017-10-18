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
    private ArrayList<Conversation> array;

    public ListConversations(){
        array = new ArrayList<Conversation>();
    }

    public ListConversations(ArrayList list){
        array = new ArrayList<Conversation>();
        array.addAll(list);
    }

    public ArrayList<Conversation> getArray(){return array;}

    public void add(Conversation con){
        array.add(con);
    }

    public void remove(Conversation con){
        array.remove(con);
    }
}
