package com.example.bianhaifang;

import static com.example.bianhaifang.ConnectThread.XIANGJIMULURCV;
import static com.example.bianhaifang.ConnectThread.XIANGJIPAIZHAOCTL;
import static com.example.bianhaifang.ConnectThread.XIANGJIPARAM;
import static com.example.bianhaifang.ConnectThread.XIANGJISHIPINGCTL;
import static com.example.bianhaifang.ConnectThread.XIANGJISHIPINGRCV;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEVICE_CONNECTING = 1;//有设备正在连接热点
    public static final int DEVICE_CONNECTED  = 2;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE  = 3;//发送消息成功
    public static final int SEND_MSG_ERROR    = 4;//发送消息失败
    public static final int GET_MSG           = 6;//获取新消息

    public static final int XIANGJIMULU           = 300;//获取新消息
    public static final int XIANGJISHIPING        = 302;


    public static String data_name;

    public TextView text_state;
    public ConnectThread connectThread;
    public ListenThread listenerThread;
    public RcvThread rcvThread;
    public InputStream inputStream;
    public OutputStream outputStream;

    public Frame frame = new Frame();

    public String Debug = "MainActivity";

    int flag = 1;
    /**
     * 热点名称
     */
    public static final String WIFI_HOTSPOT_SSID = "TEST";
    public static final int    PORT              = 54321;
    private static final int    SERVEPORT         = 54321;
    public WifiManager wifiManager;

    public TextView status_init;
    public TextView status_text;
    List<String> videolistshow;

    public Button button_canshu;
    public Button button_shoujimulu;
    public Button button_xiangjimulu;
    public Button button_shuaxinliulan;

    public Button button_viode_control;
    public Button button_photo_control;

    Socket socket;
    VideoView video;//获取vedio组件

    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private BroadReceive mybroadreceive;
    private LocalBroadcastManager localBroadcastManager;
    //然后通过一个函数来申请
    public static void verifyStoragePermissions(MainActivity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*获取页面参数*/
        setContentView(R.layout.activity_main);
        video = findViewById(R.id.vedio);
        button_canshu = findViewById(R.id.canshusheding);
        button_shoujimulu = findViewById(R.id.shoujimulu);
        button_xiangjimulu = findViewById(R.id.xiangjimulu);
        button_shuaxinliulan = findViewById(R.id.shuaxinliulan);
        button_viode_control = findViewById(R.id.yaokongluxiang);
        button_photo_control = findViewById(R.id.yaokongpaizhao);

        button_canshu.setOnClickListener(l);
        button_shoujimulu.setOnClickListener(l);
        button_xiangjimulu.setOnClickListener(l);
        button_shuaxinliulan.setOnClickListener(l);
        button_viode_control.setOnClickListener(l);
        button_photo_control.setOnClickListener(l);

        videolistshow = new ArrayList<>();

        Frame frame = new Frame();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //检查Wifi状态
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        text_state = (TextView) findViewById(R.id.text_state);
        status_init = (TextView) findViewById(R.id.status_init);
        status_text = findViewById(R.id.status_text);
  //      status_init.setText("已连接到：" + wifiManager.getConnectionInfo().getSSID() +"\n路由：" + getWifiRouteIPAddress(MainActivity.this));
 //               开启连接线程
        verifyStoragePermissions(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.rfstar.action.NORMAL_BROADCAST");
        mybroadreceive = new BroadReceive();
        registerReceiver(mybroadreceive, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Log.e("MainAcitivity", "socket create");
//                        socket = new Socket("172.20.10.7", PORT);//我的wifi
                        socket = new Socket("192.168.8.180", PORT);//安工wifi
                    } catch (IOException e) {
                        Log.e("MainAcitivity", "socket got problem = " + e + "------");
                    }
                }while(socket == null);

                while (!(socket.isClosed() == false && socket.isConnected() == true)) {

                    try {
                        Log.e("socket connect", "try connect---------");
                        //socket.connect(serveraddr);
                        /*这里用来改ip*/
//                        socket = new Socket("172.20.10.7", PORT); //我的wifi
                            socket = new Socket("192.168.8.180", PORT); //安工wifi
                    } catch (IOException e) {
                        try {
                            socket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        e.printStackTrace();
                        Log.e("socket connect", "connect time out 10s");
                    }
                    //Thread.sleep(5000);
                }

                listenerThread = new ListenThread(MainActivity.this,SERVEPORT, handler);
                listenerThread.start();



                Log.e("MainActivity", "connect success");
                //获取数据流
                //connectThread = new ConnectThread(MainActivity.this, socket, handler);
                //connectThread.start();
                //rcvThread = new RcvThread(MainActivity.this, socket, handler);
                //rcvThread.start();
                //System.out.println(getWifiRouteIPAddress(MainActivity.this));

            }

        }).start();

        Log.e("dfefef", "fdfefef");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            //String path = data.getStringExtra("path");

            if(data != null)
            {
                String flag = data.getStringExtra("flag");
                String path = data.getData().toString();
                Log.e("测试", "activity接收数据---"+path);
                Log.e("测试", "activity接收标志---"+flag);

                // if(flag.equals("bofang") ){
                //    Log.e("测试", "播放---");
                shipinbofang(path);
            }

        }
    }

    View.OnClickListener l = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.canshusheding:
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);

                    Log.e(Debug, "the param set has been click ---------------");
                   // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent,0);//requestCode=0
                   // startActivity(intent);
                    break;
                case R.id.shuaxinliulan:
                    //测试用的
