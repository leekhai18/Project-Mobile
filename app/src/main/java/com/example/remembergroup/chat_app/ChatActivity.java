package com.example.remembergroup.chat_app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.remembergroup.adapter.ChatAdapter;
import com.example.remembergroup.model.Constant;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ImageMessage;
import com.example.remembergroup.model.Message;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.remembergroup.model.Constant.CAMERA_REQUEST;

public class ChatActivity extends AppCompatActivity {
    private final String CLIENT_SEND_MESSAGE = "CLIENT_SEND_MESSAGE";
    private final String SERVER_SEND_MESSAGE = "SERVER_SEND_MESSAGE";
    private final String MESSAGE = "MESSAGE";
    private final String SENDER = "SENDER";
    private  final String MEM_ROOM = "MEM_ROOM";

    private Socket mSocket;

    ListView lvChat;
    ChatAdapter chatAdapter;
    ArrayList<Message> messageArrayList;
    Friend friendChat;
    ImageButton btnSend,btnImage,btnEmotion,btnCamera;
    EditText txtMessage;
    TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initFriend();
        initMine();
        addControls();
        addEvents();
        initSocket();
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        mSocket.on(SERVER_SEND_MESSAGE, onListenServer_SendMessage);
    }


    private void addControls() {
        ArrayList<Integer> listLayout=new ArrayList<>();
        listLayout.add(R.layout.your_chat);
        listLayout.add(R.layout.your_friend_chat);
        listLayout.add(R.layout.your_image_chat);
        listLayout.add(R.layout.your_friend_image_chat);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText(friendChat.getName());
        lvChat= (ListView) findViewById(R.id.lvChat);
        btnSend= (ImageButton) findViewById(R.id.btnSend);
        btnImage= (ImageButton) findViewById(R.id.btnImage);
        btnCamera= (ImageButton) findViewById(R.id.btnCamera);
        btnEmotion= (ImageButton) findViewById(R.id.btnEmotion);
        txtMessage= (EditText) findViewById(R.id.txtMessage);
        messageArrayList =new ArrayList<>();
        chatAdapter=new ChatAdapter(this,listLayout, messageArrayList, friendChat);
        lvChat.setAdapter(chatAdapter);
    }

    private void initMine() {
        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.person2);
        Me.getInstance().setAvatar(avatar);
    }

    private void initFriend() {
        Intent intent = this.getIntent();
        Friend friendChatWith = (Friend) intent.getParcelableExtra(MEM_ROOM);
        if (friendChatWith != null) {
            setFriend(friendChatWith);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void setFriend(Friend fr) {
        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.person1);
        fr.setAvatar(avatar);
        friendChat = fr;
    }

    private void addEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromCamera();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, UserActivity.class);
        startActivity(i);
    }

    //TO DO: get image from camera
    private void getImageFromCamera() {

//        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.person1);
//        ImageMessage imageMessage=new ImageMessage(Constant.TYPE_IMAGE_MESSAGE,true,icon);
//        messageArrayList.add(imageMessage);
//        chatAdapter.notifyDataSetChanged();
//        scrollMyListViewToBottom();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void sendMessage() {
        TextMessage message = new TextMessage(txtMessage.getText().toString(), true);
        messageArrayList.add(message);
        chatAdapter.notifyDataSetChanged();
        scrollMyListViewToBottom();

        org.json.simple.JSONObject  obj = new org.json.simple.JSONObject();
        obj.put("message", txtMessage.getText().toString());
        obj.put("receiver", friendChat.getEmail());
        mSocket.emit(CLIENT_SEND_MESSAGE, obj);
    }

    private void scrollMyListViewToBottom() {
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvChat.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageMessage imageMessage=new ImageMessage(photo, true);
            messageArrayList.add(imageMessage);
            chatAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();
        }
    }


    private Emitter.Listener onListenServer_SendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String sender;
                    try {
                        message = data.getString(MESSAGE);
                        sender = data.getString(SENDER);
                        messageArrayList.add(new TextMessage(message, false));
                        chatAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
