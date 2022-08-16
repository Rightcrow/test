package com.example.bianhaifang;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

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
import java.util.Date;

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

    public RcvThread(Context context, Socket socket, Handler handler) {
        setName("ConnectThread");
        Log.e("ConnectThread", "ConnectThread");
        this.socket = socket;
        this.handler = handler;
        this.context = context;
        msgFrame = new MsgFrame(context);
    }
    @Override
    public void run() {
/*        if(activeConnect){
//            socket.c
        }*/
        BufferedReader reader = null;
        //获取数据流
           // ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
        Log.e("RcvThread", "run");
        Log.e("rcv thread", "socket first");


        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
                //if (socket.isClosed() == false && socket.isConnected() == true)
                //{
                try {
                    //inputStream = socket.getInputStream();
                    /*
                    OutputStream outputStream = socket.getOutputStream();
                    Log.e("rcvthread", "getOutput");
                    String ab = "abcdef";
                    byte[] sendData = ab.getBytes();

                    outputStream.write(sendData);

                     */
                    //outputStream.close();


                    Log.e("rcvthread", "getInput");
                    byte[] buffer = new byte[1024];

                    int n = 0;
                    //while ((n = inputStream.read(buffer, 0, 1024)) != -1);
                    int size = 0;
                    while ( (size = inputStream.read(buffer, 0, 1024))==-1);
                    //inputStream.reset();
                    Log.e("rcvthreadzie", String.valueOf(size));
                    String a = new String(buffer);
                    String b = null;
                    if (a.length() < size)
                    {
                        b = a;
                    }
                    else {
                        b = a.substring(0, size);
                    }

                    Log.e("rcvthread", b);

                    RcvParamAnalysis(b);

                    /*
                    if (b.equals("abcdef"))
                    {
                        Date nowTime = new Date(System.currentTimeMillis());
                        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                        String now = sdFormatter.format(nowTime);

                        final File f = new File(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"
                                +"test11"+now+".mp4");
                        Log.e("rcv doInBackground", f.getPath());
                        //File dirs = new File(f.getParent());
                        //Log.e("rcv doInBackground", f.getParent());
                        //if(!dirs.exists())
                        //    dirs.mkdirs();
                        if (f.createNewFile())
                        {
                            Log.e("create failed", "failed");
                        }
                    }

                     */



                    //FileRcv("listxx.mp4");
                    /*
                    byte[] buffer = new byte[1024];
                    int bytes;
                    Date nowTime = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                    String now = sdFormatter.format(nowTime);

                    final File f = new File(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"
                            +"listy-"+now+".mp4");
                    Log.e("rcv doInBackground", f.getPath());
                    //File dirs = new File(f.getParent());
                    //Log.e("rcv doInBackground", f.getParent());
                    //if(!dirs.exists())
                    //    dirs.mkdirs();
                    if (f.createNewFile())
                    {
                        Log.e("create failed", "failed");
                    }

                    OutputStream save_file = new FileOutputStream(f);
                    int n;
                    while ((n = inputStream.read(buffer, 0, 1024)) != -1)
                    {
                        save_file.write(buffer, 0 ,n);
                    }

                    Log.e("rcv thread", "recieved");

                    save_file.flush();
                    save_file.close();
                    */









                /*
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String content = "";
                    while ((content = reader.readLine()) != null){
                        //  Log.e("收到消息111:",content);
                        Message message = Message.obtain();
                        message.what = MainActivity.GET_MSG;
                        Bundle bundle = new Bundle();
                        bundle.putString("MSG",  content);
                        message.setData(bundle);
                        handler.sendMessage(message);
                        byte[] bytearray = content.getBytes();
                        msgFrame.SolveParam(bytearray);
                    }
                    reader.close();

                     */



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("rcvrcvrcv","fail");
                }
            }



        //else
                //{
                  //  Log.e("rcvthread", "socket is not connect");
                //}




                //读取数据
//                bytes = inputStream.read(buffer);
//                if (bytes > 0) {
//                   // byte[] rcvmsg = messageBuffer.toByteArray();
//
//                    final byte[] data = new byte[bytes];
//                    System.arraycopy(buffer, 0, data, 0, bytes);
//                    Message message = Message.obtain();
//                    message.what = MainActivity.GET_MSG;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("MSG", new String(data));
//                    message.setData(bundle);
//                    handler.sendMessage(message);
//                }

            //}
    }
    public RcvThread(Socket socket) {
        this.socket = socket;
    }

    public void RcvParamAnalysis(String param )
    {
        if (param.equals("xiangjimulusend"))
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


        if(param.length()>14 && param.length()!=17)
        {
            String head = param.substring(0, 14);
            String data = param.substring(14, param.length());
            Log.e("xiangjishiping_head", head);
            Log.e("xiangjishipingrcv_name", data);
            if (head.equals("xiangjishiping"))
            {
                Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
                Bundle bundle = new Bundle();
                bundle.putString("name", "xiangjishipingrcv");
                bundle.putString("thread", "RcvThread");
                intent.putExtra("data", bundle);
                context.sendBroadcast(intent);

                FileRcv(data);
            }
        }


    }

    public void FileRcv(String filename ) {
        try {

            //inputStream = socket.getInputStream();

            byte[] buffer = new byte[1024];
            Date nowTime = new Date(System.currentTimeMillis());
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String now = sdFormatter.format(nowTime);

            Message messagel = Message.obtain();
            messagel.what = MainActivity.GET_MSG;
            handler.sendMessage(messagel);

            final File f = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM" + "/" + "Camera" + "/"
                    /*context.getPackageName()+*/ + "/" + filename+".mp4");
            Log.e("rcvtread doInBackground", f.getPath());
            //File dirs = new File(f.getParent());
            //Log.e("rcv doInBackground", f.getParent());
            //if(!dirs.exists())
            //    dirs.mkdirs();
            if (f.createNewFile())
            {
                Log.e("rcvthread create failed", "failed");
            }


            OutputStream save_file = new FileOutputStream(f);
            int n;

            //inputStream.wait(0);

            int end_times = 0;

            while ((n = inputStream.read(buffer, 0, 1024)) > 0 || end_times==0)
            {
                for (int i=0; i<n; i++)
                {
                    if (i<= buffer.length-6)
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
                //String p = new String(buffer);
                //Log.e("rcvthread read", p);
            }



            Log.e("rcvthreadd thread", "recieved");

            save_file.flush();
            save_file.close();


            if (!filename.equals("list"))
            {
                Message messagef = Message.obtain();
                messagef.what = MainActivity.GET_MSG;
                handler.sendMessage(messagef);



                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(Environment.getExternalStorageDirectory() + "/" + "DCIM" + "/" + "Camera" + "/"
                        /*context.getPackageName()+*/ + "/" + "VID20220614204246"+".mp4");


                Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
                Bundle bundle = new Bundle();
                bundle.putString("name", f.getPath());
                bundle.putString("thread", "RcvThreadPlay");
                intent.putExtra("data", bundle);
                context.sendBroadcast(intent);
            }

            Log.e("rcvthread", "file is ok");

        }
        catch (IOException e)
        {
            Log.e("rcvthread", "file error");
        }



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
