package com.example.remembergroup.chat_app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.remembergroup.adapter.RequestAddFriendAdapter;
import com.example.remembergroup.adapter.UserAdapter;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Khai Lee on 12/25/2017.
 */

public class AddFriendActivity extends Activity {
    private final String CLIENT_PULL_USERS_NOT_FRIEND = "CLIENT_PULL_USERS_NOT_FRIEND";
    private final String SERVER_RES_USERS_NOT_FRIEND = "SERVER_RES_USERS_NOT_FRIEND";
    private final String SERVER_SEND_REQUEST_ADD_FRIEND = "SERVER_SEND_REQUEST_ADD_FRIEND";
    private final String SERVER_SEND_NEW_FRIEND = "SERVER_SEND_NEW_FRIEND";


    private Socket mSocket;
    TabHost tabHost;

    ListView lvRequestAddF;
    ListView lvSearchUser;

    ArrayList<User> listUserNotFriend;
    ArrayList<User> listUserNFToQR;
    ArrayList<User> listUserRequest;

    RequestAddFriendAdapter requestAdapter;
    UserAdapter userAdapter;

    SearchView searchTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addfriend);

        initSocket();
        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userAdapter.notifyDataSetChanged();
        requestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        listUserNotFriend.clear();
        listUserNFToQR.clear();
        listUserRequest.clear();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }


    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;

        mSocket.emit(CLIENT_PULL_USERS_NOT_FRIEND, listEmailFriendString());
        mSocket.on(SERVER_RES_USERS_NOT_FRIEND, onListen_UsersNotFriend);
        mSocket.on(SERVER_SEND_REQUEST_ADD_FRIEND, onListen_AddFriend);
        mSocket.on(SERVER_SEND_NEW_FRIEND, onListen_NewFriend);
    }

    private String listEmailFriendString(){
        ArrayList<String> listEmailFriend = new ArrayList<>();
        for (int i = 0; i < ListFriends.getInstance().getArray().size(); i++){
            listEmailFriend.add(ListFriends.getInstance().getArray().get(i).getEmail());
        }

        Gson gson = new Gson();

        return gson.toJson(listEmailFriend);
    }

    private void addControls() {
        //Generate tab
        tabHost= (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1=tabHost.newTabSpec("t1");
        tab1.setIndicator("Request");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2=tabHost.newTabSpec("t2");
        tab2.setIndicator("Make Friend");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);



        listUserRequest = new ArrayList<>();
        requestAdapter = new RequestAddFriendAdapter(this, R.layout.requestaddfriennd, listUserRequest);
        lvRequestAddF = findViewById(R.id.lvRequestAddFriend);
        lvRequestAddF.setAdapter(requestAdapter);

        listUserNotFriend = new ArrayList<>();
        listUserNFToQR = new ArrayList<>();
        userAdapter = new UserAdapter(this, R.layout.usertoaddfriend, listUserNotFriend);
        lvSearchUser = findViewById(R.id.lvSearchUsers);
        lvSearchUser.setAdapter(userAdapter);

        searchTool = findViewById(R.id.searchTool);
    }

    private void addEvents() {
        searchTool.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                requestAdapter.getFilter().filter(newText);
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        if(stringPicture=="")
            return null;
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    private Emitter.Listener onListen_UsersNotFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray arrayUser;
                    try {
                        arrayUser = data.getJSONArray(SERVER_RES_USERS_NOT_FRIEND);

                        if (arrayUser != null){
                            for(int i = 0; i < arrayUser.length(); i++) {
                                JSONObject obj = (JSONObject) arrayUser.get(i);
                                //Bitmap avatar = getBitmapFromString(obj.getString("AVATAR"));

                                listUserNotFriend.add(new User(obj.getString("EMAIL"), obj.getString("NAME"), BitmapFactory.decodeResource(getResources(), R.drawable.person2)));
                                listUserNFToQR.add(listUserNotFriend.get(i));

                                for(int j = 0; j < Me.getInstance().listEUserRequest.size(); j++){
                                    if (Me.getInstance().listEUserRequest.get(j).equals(obj.getString("EMAIL"))
                                            && !listUserRequest.contains(listUserNotFriend.get(i))){
                                        listUserRequest.add(listUserNotFriend.get(i));
                                    }
                                }
                            }
                        }

                        requestAdapter.notifyDataSetChanged();
                        userAdapter.notifyDataSetChanged();

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
                        String email = data.getString(SERVER_SEND_REQUEST_ADD_FRIEND);

                        for(int i = 0; i < listUserNFToQR.size(); i++){
                            if (listUserNFToQR.get(i).getEmail().equals(email)){
                                if (!Me.getInstance().listEUserRequest.contains(email)) {
                                    Me.getInstance().listEUserRequest.add(email);
                                }

                                if (!listUserRequest.contains(listUserNFToQR.get(i))) {
                                    listUserRequest.add(listUserNFToQR.get(i));
                                }
                            }
                        }

                        requestAdapter.notifyDataSetChanged();

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
                    try {
                        Friend fr =  new Friend( data.getString("EMAIL"),
                                data.getString("NAME"),
                                BitmapFactory.decodeFile("drawable://" + R.drawable.person),
                                (data.getString("STATE").equals("online")) ? true:false);

                        if (!ListFriends.getInstance().getArray().contains(fr)) {
                            ListFriends.getInstance().add(fr);
                        }

                        listUserNotFriend.remove(Me.getInstance().accepted);
                        userAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