//                    for(int i = 0; i < videolistshow.size(); i++){
//                        Log.e("xiazailujing", "xiazailujing---");
//                        if(videolistshow.get(i) == "iazailujing"){
//                            String xiazailujing = videolistshow.get(i+1);
//                            Log.e("xiazailujing1", "xiazailujing1---"+xiazailujing);
//                        }
//                    }
                    break;
                case R.id.xiangjimulu:

                    //将线程申明为全局变量 使用第一种发送消息方法
                    //注意：哪个线程给哪个线程发，就调用接收方的handler

                    Handler connect_handler = connectThread.getHandler();

                    /*把这里去掉了,避免点击相机目录的时候又发一次报文*/
//                    if (connect_handler != null)
//                    {
//                        Message message = handler.obtainMessage();
//                        message.what = XIANGJIMULU;
//                        message.obj = "XIANGJIMULU";
//                        connectThread.getHandler().sendMessage(message);
//                    }


                    Intent intent3 = new Intent(MainActivity.this,xjml_activity.class);

                    // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    for(int i = 0; i < videolistshow.size(); i++){
                        intent3.putExtra("videolist"+i,videolistshow.get(i));
//                        Log.e("测试", "视频列表---"+videolistshow.get(i));
//                        Log.e("测试", "视频列表长度---"+i);
                    }
                    intent3.putExtra("videolen",videolistshow.size());
                    intent3.putExtras(intent3);
                    //方式1
                    startActivityForResult(intent3,1);//requestCode=1
                    //方式2
                    // startActivity(intent);
                    break;
                case R.id.shoujimulu:
