package com.example.remembergroup.messenger2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.example.remembergroup.adapter.FriendAdapter;
import com.example.remembergroup.model.Friend;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    TabHost tabHost;
    ListView lvFriend;
    ArrayList<Friend> friends;
    FriendAdapter friendAdapter;
    ImageButton btnChat,btnProfile,btnSetting;
    ListView lvFriendsOnline;
    ArrayList<Friend> listFriendsOnline;
    FriendAdapter adapterFriendsOnline;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        addControls();

        settingUI();

        addEvents();
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
        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchChatRoom("MEM_ROOM", friends.get(i).getName());
            }
        });

        lvFriendsOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        tab1.setIndicator("Your message");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2=tabHost.newTabSpec("t2");
        tab2.setIndicator("Nearly");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);

        TabHost.TabSpec tab3=tabHost.newTabSpec("t2");
        tab3.setIndicator("Active message");
        tab3.setContent(R.id.tab3);
        tabHost.addTab(tab3);

        btnChat= (ImageButton) findViewById(R.id.btnChat);
        btnProfile= (ImageButton) findViewById(R.id.btnProfile);
        btnSetting= (ImageButton) findViewById(R.id.btnSetting);

        lvFriend= (ListView) findViewById(R.id.lvFriends);
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
        friends=new ArrayList<>();
        friends.add(friend1);
        friends.add(friend2);
        friends.add(friend3);
        friendAdapter=new FriendAdapter(this,R.layout.message,friends);
        lvFriend.setAdapter(friendAdapter);

        lvFriendsOnline = (ListView) findViewById(R.id.lvFriendsOnline);
        adapterFriendsOnline = new FriendAdapter(this, R.layout.friend_active, listFriendsOnline);
        listFriendsOnline = new ArrayList<>();
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
        mnuSearch.setTitle("Search your message");
        SearchView searchView= (SearchView) mnuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
