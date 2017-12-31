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

    public Conversation getConverstaionById(String id) {
        for (int i = 0; i < array.size(); i++){
            if (array.get(i).getId().equals(id)){
                return array.get(i);
            }
        }

        return null;
    }

    public ArrayList<Conversation> getArray(){return array;}

    public void add(Conversation con){
        if (!array.contains(con)){
            array.add(con);
        }
    }

    public void remove(Conversation con){
        array.remove(con);
    }
}
