package com.example.remembergroup.chat_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Khai Lee on 10/11/2017.
 */

public class RegisterActivity extends Activity {
    private final String CLIENT_REGISTER = "CLIENT_REGISTER";
    private final String SERVER_RE_REGISTER = "SERVER_RE_REGISTER";
    private final String SERVER_RE_CHECK_EXISTENCE = "SERVER_RE_CHECK_EXISTENCE";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPhoneNumber;
    private ImageView imgAvatar;
    private Button btnChooseAvatar;
    private AlertDialog.Builder dialog;

    private Socket mSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSocket = SingletonSocket.getInstance().mSocket;

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChooseAvatar = findViewById(R.id.btnChooseAvatar);

        dialog = new AlertDialog.Builder(this);

        // Listening
        mSocket.on(SERVER_RE_REGISTER, onRegister);
        mSocket.on(SERVER_RE_CHECK_EXISTENCE, onCheckExistence);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendInfoRegister();
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                launchLogin();
            }
        });

        btnChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        this.finish();
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

    private void sendInfoRegister() {
        String username = inputFullName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String avatarBitmapString = getStringFromBitmap(((BitmapDrawable) imgAvatar.getDrawable()).getBitmap());

        boolean checkInfo = checkInputInfoUser(username, email, password, phoneNumber);
        if (checkInfo == true) {
            mSocket.emit(CLIENT_REGISTER, username, password, email, phoneNumber, avatarBitmapString);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkInputInfoUser(String username, String email, String password, String phone) {
        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phone.isEmpty()) {
            return true;
        }
        return false;
    }

    // Launch login activity
    private void launchLogin(){
        Intent i = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(i);
        finish();
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Emitter.Listener onCheckExistence = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();

                    if (data == "true") {
                        Toast.makeText(getApplicationContext(), "Email has existed!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.setMessage("Registering...");

                        if(!RegisterActivity.this.isFinishing())
                        {
                            dialog.show();
                        }
                    }
                }
            });
        }
    };



    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();

                    if (data == "true") {
                        Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                        launchLogin();
                    }
                }
            });
        }
    };
}
