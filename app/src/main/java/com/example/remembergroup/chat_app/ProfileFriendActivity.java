package com.example.remembergroup.chat_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.Signature;

import io.socket.emitter.Emitter;

/**
 * Created by Khai Lee on 12/31/2017.
 */

public class ProfileFriendActivity extends AppCompatActivity {
    private final String CLIENT_UNFRIEND = "CLIENT_UNFRIEND";
    private final String SERVER_UNFRIEND_SUCCESS = "SERVER_UNFRIEND_SUCCESS";

    private ImageView imgAvatar;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPhoneNum;
    private TextView unfriend;
    private Friend friend;
    private Handler handler;
    private Runnable runnable;

    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilefriend);

        Intent intent = this.getIntent();
        friend = (Friend) intent.getParcelableExtra("PROFILEFRIEND");

        SingletonSocket.getInstance().mSocket.on(SERVER_UNFRIEND_SUCCESS, onListen_UnfriendSuccess);
        addControl();
        addEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }


    private void addControl() {
        imgAvatar = findViewById(R.id.imgAvatar);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtPhoneNum = findViewById(R.id.txtPhoneNumber);
        unfriend = findViewById(R.id.txtUnfriend);

        imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(friend.getEmail()));
        txtEmail.setText(friend.getEmail());
        txtName.setText(friend.getName());
        txtPhoneNum.setText(friend.getPhoneNumber());

        alertDialogBuilder = new AlertDialog.Builder(ProfileFriendActivity.this);
        alertDialogBuilder.setMessage("Unfriend now, Y/N");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SingletonSocket.getInstance().mSocket.emit(CLIENT_UNFRIEND, friend.getEmail());

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unfriend failed", Toast.LENGTH_SHORT).show();
                    }
                };
                handler.postDelayed(runnable,2000);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void addEvent() {
        unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    Emitter.Listener onListen_UnfriendSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data =  args[0].toString();

                    if (!data.equals("")){
                        handler.removeCallbacks(runnable);
                        Intent i = new Intent(ProfileFriendActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unfriend failed", Toast.LENGTH_SHORT).show();
                        handler.removeCallbacks(runnable);
                    }
                }
            });
        }
    };
}
