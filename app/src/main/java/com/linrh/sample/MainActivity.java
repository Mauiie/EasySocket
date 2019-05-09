package com.linrh.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linrh.easysocket.Callback;
import com.linrh.easysocket.EasySocket;


public class MainActivity extends Activity {
    TextView tv;
    Button mButton;
    EditText mEditText;

    EasySocket socket;

    StringBuilder sb = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        mButton = findViewById(R.id.button);
        mEditText = findViewById(R.id.editText);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.send((mEditText.getText().toString() + "\n").getBytes());
            }
        });


        EasySocket.Builder builder = new EasySocket.Builder();
        socket = builder.setIp("10.10.201.52")
                .setPort(30000)
                .setNeedHeart(true)  //默认为false
                .setMaxHeartTime(5000) //默认为5000
                .setCallback(new Callback() {
                    @Override
                    public void onConnected() {

                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onReconnected() {

                    }

                    @Override
                    public void onSend() {

                    }

                    @Override
                    public void onReceived(byte[] msg) {
                        String text = new String(msg);
                        show(text);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                }).build();

        socket.connect();


    }


    @Override
    protected void onStop() {
        socket.disconnect();
        super.onStop();
    }

    private void show(String text) {
        if (sb.length() > 3000) {
            sb.setLength(0);
        }

        sb.insert(0, text);
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(sb.toString());
            }
        });

    }
}
