package com.linrh.libtcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SimpleServer {

    public static ArrayList<Socket> sockets = new ArrayList<Socket>();
    private static ServerSocket ss = null;
    private static int cnt = 0;

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        ss = new ServerSocket(30000);
        sockets.clear();
        System.out.println(ss.getInetAddress().getHostName());


        while (true) {
            Socket s = ss.accept();
            sockets.add(s);

            cnt++;
            System.out.println("["+cnt+"]"+"the new socket:"+s.getInetAddress()+":"+s.getPort());

            new Thread(new ServerThread(s, cnt)).start();
        }
    }

}
