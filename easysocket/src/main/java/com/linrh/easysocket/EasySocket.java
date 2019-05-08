package com.linrh.easysocket;

import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 作者：created by @author{ John } on 2019/5/8 0008下午 2:22
 * 描述：
 * 修改备注：
 */

public class EasySocket {

    private Builder mBuilder;

    private String ip;
    private int port;
    private Callback callback;


    private Socket mSocket;
    private InputStream is = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;
    private OutputStream os = null;

    public Boolean isConnected = false;
    private Thread mThread;
    private byte[] buffer = new byte[1024];

    private String TAG = "SocketConnect";

    private Thread watchThread = null;

    private Boolean isAutoConnect = true;


    private long lasttime = 0;

    private long maxHeartTime = 5000;
    private Boolean needHeart = false;




    public EasySocket(Builder builder) {
        this.mBuilder = builder;
        this.ip = builder.ip;
        this.port = builder.port;
        this.callback = builder.callback;


    }



    public void connect() {
        disconnectSocketIfNecessary();

        if (Thread.currentThread()==Looper.getMainLooper().getThread()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    realconnect();
                }


            }).start();
        }else {
            realconnect();
        }

        //连接了socket之后，才创建监听进程。
        openWatchThread();

    }

    private void realconnect() {
        try {

            mSocket = new Socket(ip,port);

            Boolean isConnect = mSocket.isConnected();

            if (isConnect) {

                is = mSocket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                os = mSocket.getOutputStream();

                isConnected = true;
                callback.onConnected();
                Log.e(TAG, "onConnected");

                //创建监听线程
                openThread();



            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e(TAG, "onError");
        }


    }


    private void disconnectSocketIfNecessary() {
        try {
            if (mSocket!=null) {
                isConnected = false;
                closeThread();

                if (!mSocket.isClosed()) {
                    if(!mSocket.isInputShutdown()){
                        mSocket.shutdownInput();
                    }
                    if (!mSocket.isOutputShutdown()) {
                        mSocket.shutdownOutput();
                    }

                    if (br!=null) {
                        br.close();
                        br=null;
                    }
                    if (isr!=null) {
                        isr.close();
                        isr=null;
                    }
                    if (is!=null) {
                        is.close();
                        is=null;
                    }
                    if (os!=null) {
                        os.close();
                        os=null;
                    }

                    mSocket.close();
                }
                mSocket = null;

                callback.onDisconnected();
                Log.e(TAG, "onDisconnected");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            callback.onError("断开连接异常");
            Log.e(TAG, "onError");
        }
    }


    public void disconnect(){
        disconnectSocketIfNecessary();
        closeWatchThread();
    }

    private void closeThread()
    {
        if (mThread!=null) {
            isConnected = false;
            mThread.interrupt();
            mThread = null;
            Log.e(TAG, "close thread");
        }
    }

    private void closeWatchThread()
    {
        if (watchThread!=null) {
            isAutoConnect = false;
            watchThread.interrupt();
            watchThread = null;
            Log.e(TAG, "close watchThread");
        }
    }

    private void openThread()
    {
        closeThread();
        mThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isConnected) {
                    try {
                        int readLen=0;

                        readLen = is.read(buffer);
                        if (readLen>0) {
                            byte[] data = new byte[readLen];
                            System.arraycopy(buffer, 0, data, 0, readLen);

                            callback.onReceived(data);
                            Log.e(TAG, "onReceived"+":"+new String(data));


                            lasttime = System.currentTimeMillis();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        callback.onError("读取数据异常");
                        Log.e(TAG, "onError");
                    }

                }
            }
        });
        mThread.start();
    }

    private void openWatchThread()
    {
        //closeWatchThread();
        if (watchThread!=null) {
            return;
        }
        watchThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isAutoConnect) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {
                        //加入超时判断
//                        if (needHeart) {
//                            if((System.currentTimeMillis() - lasttime) > maxHeartTime){
//                                isConnected = false;
//                            }
//                        }

                        //sendHeart(0xff);

                        if (isConnected) {

                        }else {
                            //未连接的情况下，重新连接服务器
                            Log.e(TAG, "onReconnect");
                            callback.onReconnected();
                            disconnectSocketIfNecessary();
                            realconnect();
                        }



                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        callback.onError("读取数据异常");
                        Log.e(TAG, "onError");
                    }

                }
            }
        });
        watchThread.start();
    }


    /**
     * 发送命令
     * @param msg  信息
     */
    public void send(final byte[] msg)
    {

        if (Thread.currentThread()==Looper.getMainLooper().getThread()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    realsend(msg);
                }
            }).start();
        }else {
            realsend(msg);
        }

    }

    private void realsend(byte[] msg) {

        try {
            os.write(msg);
            os.flush();

            callback.onSend();
            Log.e(TAG, "onSend");

//            if (!needHeart) {
//
//                lasttime = System.currentTimeMillis();
//            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            callback.onError("发送失败");
            Log.e(TAG, "onError");
        }
    }


    /**
     * 发送心跳包
     * @param i
     */
    private void sendHeart(int i)
    {
        try {
            os.write(i);
            os.flush();

            if (mSocket.isInputShutdown()||mSocket.isOutputShutdown()) {
                isConnected = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "sendHeart fail");
            isConnected = false;
        }
    }


    /**
     * 配置构造器
     */
    public static class Builder{

        private String ip;
        private int port;
        private Callback callback;

        private long maxHeartTime = 5000;
        private Boolean needHeart = false;

        public String getIp() {
            return ip;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public int getPort() {
            return port;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Callback getCallback() {
            return callback;
        }

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public long getMaxHeartTime() {
            return maxHeartTime;
        }

        public Builder setMaxHeartTime(long maxHeartTime) {
            this.maxHeartTime = maxHeartTime;
            return this;
        }

        public Boolean getNeedHeart() {
            return needHeart;
        }

        public Builder setNeedHeart(Boolean needHeart) {
            this.needHeart = needHeart;
            return this;
        }

        public EasySocket build()
        {

            if (this.ip == null) {
                throw new IllegalStateException("ip == null");
            } else if (this.port == 0) {
                throw new IllegalStateException("port == 0");
            } else {
                return new EasySocket(this);
            }
        }
    }






}
