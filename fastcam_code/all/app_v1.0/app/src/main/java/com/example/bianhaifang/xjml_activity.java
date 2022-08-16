package com.example.bianhaifang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class xjml_activity extends AppCompatActivity {

    private ImageView title_back;
    private LocalBroadcastManager localBroadcastManager;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xjml);

        View account = View.inflate(this, R.layout.activity_xjml, null);
        title_back = (ImageView) account.findViewById(R.id.title_back);
        //findViewById(R.id.title_back).setOnClickListener(l);
        title_back.setOnClickListener(l);

        List<String> s = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                //final File f = new File(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"
                //        /*context.getPackageName()+*/+"lisdt"+".mp4");
                BufferedReader reader = null;
                try {
                    System.out.println("以行为单位读取文件内容，一次读一整行：");
                    reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory()+"/"+"DCIM"+"/"+"Camera"+"/"
                            /*context.getPackageName()+*/+"lisdt"+".mp4"));
                    String tempString = null;
                    int line = 1;
                    // 一次读入一行，直到读入null为文件结束
                    while ((tempString = reader.readLine()) != null) {
                        // 显示行号
                        s.add(tempString);
                        System.out.println("line " + line + ": " + tempString);
                        line++;
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Intent intent = getIntent();
                // int len = intent.getIntExtra("videolen",0);
                /*
                int len = 10;
                for(int i =0;i<len;i++){
                    //String str = intent.getStringExtra("videolist"+i);
                    //s.add(intent.getStringExtra("videolist"+i));
                    s.add("videolist"+i);
                    //Log.e("测试", "videolist---"+intent.getStringExtra("videolist"+i));
                }
                Log.e("测试", "videolen---"+len);

                 */

            }
        }).start();
        List<String> s1 = new ArrayList<>();
        adapter = new ArrayAdapter<String>(xjml_activity.this, android.R.layout.simple_list_item_1, s);
        ListView listView = findViewById(R.id.videolist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("xjml", s.get(i));
                //Intent intent = new Intent(xjml_activity.this,MainActivity.class);
                //把数据捆设置改意图
                //intent.putExtra("flag", "xiazai");

                Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
                Bundle bundle = new Bundle();
                bundle.putString("name", s.get(i));
                bundle.putString("thread", "xjml_activity");
                intent.putExtra("data", bundle);
                sendBroadcast(intent);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                    }
                },1000);
                finish();
               // intent.putExtra("path", s.get(i));
                //setResult(1,intent);//requestCode=1
                //关闭当前activity
                //finish();

            }
        });
    }
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_back:
                    //Intent intent = new Intent(xjml_activity.this,MainActivity.class);
                    //把数据捆设置改意图
                    //intent.putExtra("path", "");
                    //intent.putExtra("flag", "fanhui");
                    //setResult(1,intent);//requestCode=1
                    //finish();
                    break;
                case 11:
                    break;
            }
        }
    };



}