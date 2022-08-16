package com.example.bianhaifang;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监听线程
 * Created by syh on 2018/4/3
 */
public class ListenThread extends Thread {

    public ServerSocket serverSocket = null;
    private Handler handler;
    private int     port;
    private Socket  socket;
    Context context;

    public ListenThread(Context context, int port, Handler handler) {
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        this.context = context;
        /*
        try {
            serverSocket = new ServerSocket(port);//监听本机的12345端口
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }


    @Override
    public void run() {
        /*
        while (true) {
            try {
                Log.e("ListennerThread", "阻塞");
                //阻塞，等待设备连接
                if (serverSocket != null)
                    socket = serverSocket.accept();
                System.out.println("success rcv");
                Message message = Message.obtain();
                message.what = MainActivity.DEVICE_CONNECTING;
                handler.sendMessage(message);

            } catch (IOException e) {
                Log.e("ListennerThread", "error:" + e.getMessage());
                e.printStackTrace();
            }
        }

         */
        Message message = Message.obtain();
        message.what = MainActivity.DEVICE_CONNECTING;
        handler.sendMessage(message);

        Message messagel = Message.obtain();
        messagel.what = MainActivity.GET_MSG;
        handler.sendMessage(messagel);
    }

    public Socket getSocket() {
        return socket;
    }
}