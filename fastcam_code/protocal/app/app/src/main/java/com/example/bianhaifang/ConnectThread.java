package com.example.bianhaifang;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Base64;

/**
 * 连接线程
 * Created by syhuang on 2018/4/3
 */
public class ConnectThread extends Thread {

    public static final int XIANGJIMULU           = 300;//获取新消息
    public static final int XIANGJIMULURCV        = 301;
    public static final int XIANGJISHIPING        = 302;
    public static final int XIANGJISHIPINGRCV     = 303;
    public static final int XIANGJISHIPINGCTL        = 304;
    public static final int XIANGJIPARAM        = 305;
    public static final int XIANGJIPAIZHAOCTL        = 306;
    //private LocalBroadcastManager localBroadcastManager;
    private final Socket       socket;
    public       Handler      handler;
    public       Handler      sendhandler;
    public       InputStream  inputStream;
    public        OutputStream outputStream;
    MainActivity ctx;
    Context context;
 //   MainActivity mainActivity = new MainActivity();

    public ConnectThread(Context context, Socket socket, Handler handler) {
        setName("ConnectThread");
        Log.e("ConnectThread", "ConnectThread");
        this.socket = socket;
        this.handler = handler;
        this.sendhandler = handler;
        this.context = context;
    }
    public Handler getHandler(){

        return handler;
    }

    @Override
    public void run() {
/*        if(activeConnect){
//            socket.c
        }*/
        Log.e("ConnectThreadrun", "ConnectThreadrun");
        //handler.sendEmptyMessage(MainActivity.DEVICE_CONNECTED);

        Looper.prepare();

        handler = new Handler(Looper.myLooper())
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleMessage(Message msg) {
                Log.e("handleMessage", "handleMessage");
                //获取数据流
                //if (socket.isClosed() == false && socket.isConnected() == true)
                //{
                    try {

                        outputStream = socket.getOutputStream();

                        switch (msg.what)
                        {
                            case XIANGJIMULU:
                                Log.e("XIANGJIMULU", new String((byte[]) msg.obj));
                                //sendData((String) msg.obj);
                                /*必须用这种形式转码，不然传输的时候，会产生乱码*/
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJIMULURCV:
                                Log.e("xiangjimulurcv", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJISHIPING:
                                Log.e("xiangjishipin", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJISHIPINGRCV:
                                Log.e("xiangjishipinrcv", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJIPARAM:
                                Log.e("xiangjiparam", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJISHIPINGCTL:
                                Log.e("xiangjishipingctl", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;
                            case XIANGJIPAIZHAOCTL:
                                Log.e("xiangjipaizhaoctl", new String((byte[]) msg.obj));
                                sendData(Base64.getEncoder().encodeToString((byte[]) msg.obj));
                                break;


                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //}

            }
        };
        //启动Looper
        Looper.loop();
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                Log.e("len", "len：" + len);
            }
            out.close();
            //            inputStream.close();
        } catch (IOException e) {

            return false;
        }
        return true;
    }

    /**
     * 发送数据
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendData(String msg) {
        Log.i("ConnectThread", "发送数据:" + (outputStream == null));
        if (outputStream != null)
        {
            try
            {
                outputStream.flush();
                /*必须用这种形式转码，不然传输的时候，会产生乱码*/
                outputStream.write(Base64.getDecoder().decode(msg));
                Log.e("ConnectThread", "发送消息：" + Arrays.toString(msg.getBytes()));
                Message message = Message.obtain();
                message.what = MainActivity.SEND_MSG_SUCCSEE;
                Bundle bundle = new Bundle();
                bundle.putString("MSG", new String(msg));
                message.setData(bundle);
                sendhandler.sendMessage(message);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = MainActivity.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG", new String(msg));
                message.setData(bundle);
                sendhandler.sendMessage(message);
            }
        }
    }


}
