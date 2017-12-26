package com.example.remembergroup.chat_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.IUser;
import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.TimeCreated;
import com.github.bassaer.chatmessageview.util.DateFormatter;
import com.github.bassaer.chatmessageview.util.ITimeFormatter;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.github.bassaer.chatmessageview.models.Message;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends Activity {
    private final String CLIENT_SEND_MESSAGE = "CLIENT_SEND_MESSAGE";
    private final String SERVER_SEND_MESSAGE = "SERVER_SEND_MESSAGE";
    private final String CLIENT_REQUEST_N_LAST_MESSAGE = "CLIENT_REQUEST_N_LAST_MESSAGE";
    private final String SERVER_RES_N_LAST_MESSAGE = "SERVER_RES_N_LAST_MESSAGE";
    private final String MESSAGE = "MESSAGE";
    private final String TYPE = "TYPE";
    private final String TIME = "TIME";
    private final String ROOM = "ROOM";
    private  final String CON_CHAT = "CON_CHAT";
    private final String TEXT = "TEXT";
    private final String PICTURE = "PICTURE";
    private final String AUDIO = "AUDIO";


    private Socket mSocket;
    Gson gson;

    Conversation conversation;
    ChatView chatView;
    IUser me;
    IUser friend;

    GridLayout mediaLayout;
    LinearLayout parentLayout;
    LinearLayout gallery;
    LinearLayout phoneCall;
    LinearLayout camera;
    LinearLayout voice;

    TextView txtFriendName;
    TextView txtStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        addControls();
        addEvents();

        initFriend();
        initMine();
        initSocket();
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        mSocket.on(SERVER_SEND_MESSAGE, onListenServer_SendMessage);
        mSocket.emit(CLIENT_REQUEST_N_LAST_MESSAGE, conversation.getId());
        mSocket.on(SERVER_RES_N_LAST_MESSAGE, onListenServer_LastMessages);
    }

    private void addControls() {
        chatView = (ChatView) findViewById(R.id.chatView);
        parentLayout = findViewById(R.id.parentLayout);
        mediaLayout = findViewById(R.id.mediaLayout);
        gallery = findViewById(R.id.btnGallery);
        phoneCall = findViewById(R.id.btnPhoneCall);
        camera = findViewById(R.id.btnCamera);
        voice = findViewById(R.id.btnVoice);

        mFileName = getExternalCacheDir().getAbsolutePath() + "/AudioRecording.3gp";
        ActivityCompat.requestPermissions(ChatActivity.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        txtFriendName = findViewById(R.id.txtFriendName);
        txtStatus = findViewById(R.id.txtStatus);

        gson = new Gson();
    }

    private void initMine() {
        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.person2);

        me = new IUser(Me.getInstance().getEmail(), Me.getInstance().getName(),avatar);
    }

    private void initFriend() {
        Intent intent = this.getIntent();
        conversation = (Conversation) intent.getParcelableExtra(CON_CHAT);

        if (conversation != null) {
            Bitmap avatar = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.person1);
            friend = new IUser(conversation.getFriend().getEmail(), conversation.getFriend().getName(), avatar);

            txtFriendName.setText(conversation.getFriend().getName());

            if (conversation.getFriend().isOnline()){
                txtStatus.setText("Active now");
            }else{
                txtStatus.setText("Offline");
            }
        }
    }

    private void addEvents() {
        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSendButton();
            }
        });

        chatView.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOptionButton();
            }
        });



        mediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOptionMenu();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGallery();
            }
        });

        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPhoneCall();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCamera();
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickVoice();
                closeOptionMenu();
            }
        });
    }

    private void clickVoice() {
        showVoicePopupWindow();
    }

    private void clickCamera() {
        openCamera();
    }

    private void clickPhoneCall() {
        Log.i("phonecall", "clickPhoneCall: phonecall");
    }

    private void clickGallery() {
        openGallery();
    }

    private void clickSendButton() {
        sendMessageText(chatView.getInputText());
    }

    private void clickOptionButton(){
        // Check keyboard showing
        View view = this.getCurrentFocus();
        if (view != null) {
            parentLayout.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaLayout.animate().translationY(-mediaLayout.getHeight());
                    mediaLayout.setVisibility(View.VISIBLE);
                    chatView.animate().translationY(-mediaLayout.getHeight()*0.3f);
                }
            }, 200);
        }
        else {
            mediaLayout.animate().translationY(-mediaLayout.getHeight());
            mediaLayout.setVisibility(View.VISIBLE);
            chatView.animate().translationY(-mediaLayout.getHeight()*0.3f);
        }
    }

    private void closeOptionMenu() {
        mediaLayout.animate().translationY(0);
        mediaLayout.setVisibility(View.GONE);
        chatView.animate().translationY(0);
    }

    private void sendMessageText(String content){
        Calendar createdAt = Calendar.getInstance();
        String timeCreatedString = gson.toJson(createdAt);

        Message mMessage = new Message.Builder()
                .setUser(me)
                .setMessageText(chatView.getInputText())
                .setRightMessage(true)
                .hideIcon(true)
                .setCreatedAt(createdAt)
                .setUsernameVisibility(false)
                .build();
        chatView.send(mMessage);

        //checkIsExistConversation
        org.json.simple.JSONObject  obj = new org.json.simple.JSONObject();
        obj.put("type", TEXT);
        obj.put("message", content);
        obj.put("time", timeCreatedString);
        obj.put("receiver", friend.getId());
        if (! this.getIntent().getBooleanExtra("isExist", false)){
            boolean flag = false;
            for (int i = 0; i < ListConversations.getInstance().getArray().size(); i++){
                if (ListConversations.getInstance().getArray().get(i).getId().equals(conversation.getId())){
                    flag = true;
                }
            }

            if (flag == false) {
                ListConversations.getInstance().getArray().add(0, conversation);
            }
        }
        obj.put("idConversation", conversation.getId());

        mSocket.emit(CLIENT_SEND_MESSAGE, obj);

        chatView.setInputText("");
    }

    private void sendMessagePicture(Bitmap picture) {
        Calendar createdAt = Calendar.getInstance();
        String timeCreatedString = gson.toJson(createdAt);

        Message mMessage = new Message.Builder()
                .setRightMessage(true)
                .setUser(me)
                .setPicture(picture) // Set picture
                .setType(Message.Type.PICTURE) //Set Message Type
                .setUsernameVisibility(false)
                .hideIcon(true)
                .setCreatedAt(createdAt)
                .build();
        chatView.send(mMessage);

        org.json.simple.JSONObject  obj = new org.json.simple.JSONObject();
        obj.put("type", PICTURE);
        obj.put("time", timeCreatedString);
        obj.put("message", this.getStringFromBitmap(picture));
        obj.put("receiver", friend.getId());

        //checkIsExistConversation
        if (! this.getIntent().getBooleanExtra("isExist", false)){
            boolean flag = false;
            for (int i = 0; i < ListConversations.getInstance().getArray().size(); i++){
                if (ListConversations.getInstance().getArray().get(i).getId().equals(conversation.getId())){
                    flag = true;
                }
            }

            if (flag == false) {
                ListConversations.getInstance().getArray().add(0, conversation);
            }
        }
        obj.put("idConversation", conversation.getId());

        mSocket.emit(CLIENT_SEND_MESSAGE, obj);
    }

    private void sendMessageAudio(String filename){
        String data = Base64.encodeToString(audioToByteArray(filename), Base64.DEFAULT);

        org.json.simple.JSONObject  obj = new org.json.simple.JSONObject();
        obj.put("type", AUDIO);
        obj.put("message", data);
        obj.put("receiver", friend.getId());

        //checkIsExistConversation
        if (! this.getIntent().getBooleanExtra("isExist", false)){
            boolean flag = false;
            for (int i = 0; i < ListConversations.getInstance().getArray().size(); i++){
                if (ListConversations.getInstance().getArray().get(i).getId().equals(conversation.getId())){
                    flag = true;
                }
            }

            if (flag == false) {
                ListConversations.getInstance().getArray().add(0, conversation);
            }
        }
        obj.put("idConversation", conversation.getId());

        mSocket.emit(CLIENT_SEND_MESSAGE, obj);
    }


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
                        mess = data.getString(MESSAGE);
                        idRoom = data.getString(ROOM);
                        type = data.getString(TYPE);

                        if (idRoom.equals(conversation.getId())){
                            if (type.equals(TEXT)){
                                time = data.getString(TIME);
                                Calendar createdMess = gson.fromJson(time, Calendar.class);

                                Message receivedMessage = new Message.Builder()
                                        .setUser(friend)
                                        .setMessageText(mess)
                                        .hideIcon(false)
                                        .setRightMessage(false)
                                        .setUsernameVisibility(false)
                                        .setCreatedAt(createdMess)
                                        .build();
                                chatView.receive(receivedMessage);
                            }

                            if (type.equals(PICTURE)){
                                time = data.getString(TIME);
                                Calendar createdMess = gson.fromJson(time, Calendar.class);

                                Bitmap picture = getBitmapFromString(mess);
                                Message receivedMessage = new Message.Builder()
                                        .setRightMessage(false)
                                        .setUser(friend)
                                        .setPicture(picture) // Set picture
                                        .setType(Message.Type.PICTURE) //Set Message Type
                                        .setUsernameVisibility(false)
                                        .setCreatedAt(createdMess)
                                        .hideIcon(false)
                                        .build();
                                chatView.receive(receivedMessage);
                            }

                            if (type.equals(AUDIO)){
                                byte[] audioBytes = Base64.decode(mess, Base64.DEFAULT);
                                playAudioFromByte(audioBytes);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListenServer_LastMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray array;
                    try {
                        array = data.getJSONArray(SERVER_RES_N_LAST_MESSAGE);

                        if (array != null){
                            ArrayList<Message> listMessages = new ArrayList<Message>();

                            for(int i = 0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);

                                Calendar createdAt = gson.fromJson(obj.getString("time"), Calendar.class);

                                if (obj.getString("type").equals(TEXT)){
                                    if (obj.getString("sender").equals(Me.getInstance().getEmail())){
                                        Message mMessage = new Message.Builder()
                                                .setUser(me)
                                                .setMessageText(obj.getString("message"))
                                                .hideIcon(true)
                                                .setRightMessage(true)
                                                .setUsernameVisibility(false)
                                                .setCreatedAt(createdAt)
                                                .build();
                                        listMessages.add(mMessage);
                                    } else {
                                        Message receivedMessage = new Message.Builder()
                                                .setUser(friend)
                                                .setMessageText(obj.getString("message"))
                                                .hideIcon(false)
                                                .setRightMessage(false)
                                                .setUsernameVisibility(false)
                                                .setCreatedAt(createdAt)
                                                .build();
                                        listMessages.add(receivedMessage);
                                    }
                                }

                                if (obj.getString("type").equals(PICTURE)) {
                                    Bitmap picture = getBitmapFromString(obj.getString("message"));

                                    if (obj.getString("sender").equals(Me.getInstance().getEmail())){
                                        Message mMessage = new Message.Builder()
                                                .setUser(me)
                                                .setPicture(picture)
                                                .setType(Message.Type.PICTURE)
                                                .hideIcon(true)
                                                .setRightMessage(true)
                                                .setUsernameVisibility(false)
                                                .setCreatedAt(createdAt)
                                                .build();
                                        listMessages.add(mMessage);
                                    } else {
                                        Message receivedMessage = new Message.Builder()
                                                .setUser(friend)
                                                .setPicture(picture)
                                                .setType(Message.Type.PICTURE)
                                                .hideIcon(false)
                                                .setRightMessage(false)
                                                .setUsernameVisibility(false)
                                                .setCreatedAt(createdAt)
                                                .build();
                                        listMessages.add(receivedMessage);
                                    }
                                }
                            }

                            chatView.getMessageView().init(listMessages);
                            chatView.hideKeyboard();
                            chatView.getMessageView().setSelection(chatView.getMessageView().getCount() - 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }


    //Region: Helper
    private String getStringFromBitmap(Bitmap bitmapPicture) {
         /*
         * This functions converts Bitmap picture to a string which can be
         * JSONified.
         * */
        if(bitmapPicture==null)
            return "";
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        /*
        * This Function converts the String back to Bitmap
        * */
        if(stringPicture=="")
            return null;
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    // take a photo with camera
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        closeOptionMenu();
    }

    private final int PICK_IMAGE_REQUEST = 2;
    private void openGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        closeOptionMenu();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            sendMessagePicture(imageBitmap);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                sendMessagePicture(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //
    private PopupWindow window;
    private String mFileName = null;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 3;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private void showVoicePopupWindow(){
        try {
            LinearLayout mic;

            LayoutInflater inflater = (LayoutInflater) ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.voicepopupwindow, null);
            window = new PopupWindow(layout, 250, 250, true);

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setOutsideTouchable(true);
            window.showAtLocation(layout, Gravity.CENTER, 0, 0);

            mic = layout.findViewById(R.id.btnMic);

            mic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        onRecord(true);
                    }

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        onRecord(false);
                        //send mess type voice
                        sendMessageAudio(mFileName);
                        window.dismiss();
                    }

                    return true;
                }
            });

        } catch (Exception e){
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("prepare recorder", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private byte[] audioToByteArray(String fileName){
        byte[] soundBytes = null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(fileName)));
            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }

        return soundBytes;
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

    private void playAudioFromByte(byte[] soundByteArray) {
        try {
            mPlayer = new MediaPlayer();

            // create temp file that will hold byte array
            File tempFile = File.createTempFile("kurchina", "3gp", getCacheDir());
            tempFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(soundByteArray);
            fos.close();

            FileInputStream fis = new FileInputStream(tempFile);
            mPlayer.setDataSource(fis.getFD());

            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

}
