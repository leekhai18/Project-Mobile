package com.example.remembergroup.model;



public class TextMessage extends Message {
    private String text;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public TextMessage(String text, boolean isMine) {
        super(Constant.TYPE_TEXT_MESSAGE, isMine);
        this.text = text;
    }
}
