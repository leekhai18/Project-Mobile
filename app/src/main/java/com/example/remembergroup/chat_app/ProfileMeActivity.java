package com.example.remembergroup.chat_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remembergroup.model.ListConversations;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ProfileMeActivity extends AppCompatActivity {
    private final String CLIENT_CHANGE_PASSWORD = "CLIENT_CHANGE_PASSWORD";
    private final String SERVER_CHANGE_PASSWORD_SUCCESS = "SERVER_CHANGE_PASSWORD_SUCCESS";
    private final String CLIENT_CHANGE_AVATAR = "CLIENT_CHANGE_AVATAR";
    private final String CLIENT_CHANGE_NAME = "CLIENT_CHANGE_NAME";
    private final String CLIENT_CHANGE_PHONE = "CLIENT_CHANGE_PHONE";

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
    private AlertDialog.Builder dialogChangeAvatar;


    private EditText currentpassword ;
    private EditText newpassword;
    private EditText newpasswordagain ;
    private Button btnChangepass;

    private AlertDialog dialogChangepassword;
    private AlertDialog dialogChangeText;

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

        dialogChangeAvatar = new AlertDialog.Builder(ProfileMeActivity.this);
        dialogChangeAvatar.setMessage("Change avatar, Y/N");
        dialogChangeAvatar.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                openGallery();
            }
        });
        dialogChangeAvatar.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                AlertDialog dialog = dialogChangeAvatar.create();
                dialog.show();
                return false;
            }
        });

        txtName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEditTextDialog("NAME");
                return false;
            }
        });

        txtPhoneNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEditTextDialog("PHONE");
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
                showChangeLangDialog();
            }
        });
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        SingletonSocket.getInstance().ListeningRequest();
        mSocket.on(SERVER_CHANGE_PASSWORD_SUCCESS, onListen_ChangepasswordSuccess);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }

    private void showEditTextDialog(final String what) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.changetext_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.edittext);
        Button btnChange = dialogView.findViewById(R.id.btnChange);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonChangeText(what, editText.getText().toString());
            }
        });

        dialogChangeText = dialogBuilder.create();
        dialogChangeText.show();
        resizeDialog(dialogChangeText, 0.7f, 0.3f);
    }

    private void handleButtonChangeText(String what, String text){
        if (text.equals("")) {
            Toast.makeText(getApplicationContext(), "please fill in box", Toast.LENGTH_SHORT).show();
        } else {
            if (what.equals("NAME")){
                mSocket.emit(CLIENT_CHANGE_NAME, text);
                Toast.makeText(getApplicationContext(), "change name successfully", Toast.LENGTH_SHORT).show();
                txtName.setText(text);
                Me.getInstance().setName(text);
                dialogChangeText.dismiss();
            }

            if (what.equals("PHONE")){
                mSocket.emit(CLIENT_CHANGE_PHONE, text);
                Toast.makeText(getApplicationContext(), "change phone number successfully", Toast.LENGTH_SHORT).show();
                txtPhoneNumber.setText(text);
                Me.getInstance().setPhoneNumber(text);
                dialogChangeText.dismiss();
            }
        }
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.changepassword_dialog, null);
        dialogBuilder.setView(dialogView);

        currentpassword = (EditText) dialogView.findViewById(R.id.currentpassword);
        newpassword = dialogView.findViewById(R.id.password);
        newpasswordagain = dialogView.findViewById(R.id.passwordagain);
        btnChangepass = dialogView.findViewById(R.id.btnChange);

        btnChangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonChangePassword();
            }
        });

        dialogChangepassword = dialogBuilder.create();
        dialogChangepassword.show();
        resizeDialog(dialogChangepassword, 0.7f,0.5f);
    }

    private void resizeDialog(AlertDialog mdialog, float width, float height){
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(mdialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * width);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * height);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        mdialog.getWindow().setAttributes(layoutParams);
    }

    private void handleButtonChangePassword(){
        if (currentpassword.getText().toString().equals("") || newpassword.getText().toString().equals("") || newpasswordagain.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "please fill in full filed", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences pref;
            pref = getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);

            if (currentpassword.getText().toString().equals(pref.getString("password", ""))){
                if (newpassword.getText().toString().equals(newpasswordagain.getText().toString())){
                    dialogChangepassword.dismiss();
                    mSocket.emit(CLIENT_CHANGE_PASSWORD, newpassword.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "password again is wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), " currentpassword is wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final int PICK_IMAGE_REQUEST = 2;
    private void openGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgAvatar.setImageBitmap(imageBitmap);
                MemoryManager.getInstance().addBitmapToMemoryCache(Me.getInstance().getEmail(), imageBitmap);
                Toast.makeText(getApplicationContext(), "change avatar successfully", Toast.LENGTH_SHORT).show();
                mSocket.emit(CLIENT_CHANGE_AVATAR, getStringFromBitmap(imageBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
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

    Emitter.Listener onListen_ChangepasswordSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data =  args[0].toString();

                    if (data.equals("true")){
                        Toast.makeText(getApplicationContext(), "change password successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "change password failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };
}
