package com.example.remembergroup.chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity  {
    private final String SERVER_RE_LOGIN = "SERVER_RE_LOGIN";
    private final String CLIENT_LOGIN = "CLIENT_LOGIN";

    private Socket mSocket;
    private ProgressDialog pDialog;
    EditText txtEmail,txtPassword;
    Button btnSignIn,btnForgotPassword,btnSignUp;
    LoginButton btnLoginFB;
    CallbackManager callbackManager;
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
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
        initSocket();
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        mSocket.on(SERVER_RE_LOGIN, onLogin);

        //mSocket.connect();
        if (! mSocket.connected()) {
            mSocket.connect();
        }
    }

    private void addControls() {

        txtEmail= (EditText) findViewById(R.id.txtEmail);
        txtPassword= (EditText) findViewById(R.id.txtPassword);
        btnLoginFB= (LoginButton) findViewById(R.id.btnLoginFB);
        btnForgotPassword= (Button) findViewById(R.id.btnForgotPassword);
        btnSignIn= (Button) findViewById(R.id.btnSignIn);
        btnSignUp= (Button) findViewById(R.id.btnSignUp);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
    }

    private void addEvents() {
        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this,"Login sucess",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this,"Login cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this,"Login failed",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
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

            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

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
        if (pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog.cancel();
        }
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data =  args[0].toString();

                    if(data == "true"){
                        Intent intent = new Intent(MainActivity.this,
                                UserActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "Email or password is wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
}
