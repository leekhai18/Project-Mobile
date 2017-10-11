package com.example.remembergroup.chat_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;

import com.example.remembergroup.adapter.ConversationAdapter;
import com.example.remembergroup.adapter.FriendAdapter;
import com.example.remembergroup.model.Friend;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserActivity extends AppCompatActivity {

    private final String SERVER_SEND_CONVERSATIONS = "SERVER_SEND_CONVERSATIONS";
    private final String SERVER_SEND_FRIENDS = "SERVER_SEND_FRIENDS";
    private final String CLIENT_REQUEST_DATA = "CLIENT_REQUEST_DATA";
    //private final String SERVER_SEND_CONVERSATIONS = "SERVER_SEND_CONVERSATIONS";


    private Socket mSocket;
    TabHost tabHost;
    ListView lvConversations;
    ArrayList<Friend> listConversations;
    ConversationAdapter adapterConversations;
    ImageButton btnChat,btnProfile,btnSetting;
    ListView lvFriends;
    ArrayList<Friend> listFriends;
    FriendAdapter adapterFriends;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        addControls();

        settingUI();

        addEvents();

        initSocket();
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;

        mSocket.emit(CLIENT_REQUEST_DATA, "please send to me my data");
        mSocket.on(SERVER_SEND_CONVERSATIONS, onListen_Conversations);
        mSocket.on(SERVER_SEND_FRIENDS, onListen_Friends);
    }

    //TO DO: load id of controls from xml file
    private void settingUI() {
        //get size in resource
        int imageSizeW= (int) this.getResources().getDimension(R.dimen.imageSizeW);
        int imageSizeH= (int) this.getResources().getDimension(R.dimen.imageSizeH);

        //get width and height of screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        //calculate space of three imageButton
        int space= (screenWidth-imageSizeW*3)/4;
        //create layout params to three image button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSizeW, imageSizeH);
        params.setMargins(space,0,0,0);
        btnChat.setLayoutParams(params);
        btnProfile.setLayoutParams(params);
        btnSetting.setLayoutParams(params);
    }
    //TO DO: add events
    private void addEvents() {
        lvConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchChatRoom("MEM_ROOM", listConversations.get(i).getName());
            }
        });

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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

        btnChat= (ImageButton) findViewById(R.id.btnChat);
        btnProfile= (ImageButton) findViewById(R.id.btnProfile);
        btnSetting= (ImageButton) findViewById(R.id.btnSetting);

        lvConversations = (ListView) findViewById(R.id.lvConversations);
        Bitmap person1= BitmapFactory.decodeResource(getResources(), R.drawable.person);
        Bitmap person2= BitmapFactory.decodeResource(getResources(), R.drawable.person1);
        Bitmap person3= BitmapFactory.decodeResource(getResources(), R.drawable.person2);
        Friend friend1=new Friend("Nguyễn Thị Kiều Trang",person1);
        friend1.setOnline(true);
        friend1.setTextLast("OK, hôm sau nhafdsfsdfsdfsdfsdfsdfsdfsdfsddssd");
        Calendar calendar=Calendar.getInstance();

        friend1.setDateTime(calendar.getTime());

        Friend friend2=new Friend("Nguyễn Huy Hùng",person2);
        Friend friend3=new Friend("Nguyễn Thị Loan",person3);
        friend3.setDateTime(calendar.getTime());
        friend2.setDateTime(calendar.getTime());
        listConversations =new ArrayList<>();
        listConversations.add(friend1);
        listConversations.add(friend2);
        listConversations.add(friend3);
        adapterConversations =new ConversationAdapter(this,R.layout.conversation, listConversations);
        lvConversations.setAdapter(adapterConversations);

        lvFriends = (ListView) findViewById(R.id.lvFriends);
        listFriends = new ArrayList<>();
        adapterFriends = new FriendAdapter(this, R.layout.friend, listFriends);
        lvFriends.setAdapter(adapterFriends);
    }

    // Launch chatRoom activity
    private void launchChatRoom(String key, String data){
        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
        i.putExtra(key, data);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_view,menu);
        MenuItem mnuSearch=menu.findItem(R.id.mnuSearch);
        mnuSearch.setTitle("Search your conversation");
        SearchView searchView= (SearchView) mnuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterConversations.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private Emitter.Listener onListen_Conversations = new Emitter.Listener() {
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
                                    if (listFriends.get(j).getId().equals(array.getString(i))){
                                        listConversations.add(listFriends.get(j));
                                        Log.i("Test", "Change");
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
    };

    private Emitter.Listener onListen_Friends = new Emitter.Listener() {
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
    };
}
