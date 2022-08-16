package com.example.bianhaifang;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class RcvThread extends Thread{
    private LocalBroadcastManager localBroadcastManager;
    public static final int XIANGJIMULURCV        = 301;
    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;
    Context context;
    MsgFrame msgFrame;
    private static final byte DELIMITER = '\n';
    //   MainActivity mainActivity = new MainActivity();

    private String DebugTag = "RcvThread";


    /*这个函数， 把我接受的数组大小限制死了，有问题，不能这么写*/
//    private int frame_total_length = 13;

    private int frame_max_length = 1024;

    public RcvThread(Context context, Socket socket, Handler handler) {
        setName("ConnectThread");
        Log.e("ConnectThread", "ConnectThread");
        this.socket = socket;
        this.handler = handler;
        this.context = context;
        msgFrame = new MsgFrame(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run()
    {
        BufferedReader reader = null;
        Log.e("RcvThread", "run");
        Log.e("rcv thread", "socket first");

        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true)
        {
            try
            {

                byte[] buffer = new byte[frame_max_length];

                int n = 0;
                int size = 0;

                /*读取传进来的信息*/
                while ( (size = inputStream.read(buffer, 0, frame_max_length))==-1);

                String frame = Base64.getEncoder().encodeToString(buffer);

                RcvParamAnalysis(frame);

//                /*这里是把bffer的内容用来给a赋初值*/
//                String a = new String(buffer);
//                String b = null;
//                if (a.length() < size)
//                {
//                    b = a;
//                }
//                else
//                {
//                    b = a.substring(0, size);
//                }
//
//                Log.e("rcvthread", b);


//                RcvParamAnalysis(b);

                }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.e("rcvrcvrcv","fail");
            }
        }
    }


    public RcvThread(Socket socket) {
        this.socket = socket;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RcvParamAnalysis(String param)
    {
//        byte[] buffer = param.getBytes();

        byte[] buffer = Base64.getDecoder().decode(param);

//        Log.e(DebugTag, "buffer[11] = " + buffer[11] + "----------");
//
//        Log.e(DebugTag, "buffer[12] = " + buffer[12] + "----------");

//        Log.e(DebugTag, "buffer[13] = " + buffer[13] + "----------");


//        if (param.equals("xiangjimulusend"))
        /*这里是相机目录的操作*/
        if (buffer[11] == 0x02)
        {
            Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
            Bundle bundle = new Bundle();
            bundle.putString("name", "xiangjimulurcv");
            bundle.putString("thread", "RcvThread");
            intent.putExtra("data", bundle);
            context.sendBroadcast(intent);

            Log.e("rcvthread", "xiangjimulusend in");

            FileRcv("list");
        }

        /*视频接受的流程*/
        if(buffer[11] == 0x05)
        {
            Log.e(DebugTag, "video response has been recv, the recv buffer[11] = " + buffer[11] + "------");


            /*这是从字符串里直接截取的文件名字*/
//            String data = param.substring(12, 40);

            /*上下都用27，能够得到正常的名字*/
            byte[] buffer_name = new byte[27];

            System.arraycopy(buffer, 12, buffer_name, 0, 27);

//            Log.e(DebugTag, "buffer_name = " + buffer_name.toString() + "----------");

            String data = byte2HexString(buffer_name);

            Log.e(DebugTag, "video response has been recv, the hex string " + data + "------");

            data = hexStr2Str(data);

            Log.e("xiangjishipingrcv_name", data);

            Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
            Bundle bundle = new Bundle();
            bundle.putString("name", "xiangjishipingrcv");
            bundle.putString("thread", "RcvThread");
            intent.putExtra("data", bundle);
            context.sendBroadcast(intent);

            FileRcv(data);

        }

        if (buffer[11] == 0x0F)
        {
            Log.e(DebugTag, "param ack frame has recevied------------");
        }


    }

    public void FileRcv(String filename ) {
        try
        {

            //inputStream = socket.getInputStream();

            byte[] buffer = new byte[1024];
            Date nowTime = new Date(System.currentTimeMillis());
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String now = sdFormatter.format(nowTime);

            Message messagel = Message.obtain();
            messagel.what = MainActivity.GET_MSG;
            handler.sendMessage(messagel);


            Log.e("filename = ", filename);
            final File f = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM" + "/" + "Camera" + "/"
                    + "/" + filename+".mp4");
//            Log.e("rcvtread doInBackground", f.getPath());

            Log.e("rcvthread", "file name = " + filename + "----------");

//            Log.e("rcvthread", "ready to create new file -------");
            if (f.createNewFile())
            {
                Log.e("rcvthread create failed", "failed");
            }
            Log.e("rcvthread", "after create filename = " + filename + "----------");
//            Log.e("rcvthread", "create new file done -------");

//            Log.e("rcvtread", "ready to get file outputStream------");
            OutputStream save_file = new FileOutputStream(f);
            int n;

            //inputStream.wait(0);

            int end_times = 0;

            Log.e("rcvtread doInBackground", "ready to save message------");
            while (((n = inputStream.read(buffer, 0, 1024)) > 0) || end_times==0)
            {
//                Log.e("rcvtread doInBackground", "n = " + n + "--------");

                for (int i=0; i<n; i++)
                {
                    if (i <= buffer.length-6)
                    {
                        if (buffer[i]=='a' && buffer[i+1]=='f' && buffer[i+2]=='z' )
                        {
                            Log.e("rcvthread", "break");
                            end_times = 1;
                            break;
                        }
                    }
                }

                if (end_times == 1)
                {
                    break;
                }
                save_file.write(buffer, 0 ,n);
            }

//            Log.e("rcvthreadd thread", "recieved");

            save_file.flush();
            save_file.close();

            Log.e("rcvthread", "file is ok");

        }
        catch (IOException e)
        {
            Log.e("rcvthread", "recv message got problem: " + e + "--------");
            Log.e("rcvthread", "file error");

        }



    }

    public static String byte2HexString(byte[] b)
    {
        String r = "";

        for(int i = 0; i < b.length; ++i)
        {
            String hex = Integer.toHexString(b[i] & 0xff);

            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }

            r += hex.toUpperCase();
        }

        return r;
    }



    public String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";

        char[] hexs = hexStr.toCharArray();

        int length = (hexStr.length() / 2);

        byte[] bytes = new byte[length];

        int n;

        for (int i = 0; i < bytes.length; ++i)
        {
            int position = i * 2;

            n = str.indexOf(hexs[position]) * 16;

            n += str.indexOf(hexs[position + 1]);

            bytes[i] = (byte)(n & 0xff);
        }

        return new String(bytes);
    }


    public class FileServerAsnycTask extends AsyncTask<Void,Void,String> {

        private Context context;
        private TextView statusText;
        private ListenThread listenThread;

        public FileServerAsnycTask(){
            /*
            this.context  = context;
            this.statusText = (TextView)statusText;
            this.listenThread = thread;

             */
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                byte[] buffer = new byte[1024];
                Log.e("RcvThread", "doInBackground");
                //ServerSocket socket = new ServerSocket(54323);
                //if(socket == null)
                {
                    Log.e("doInBackground", "ServerSocket失败");
                }
                //Socket client = socket.accept();
                Date nowTime = new Date(System.currentTimeMillis());
                SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String now = sdFormatter.format(nowTime);


                final File f = new File(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"+
                        /*context.getPackageName()+*/"/"+"listrazzy.mp4");
                Log.e("doInBackground", f.getPath());
                File dirs = new File(f.getParent());
                Log.e("doInBackground", f.getParent());
                if(!dirs.exists())
                    dirs.mkdirs();
                if (f.createNewFile())
                {
                    Log.e("create failed", "failed");
                }
                OutputStream save_file = new FileOutputStream(f);
                int n;
                inputStream.reset();
                while ((n = inputStream.read(buffer, 0, 1024)) != -1)
                {
                    save_file.write(buffer, 0 ,n);
                }

                Log.e("rcv thread", "recieved");

                save_file.flush();
                save_file.close();
                scanPhoto(dirs);
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
                //statusText.setText("File copied - "+result);
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
            //context.sendBroadcast(mediaScanIntent);

        }

    }

}
