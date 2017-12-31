package com.example.remembergroup.chat_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.remembergroup.model.Me;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by Khai Lee on 12/30/2017.
 */

public class SplashActivity extends Activity {
    private final String SERVER_RE_LOGIN = "SERVER_RE_LOGIN";
    private final String CLIENT_REQUEST_DATA = "CLIENT_REQUEST_DATA";
    private final String CLIENT_LOGIN = "CLIENT_LOGIN";

    private Socket mSocket;
    private SharedPreferences pref;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initSocket();

        handleFlowActivity();
    }

    private void initSocket() {
        mSocket = SingletonSocket.getInstance().mSocket;
        mSocket.on(SERVER_RE_LOGIN, onLogin);
        SingletonSocket.getInstance().ListeningToGetData();

        //mSocket.connect();
        if (! mSocket.connected()) {
            mSocket.connect();
        }
    }

    private void handleFlowActivity() {
        pref = getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);
        mSocket.emit(CLIENT_LOGIN, pref.getString("email", ""), pref.getString("password", ""));
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data =  args[0].toString();

                    if(data == "true"){
                        mSocket.emit(CLIENT_REQUEST_DATA, "please send to me my data");

                        Intent intent = new Intent(SplashActivity.this,
                                MainActivity.class);

                        while(true){
                            if(!Me.getInstance().getEmail().equals("")){
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }
                    }else{
                        Intent intent = new Intent(SplashActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    };
}
