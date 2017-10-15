package com.example.remembergroup.model;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

/**
 * Created by Khai Lee on 10/14/2017.
 */

public class ListFriends {
    //
    private static ListFriends instance;

    public static ListFriends getInstance() {
        if(instance == null)
            instance = new ListFriends();
        return instance;
    }

    //
    private ArrayList<Friend> array;

    public ListFriends(){
        array = new ArrayList<Friend>();
    }

    public ListFriends(ArrayList listF){
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
