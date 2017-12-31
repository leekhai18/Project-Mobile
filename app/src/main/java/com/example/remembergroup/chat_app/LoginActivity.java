package com.example.remembergroup.chat_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity  {
    private final String SERVER_RE_LOGIN = "SERVER_RE_LOGIN";
    private final String CLIENT_LOGIN = "CLIENT_LOGIN";
    private final String CLIENT_REQUEST_DATA = "CLIENT_REQUEST_DATA";
    private final String CLIENT_FORGOT_PASSWORD = "CLIENT_FORGOT_PASSWORD";
    private final String SERVER_SEND_PASSWORD = "SERVER_SEND_PASSWORD";

    private Handler handler;
    private Runnable runnable;
    private boolean flagForceOff;
    private Socket mSocket;
    private ProgressDialog pDialog;
    EditText txtEmail,txtPassword;
    Button btnSignIn,btnForgotPassword,btnSignUp;
    LoginButton btnLoginFB;
    CallbackManager callbackManager;

    private AlertDialog dialogForgotPassword;
    private EditText edtRealEmail;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.remembergroup.chat_app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        addControls();
        addEvents();
        initSocket();
    }



    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;

        mSocket.on(SERVER_RE_LOGIN, onLogin);
        SingletonSocket.getInstance().ListeningToGetData();
        mSocket.on(SERVER_SEND_PASSWORD, onListen_GetPassword);

        //mSocket.connect();
        if (! mSocket.connected()) {
            mSocket.connect();
        }
    }

    private void addControls() {
        flagForceOff = false;

        txtEmail= (EditText) findViewById(R.id.txtEmail);
        txtPassword= (EditText) findViewById(R.id.txtPassword);
        btnLoginFB= (LoginButton) findViewById(R.id.btnLoginFB);
        btnForgotPassword= (Button) findViewById(R.id.btnForgotPassword);
        btnSignIn= (Button) findViewById(R.id.btnSignIn);
        btnSignUp= (Button) findViewById(R.id.btnSignUp);

        // Progress dialog
        pDialog = new ProgressDialog(this);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void addEvents() {
        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this,"Login sucess",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,"Login cancel",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog();
            }
        });

    }

    private void showEditTextDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.forgotpassword_dialog, null);
        dialogBuilder.setView(dialogView);

        edtRealEmail = dialogView.findViewById(R.id.txtRealEmail);
        edtEmail = dialogView.findViewById(R.id.txtEmail);
        Button btnGet = dialogView.findViewById(R.id.btnGetPassword);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtEmail.getText().toString().equals("") || edtRealEmail.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please fill in full filed", Toast.LENGTH_SHORT).show();
                } else {
                    mSocket.emit(CLIENT_FORGOT_PASSWORD, edtEmail.getText().toString());
                }
            }
        });

        dialogForgotPassword = dialogBuilder.create();
        dialogForgotPassword.show();
        resizeDialog(dialogForgotPassword, 0.8f, 0.3f);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request

        pDialog.setMessage("Logging in...");
        showDialog();

        mSocket.emit(CLIENT_LOGIN, email, password);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing() && !LoginActivity.this.isFinishing()) {
            pDialog.dismiss();
            pDialog.cancel();
        }
    }

    private void writeAccount() {
        //set activity_executed inside insert() method.
        SharedPreferences pref = getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();

        edt.putString("email", Me.getInstance().getEmail());
        edt.putString("password", txtPassword.getText().toString().trim());

        edt.commit();
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data =  args[0].toString();

                    if(data.equals("true")){
                        mSocket.emit(CLIENT_REQUEST_DATA, "please send to me my data");

                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);

                        handler.postDelayed(runnable, 2000);

                        while(true){
                            if(!Me.getInstance().getEmail().equals("")){
                                handler.removeCallbacks(runnable);
                                hideDialog();
                                writeAccount();
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Email or password is wrong", Toast.LENGTH_SHORT).show();
                        handler.removeCallbacks(runnable);
                    }
                }
            });
        }
    };

    Emitter.Listener onListen_GetPassword = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();

                    if (data.equals("false")){
                        Toast.makeText(getApplicationContext(), "Email of chat app does not exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your password just send to your email", Toast.LENGTH_SHORT).show();
                        dialogForgotPassword.dismiss();
                    }
                }
            });
        }
    };
}
