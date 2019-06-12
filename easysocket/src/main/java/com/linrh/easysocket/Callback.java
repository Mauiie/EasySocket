package com.linrh.easysocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 作者：created by @author{ John } on 2019/5/8 0008下午 2:21
 * 描述：
 * 修改备注：
 */

public interface Callback {
    void onConnected();
    void onDisconnected();
    void onReconnected();
    void onSend();
    void onReceived(byte[] msg);
    void onError(String msg);
    void onSendHeart();
}


