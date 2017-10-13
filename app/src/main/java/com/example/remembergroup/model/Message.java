package com.example.remembergroup.model;

import java.io.Serializable;




public class Message implements Serializable{

    private int type;
    private boolean isMine;

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public boolean isMine() {
        return isMine;
    }
    public void setMine(boolean mine) {
        isMine = mine;
    }

    public Message(int type, boolean isMine) {
        this.type = type;
        this.isMine = isMine;
    }

    public Message() {
    }



}
