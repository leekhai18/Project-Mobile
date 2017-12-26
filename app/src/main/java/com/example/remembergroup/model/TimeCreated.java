package com.example.remembergroup.model;

import com.github.bassaer.chatmessageview.util.ITimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * Created by Khai Lee on 12/25/2017.
 */

public class TimeCreated implements ITimeFormatter {
    private String timeString;

    public TimeCreated(String timeCreatedMessage){
        timeString = timeCreatedMessage;
    }

    @NotNull
    @Override
    public String getFormattedTimeText(Calendar calendar) {
        return timeString;
    }
}
