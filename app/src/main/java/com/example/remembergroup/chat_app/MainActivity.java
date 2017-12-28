package com.example.remembergroup.chat_app;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.remembergroup.adapter.ConversationAdapter;
import com.example.remembergroup.adapter.FriendAdapter;
import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.github.bassaer.chatmessageview.models.Message;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends Activity {

    private final String SERVER_SEND_CONVERSATIONS = "SERVER_SEND_CONVERSATIONS";
    private final String SERVER_SEND_FRIENDS = "SERVER_SEND_FRIENDS";
    private final String CLIENT_REQUEST_DATA = "CLIENT_REQUEST_DATA";
    private  final String SERVER_UPDATE_STATE_TO_OTHERS = "SERVER_UPDATE_STATE_TO_OTHERS";
    private  final String STATE = "STATE";
    private  final String EMAIL = "EMAIL";
    private  final String ID = "ID";
    private  final String SERVER_UPDATE_FRIENDS_ONLINE = "SERVER_UPDATE_FRIENDS_ONLINE";
    private  final String CON_CHAT = "CON_CHAT";
    private  final String SERVER_SEND_NEW_CONVERSATION = "SERVER_SEND_NEW_CONVERSATION";
    private final String SERVER_SEND_MESSAGE = "SERVER_SEND_MESSAGE";
    private final String SERVER_SEND_REQUEST_ADD_FRIEND = "SERVER_SEND_REQUEST_ADD_FRIEND";
    private final String SERVER_SEND_NEW_FRIEND = "SERVER_SEND_NEW_FRIEND";


    private Socket mSocket;
    TabHost tabHost;
    ListView lvConversations;
    ArrayList<Conversation> listConversations;
    ConversationAdapter adapterConversations;
    ListView lvFriends;
    ArrayList<Friend> listFriends;
    FriendAdapter adapterFriends;
    SearchView searchTool;
    ImageView imgMe;
    ImageView imgAddFriend;
    TextView txtNumRequest;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSocket();
        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterConversations.notifyDataSetChanged();
        adapterFriends.notifyDataSetChanged();

        if (Me.getInstance().listEUserRequest.size() != 0) {
            txtNumRequest.setText(String.valueOf(Me.getInstance().listEUserRequest.size()));
        } else {
            txtNumRequest.setText("");
        }
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;

        mSocket.on(SERVER_UPDATE_STATE_TO_OTHERS, onListen_UpdateStateToOthers);
        mSocket.on(SERVER_UPDATE_FRIENDS_ONLINE, onListen_UpdateFriendsOnline);
        mSocket.on(SERVER_SEND_NEW_CONVERSATION, onListen_NewConversation);
        mSocket.on(SERVER_SEND_MESSAGE, onListenServer_SendMessage);
        mSocket.on(SERVER_SEND_REQUEST_ADD_FRIEND, onListen_AddFriend);
        mSocket.on(SERVER_SEND_NEW_FRIEND, onListen_NewFriend);
    }

    //TO DO: add events
    private void addEvents() {
        lvConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchChatRoom(CON_CHAT, listConversations.get(i), true);
            }
        });

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean flag = false;

                for (int j = 0; j < ListConversations.getInstance().getArray().size(); j++){
                    if (ListConversations.getInstance().getArray().get(j).getFriend().getEmail().equals(listFriends.get(i))){
                        flag = true;
                        launchChatRoom(CON_CHAT, ListConversations.getInstance().getArray().get(j), true);
                    }
                }

                if (flag == false) {
                    String idString = listFriends.get(i).getEmail() + Me.getInstance().getEmail();
                    int idInt = 0;
                    for (int j = 0; j < idString.length(); j++){
                        idInt += idString.codePointAt(j);
                    }

                    launchChatRoom(CON_CHAT, new Conversation(String.valueOf(idInt), listFriends.get(i)), false);
                }
            }
        });


        searchTool.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterConversations.getFilter().filter(newText);
                adapterFriends.getFilter().filter(newText);
                return false;
            }
        });

        imgAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                startActivity(i);
            }
        });

        imgMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileMeActivity.class);
                startActivity(i);
            }
        });
    }


    //TO DO: setting user interface
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addControls() {
        //Generate tab
        tabHost= (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1=tabHost.newTabSpec("t1");
        tab1.setIndicator("Conversations");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2=tabHost.newTabSpec("t2");
        tab2.setIndicator("Friends");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);

        lvConversations = (ListView) findViewById(R.id.lvConversations);
        listConversations = ListConversations.getInstance().getArray();
        adapterConversations =new ConversationAdapter(this,R.layout.conversation, listConversations);
        lvConversations.setAdapter(adapterConversations);

        lvFriends = (ListView) findViewById(R.id.lvFriends);
        listFriends = ListFriends.getInstance().getArray();
        adapterFriends = new FriendAdapter(this, R.layout.friend, listFriends);
        lvFriends.setAdapter(adapterFriends);

        searchTool = findViewById(R.id.searchTool);
        imgAddFriend = findViewById(R.id.imgAddFriend);
        imgMe = findViewById(R.id.imgMe);
        //imgMe.setImageBitmap(Me.getInstance().getAvatar());
        txtNumRequest = findViewById(R.id.numRequestAddFriend);
    }

    // Launch chatRoom activity
    private void launchChatRoom(String key, Conversation cons, boolean isExist){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(key, cons);
        i.putExtra("isExist", isExist);
        startActivity(i);
    }


 /*   private Emitter.Listener onListen_Conversations = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray array;
                    try {
                        array = data.getJSONArray(SERVER_SEND_CONVERSATIONS);
                        if (array != null) {
                            for(int i = 0; i < array.length(); i++){
                                for(int j = 0; j < listFriends.size(); j++){
                                    if (listFriends.get(j).getEmail().equals(array.getString(i))){
                                        listConversations.add(listFriends.get(j));
                                    }
                                }
                            }

                            adapterConversations.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };*/

