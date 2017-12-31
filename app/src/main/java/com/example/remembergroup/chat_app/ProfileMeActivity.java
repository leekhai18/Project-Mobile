package com.example.remembergroup.chat_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;

import io.socket.client.Socket;

public class ProfileMeActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPhoneNumber;
    private ImageView imgAvatar;
    private LinearLayout switchAccount;
    private LinearLayout aboutApp;
    private LinearLayout changePassword;

    private Socket mSocket;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog.Builder dialogAbout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileme);

        addControl();
        addEvent();
        initSocket();
    }

    @Override
    protected void onStart() {
        super.onStart();

        txtName.setText(Me.getInstance().getName());
        txtEmail.setText(Me.getInstance().getEmail());
        txtEmail.setEnabled(false);
        txtPhoneNumber.setText(Me.getInstance().getPhoneNumber());
        imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(Me.getInstance().getEmail()));
    }

    private void addControl() {
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        imgAvatar = findViewById(R.id.imgAvatar);
        switchAccount = findViewById(R.id.switchAccount);
        aboutApp = findViewById(R.id.aboutApp);
        changePassword = findViewById(R.id.changePassword);

        dialogAbout = new AlertDialog.Builder(this);
        dialogAbout.setTitle("About Chat Closer");
        dialogAbout.setMessage(
                "Version: 1.0.0\n" + "Produce:\n" +
                "   Le Tuan Khai\n" +
                "   Chau Van Sang\n" +
                "   Tran Thien Hoa");

        alertDialogBuilder = new AlertDialog.Builder(ProfileMeActivity.this);
        alertDialogBuilder.setMessage("Switch account, Y/N");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mSocket.disconnect();
                ListFriends.getInstance().getArray().clear();
                ListFriends.getInstance().getArray().removeAll(ListFriends.getInstance().getArray());
                ListConversations.getInstance().getArray().clear();
                ListConversations.getInstance().getArray().removeAll(ListConversations.getInstance().getArray());
                SingletonSocket.getInstance().wasListenConversation = false;
                SingletonSocket.getInstance().wasListenFriend = false;
                Me.getInstance().setEmail("");
                Me.getInstance().listEUserRequest.clear();
                Me.getInstance().listEUserRequest.removeAll(Me.getInstance().listEUserRequest);

                Intent i = new Intent(ProfileMeActivity.this, LoginActivity.class);
                finish();
                startActivity(i);
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
        imgAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.i("AVATAR", "onLongClick: AVATAR");
                return false;
            }
        });

        txtName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i("NAME", "onLongClick: NAME");
                return false;
            }
        });

        txtPhoneNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i("PHONENUMBER", "onLongClick: PHONENUMBER");
                return true;
            }
        });

        switchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = dialogAbout.create();
                dialog.show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        SingletonSocket.getInstance().ListeningRequest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }
}
