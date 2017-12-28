package com.example.remembergroup.chat_app;

import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.remembergroup.adapter.RequestAddFriendAdapter;
import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Khai Lee on 10/6/2017.
 */

public class SingletonSocket {
    private final String SERVER_URL = "https://serverchatting.herokuapp.com/";
    private final int SERVER_PORT = 3000;
    private final String SERVER_URL_LOCAL = "http://192.168.79.1:3000";

    private final String SERVER_SEND_CONVERSATIONS = "SERVER_SEND_CONVERSATIONS";
    private final String SERVER_SEND_FRIENDS = "SERVER_SEND_FRIENDS";
    private final String SERVER_SEND_REQUEST_ADD_FRIEND = "SERVER_SEND_REQUEST_ADD_FRIEND";
    private  final String MEM_ROOM = "MEM_ROOM";
    private  final String SERVER_SEND_DATA_ME = "SERVER_SEND_DATA_ME";
    private  final String NAME = "NAME";
    private  final String EMAIL = "EMAIL";
    private  final String AVARTAR = "AVARTAR";
    private  final String STATE = "STATE";
    private  final String ID = "ID";

    // instance
    private static SingletonSocket INSTANCE = new SingletonSocket();

    public Socket mSocket;

    {
        try {
            IO.Options opts = new IO.Options();
            opts.port = SERVER_PORT;
            mSocket = IO.socket(SERVER_URL_LOCAL);
        } catch (URISyntaxException e) {
        }
    }

    // other instance variables can be here
    private SingletonSocket() {};

    public static SingletonSocket getInstance() {return(INSTANCE);}
    // other instance methods can follow

    public void ListeningToGetData(){
        mSocket.on(SERVER_SEND_DATA_ME, onListen_MyData);
        mSocket.on(SERVER_SEND_CONVERSATIONS, onListen_Conversations);
        mSocket.on(SERVER_SEND_FRIENDS, onListen_Friends);

    }

    public void ListeningRequest() {
        mSocket.on(SERVER_SEND_REQUEST_ADD_FRIEND, onListen_AddFriend);
    }

    private Emitter.Listener onListen_MyData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            JSONArray arrayRequest;
            try {
                Me.getInstance().setEmail(data.getString(EMAIL));
                Me.getInstance().setName(data.getString(NAME));
                // Need add avatar in here

                arrayRequest = data.getJSONArray(SERVER_SEND_REQUEST_ADD_FRIEND);
                for (int i = 1; i < arrayRequest.length(); i++){
                    Me.getInstance().listEUserRequest.add(arrayRequest.get(i).toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onListen_Friends = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            JSONArray array;
            try {
                array = data.getJSONArray(SERVER_SEND_FRIENDS);
                if (array != null){
                    for(int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        Friend fr =  new Friend( obj.getString("EMAIL"),
                                obj.getString("NAME"),
                                BitmapFactory.decodeFile("drawable://" + R.drawable.person),
                                (obj.getString("STATE").equals("online")) ? true:false);

                        if (!ListFriends.getInstance().getArray().contains(fr)) {
                            ListFriends.getInstance().add(fr);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onListen_Conversations = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            JSONArray array;
            try {
                array = data.getJSONArray(SERVER_SEND_CONVERSATIONS);
                if (array != null) {
                    for(int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        JSONObject latest = obj.getJSONObject("LATESTMESSAGE");

                        Gson gson = new Gson();
                        Calendar createdAt = gson.fromJson(latest.getString("time"), Calendar.class);
                        DateFormat formater = new DateFormat();
                        String timeString = formater.format("dd/MM hh:mm", createdAt).toString();

                        for(int j = 0; j < ListFriends.getInstance().getArray().size(); j++){
                            if (ListFriends.getInstance().getArray().get(j).getEmail().equals(obj.getString(EMAIL))){
                                if (latest.getString("type").equals("TEXT")){
                                    ListConversations.getInstance()
                                            .add(new Conversation(obj.getString(ID),ListFriends.getInstance().getArray().get(j),
                                                    latest.getString("message"), timeString,
                                                    latest.getString("sender").equals(Me.getInstance().getEmail()) ? 1:0));
                                }
                                if (latest.getString("type").equals("PICTURE")){
                                    ListConversations.getInstance()
                                            .add(new Conversation(obj.getString(ID),ListFriends.getInstance().getArray().get(j),
                                                    "picture...", timeString,
                                                    latest.getString("sender").equals(Me.getInstance().getEmail()) ? 1:0));
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onListen_AddFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                if (!Me.getInstance().listEUserRequest.contains(data.getString(SERVER_SEND_REQUEST_ADD_FRIEND))) {
                    Me.getInstance().listEUserRequest.add(data.getString(SERVER_SEND_REQUEST_ADD_FRIEND));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
