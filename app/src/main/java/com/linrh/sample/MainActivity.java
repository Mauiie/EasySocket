package com.linrh.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linrh.easysocket.Callback;
import com.linrh.easysocket.EasySocket;

import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    EasySocket socket;
    StringBuilder sb = new StringBuilder();

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.cb_needheart)
    CheckBox cbNeedheart;
    @BindView(R.id.et_heartpackage)
    EditText etHeartpackage;
    @BindView(R.id.et_sendhearttime)
    EditText etSendhearttime;
    @BindView(R.id.et_recsevertimeout)
    EditText etRecsevertimeout;
    @BindView(R.id.tv_rec)
    TextView tvRec;
    @BindView(R.id.et_send)
    EditText etSend;
    @BindView(R.id.btn_send)
    Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        changeState();
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

        tvRec.post(new Runnable() {
            @Override
            public void run() {
                tvRec.setText(sb.toString());
            }
        });

    }

    @OnClick({R.id.btn_connect, R.id.cb_needheart, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connectSever(etIp.getText().toString(),
                        Integer.parseInt(etPort.getText().toString()),
                        cbNeedheart.isChecked(),
                        hexStr2Byte(etHeartpackage.getText().toString().trim()),
                        Integer.parseInt(etSendhearttime.getText().toString()),
                        Integer.parseInt(etRecsevertimeout.getText().toString()));
                break;
            case R.id.cb_needheart:
                changeState();
                break;
            case R.id.btn_send:
                //socket.send((etSend.getText().toString() + "\n").getBytes());
                socket.send((etSend.getText().toString()).getBytes());

                break;
        }
    }

    private void changeState()
    {
        if (cbNeedheart.isChecked()){
            etHeartpackage.setEnabled(true);
            etSendhearttime.setEnabled(true);
            etRecsevertimeout.setEnabled(true);
        }else{
            etHeartpackage.setEnabled(false);
            etSendhearttime.setEnabled(false);
            etRecsevertimeout.setEnabled(false);
        }
    }

    private void connectSever(String ip, int port, Boolean needHeart, byte[] heartpackage, int heartInterval, int maxSeverResponseHeartOutTime)
    {
        EasySocket.Builder builder = new EasySocket.Builder();
        socket = builder.setIp(ip)
                .setPort(port)
                /**
                 * 默认不开启心跳。
                 */
                .setNeedHeart(needHeart)
                /**
                 * 默认心跳包为0xA.
                 */
                .setHeartPackage(heartpackage)
                /**
                 * 客户端发送心跳包间隔默认10秒
                 */
                .setHeartInterval(heartInterval)
                /**
                 * 客户端等待服务器回应心跳包默认5秒，
                 * 此数值需要比心跳发送间隔小。
                 */
                .setMaxSeverResponseHeartOutTime(maxSeverResponseHeartOutTime)
                .setCallback(new Callback() {
                    @Override
                    public void onConnected() {
                        btnConnect.setEnabled(false);
                    }

                    @Override
                    public void onDisconnected() {
                        btnConnect.setEnabled(true);
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
                    public void onError(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSendHeart() {

                    }
                }).build();

        socket.connect();
    }


    public static byte[] hexStr2Byte(String hex) {
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }

}