//                    Intent intent4 = new Intent(MainActivity.this,shipin_M_act.class);
//                   // intent4.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    startActivityForResult(intent4,0);//requestCode=0
//                   // startActivity(intent4);
                    Intent intent4 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent4.setType("video/*");
                    startActivityForResult(intent4, 0);
                    break;

                case R.id.yaokongpaizhao:

                    Log.e(Debug, "the photo control has been click before---------------");

                    Handler connect_handler3 = connectThread.getHandler();

                    Log.e(Debug, "the photo control has been click ---------------");
                    if (connect_handler3 != null)
                    {
                        Message message = handler.obtainMessage();
                        message.what = XIANGJIPAIZHAOCTL;
                        message.obj = frame.PhotoControlFrameAssemble();

                        Log.e(Debug, "photo control frame has been assemble-------------");

                        connectThread.getHandler().sendMessage(message);
                    }
                    break;

                case R.id.yaokongluxiang:

                    Log.e(Debug, "the video control has been click before---------------");

                    Handler connect_handler2 = connectThread.getHandler();

                    Log.e(Debug, "the video control has been click ---------------");
                    if (connect_handler2 != null)
                    {
                        Message message = handler.obtainMessage();
                        message.what = XIANGJISHIPINGCTL;
                        message.obj = frame.VideoControlFrameAssemble();

                        Log.e(Debug, "video control frame has been assemble--------------");

                        connectThread.getHandler().sendMessage(message);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public void shipinbofang(String path){
        //加载要播放的视频
        Log.e("测试", "视频播放---");
        video.setVideoPath(path);//指定要播放视频
        //控制视频播放
        MediaController mc = new MediaController(MainActivity.this);//创建MediaController
        video.setMediaController(mc);//让videoview与MediaController关联
        video.requestFocus();//让videoview获得焦点
        video.start();//播放视频
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "播放完毕", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * wifi获取 已连接网络路由  路由ip地址
     * @param context
     * @return
     */
    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.ipAddress);
        Log.e("route ip", "wifi route ip：" + routeIp);

        return routeIp;
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.e("handler", "handler open");
            switch (msg.what) {
                case DEVICE_CONNECTING:
                    connectThread = new ConnectThread(MainActivity.this,socket, handler);
                    connectThread.start();
                    //服务器端需要开启此监听
                    rcvThread = new RcvThread(MainActivity.this, socket, handler);
                    rcvThread.start();
                    Handler connect_handler = connectThread.getHandler();
                    int flag = 1;
                    if (connect_handler != null && flag == 1)
                    {
                        Message message = handler.obtainMessage();
                        message.what = XIANGJIMULU;
                        /*这里用来发送请求,这里报了空指针的错误，已修复*/
                        message.obj = frame.ReadLogReqFrameAssemble();
                        connect_handler.sendMessage(message);
                        flag = 0;
                    }

                    break;
                case DEVICE_CONNECTED:
                    status_init.setText("设备连接成功");
                    break;
                case SEND_MSG_SUCCSEE:
                    status_init.setText("发送消息成功:" + msg.getData().getString("MSG"));
                    break;
                case SEND_MSG_ERROR:
                    status_init.setText("发送消息失败:" + msg.getData().getString("MSG"));
                    break;
                case GET_MSG:
                    Log.e("FileServerAsnycTask", "FileServerAsnycTask");
                    FileServerAsnycTask f1 = new FileServerAsnycTask(MainActivity.this, status_text,listenerThread);
                    f1.execute();
                    //}
                    break;
                default:
                    break;
            }
        }
    };



    public static class FileServerAsnycTask extends AsyncTask<Void,Void,String> {

        private Context context;
        private TextView statusText;
        private ListenThread listenThread;

        public FileServerAsnycTask(Context context,View statusText,ListenThread thread){
            this.context  = context;
            this.statusText = (TextView)statusText;
            this.listenThread = thread;
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                Log.e("doInBackground", "doInBackground");
                /*
                ServerSocket socket = new ServerSocket(54323);
                if(socket == null)
                {
                    Log.e("doInBackground", "ServerSocket失败");
                }*/
                //Socket client = socket.accept();
                Date nowTime = new Date(System.currentTimeMillis());
                SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String now = sdFormatter.format(nowTime);


                final File f = new File(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"+
                        /*context.getPackageName()+*/"/"+"wifi-"+now+".mp4");
                Log.e("doInBackground", f.getPath());
                File dirs = new File(f.getParent());
                Log.e("doInBackground", f.getParent());
                if(!dirs.exists())
                    dirs.mkdirs();
                if (f.createNewFile())
                {
                    Log.e("create failed", "failed");
                }
                scanPhoto(dirs);
                f.delete();
                //InputStream is = client.getInputStream();
                //ConnectThread.copyFile(is, new FileOutputStream(f));
                //socket.close();
                Log.e("path", f.getAbsolutePath());
                return f.getAbsolutePath();
            }catch(Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                statusText.setText("File copied - "+result);
//                Intent i = new Intent();
//                i.setAction(Intent.ACTION_VIEW);
//                i.setDataAndType(Uri.parse("file//"+result), "video/*");
//                context.startActivity(i);

            }
        }
        private void scanPhoto(File file) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

        }

    }

    public class BroadReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            String name = bundle.getString("name");
            String thread = bundle.getString("thread");
            Log.e("broad received", name);
            Log.e(Debug, "name's content length = " + name.length());

            if (thread.equals("MainActivity2"))
            {
                Handler connect_handler = connectThread.getHandler();

                if (connect_handler != null)
                {
                    Message message = handler.obtainMessage();
                    message.what = XIANGJIPARAM;
                    message.obj = frame.ChangeParamFrameAssemble(name);
                    data_name = new String(name);
                    connect_handler.sendMessage(message);
                }
            }

            if (thread.equals("xjml_activity"))
            {
                /*这个handler的操作，到ConnectThread.java里面去找*/
                Handler connect_handler = connectThread.getHandler();

                if (connect_handler != null)
                {
                    Message message = handler.obtainMessage();
                    message.what = XIANGJISHIPING;
                    message.obj = frame.ReadVideoReqFrameAssemble(name);
                    Log.e(Debug, "Video request has been send --------");
//                    data_name = new String(name);
                    connect_handler.sendMessage(message);
                }
            }


            if (thread.equals("RcvThread"))
            {
                if (name.equals("xiangjishipingrcv"))
                {
                    Handler connect_handler = connectThread.getHandler();

                    if (connect_handler != null)
                    {
                        Message message = handler.obtainMessage();
                        message.what = XIANGJISHIPINGRCV;
                        message.obj = frame.ReadVideoAckFrameAssemble(name);
                        connect_handler.sendMessage(message);
                    }
                }

                /*对应RcvThread里RcvParamAnalysis的buffer[11] = 0x01
                 * 收到读取目录请求的处理*/
                if (name.equals("xiangjimulurcv"))
                {
                    Handler connect_handler = connectThread.getHandler();

                    if (connect_handler != null)
                    {
                        Message message = handler.obtainMessage();
                        message.what = XIANGJIMULURCV;
                        /*自己加的用于组帧的函数*/
                        message.obj = frame.ReadLogACKFrameAssemble();
                        connect_handler.sendMessage(message);
                    }
                }
            }


            /*
            //try {
                OutputStream outputStream = socket.getOutputStream();
                Log.e("rcvthread", "getOutput");
                String ab = "abcdef";
            byte[] sendData = ab.getBytes();

            outputStream.write(sendData);
                Toast.makeText(context, "Download:" + name,Toast.LENGTH_SHORT).show();
            //} catch (IOException e) {
               // e.printStackTrace();
               // Log.e("touch","error");

            //}*/
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(mybroadreceive);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}