/*    private Emitter.Listener onListen_Friends = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray array;
                    try {
                        array = data.getJSONArray(SERVER_SEND_FRIENDS);
                        if (array != null){
                            for(int i = 0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                listFriends.add(new Friend( obj.getString("email"),
                                                            obj.getString("name"),
                                                            BitmapFactory.decodeResource(getResources(), R.drawable.person),
                                                            obj.getString("state")=="online" ? true:false));
                            }
                        }
                        adapterFriends.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };*/


    private Emitter.Listener onListen_NewConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String email;
                    String id;
                    String type;
                    String mess;
                    String time;

                    try {
                        email = data.getString(EMAIL);
                        id = data.getString(ID);
                        type = data.getString("TYPE");
                        if (type.equals("TEXT")){
                            mess = data.getString("MESSAGE");
                        }else if (type.equals("PICTURE")){
                            mess = "picture...";
                        }else {
                            mess = "audio...";
                        }

                        Gson gson = new Gson();
                        Calendar createdAt = gson.fromJson(data.getString("TIME"), Calendar.class);
                        DateFormat formater = new DateFormat();
                        time = formater.format("dd/MM hh:mm", createdAt).toString();

                        for(int i = 0; i < listFriends.size(); i++){
                            if (listFriends.get(i).getEmail().equals(email)){
                                listConversations.add(0,
                                        new Conversation(id, listFriends.get(i),
                                                mess, time, (email.equals(Me.getInstance().getEmail()) ? 1:0)));
                            }
                        }

                        adapterConversations.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListen_UpdateStateToOthers = new Emitter.Listener() {
        @Override
        public void call(final Object...args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String userEmail;
                    String state;
                    try {
                        userEmail = data.getString(EMAIL);
                        state = data.getString(STATE);
                        if (state.equals("online")) {
                            for(int i = 0; i < listFriends.size(); i++){
                                if (listFriends.get(i).getEmail().equals(userEmail)){
                                    listFriends.get(i).setOnline(true);
                                    if (i != 0) {
                                        Collections.swap(listFriends, 0, i);
                                    }
                                }
                            }

                            for(int i = 0; i < listConversations.size(); i++){
                                if (listConversations.get(i).getFriend().getEmail().equals(userEmail)){
                                    listConversations.get(i).getFriend().setOnline(true);
                                }
                            }
                        } else {
                            for(int i = 0; i < listFriends.size(); i++){
                                if (listFriends.get(i).getEmail().equals(userEmail)){
                                    listFriends.get(i).setOnline(false);
                                    if (i != listFriends.size() - 1) {
                                        Collections.swap(listFriends, listFriends.size() - 1, i);
                                    }
                                }
                            }

                            for(int i = 0; i < listConversations.size(); i++){
                                if (listConversations.get(i).getFriend().getEmail().equals(userEmail)){
                                    listConversations.get(i).getFriend().setOnline(false);
                                }
                            }
                        }

                        adapterConversations.notifyDataSetChanged();
                        adapterFriends.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListen_UpdateFriendsOnline = new Emitter.Listener() {
        @Override
        public void call(final Object...args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray array;
                    try {
                        array = data.getJSONArray(SERVER_UPDATE_FRIENDS_ONLINE);
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++){
                                for(int j = 0; j < listFriends.size(); j++){
                                    if (listFriends.get(j).getEmail().equals(array.getString(i))){
                                        listFriends.get(j).setOnline(true);
                                        if (j != 0) {
                                            Collections.swap(listFriends, 0, j);
                                        }
                                    }
                                }

                                for(int j = 0; j < listConversations.size(); j++){
                                    if (listConversations.get(j).getFriend().getEmail().equals(array.getString(i))){
                                        listConversations.get(j).getFriend().setOnline(true);
                                    }
                                }
                            }
                        }

                        adapterConversations.notifyDataSetChanged();
                        adapterFriends.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListenServer_SendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String mess;
                    String idRoom;
                    String type;
                    String time;

                    try {
                        idRoom = data.getString("ROOM");
                        type = data.getString("TYPE");
                        if (type.equals("TEXT")) {
                            mess = data.getString("MESSAGE");
                        } else if (type.equals("PICTURE")) {
                            mess = "picture...";
                        } else {
                            mess = "audio...";
                        }

                        Gson gson = new Gson();
                        Calendar createdAt = gson.fromJson(data.getString("TIME"), Calendar.class);
                        DateFormat formater = new DateFormat();
                        time = formater.format("dd/MM hh:mm", createdAt).toString();

                        for (int i = 0; i < ListConversations.getInstance().getArray().size(); i++){
                            if (ListConversations.getInstance().getArray().get(i).getId().equals(idRoom)){

                                ListConversations.getInstance().getArray().get(i).setLastMess(mess);
                                ListConversations.getInstance().getArray().get(i).setTimeCreated(time);
                                ListConversations.getInstance().getArray().get(i).setIsMe(
                                        data.getString("SENDER").equals(Me.getInstance().getEmail()) ? 1:0);
                            }
                        }

                        adapterConversations.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListen_AddFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (!Me.getInstance().listEUserRequest.contains(data.getString(SERVER_SEND_REQUEST_ADD_FRIEND))){
                            Me.getInstance().listEUserRequest.add(data.getString(SERVER_SEND_REQUEST_ADD_FRIEND));
                            txtNumRequest.setText(String.valueOf(Me.getInstance().listEUserRequest.size()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListen_NewFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
//                    try {
//
//                        Friend fr =  new Friend( data.getString("EMAIL"),
//                                data.getString("NAME"),
//                                BitmapFactory.decodeFile("drawable://" + R.drawable.person),
//                                (data.getString("STATE").equals("online")) ? true:false);
//
//                        if (!ListFriends.getInstance().getArray().contains(fr)) {
//                            ListFriends.getInstance().add(fr);
//                        }


//                        if (Me.getInstance().listEUserRequest.size() != 0) {
//                            txtNumRequest.setText(String.valueOf(Me.getInstance().listEUserRequest.size()));
//                        } else {
//                            txtNumRequest.setText("");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    adapterFriends.notifyDataSetChanged();
                }
            });
        }
    };
}
