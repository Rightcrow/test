package com.example.bianhaifang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity2 extends AppCompatActivity {
    private Spinner moshi;
    private Spinner tuxiang;
    private Spinner luxiang;
    private Spinner paizhaozhangshu;
    private Spinner luxiangshijian;
    private Spinner jiangeshijian;
    private Spinner hongwailingmindu;
    private Spinner shijianchuo;
    private TextView textView_moshi;
    private TextView textView_tuxiang;
    private TextView textView_luxiang;
    private TextView textView_paizhaozhangshu;
    private TextView textView_luxiangshijian;
    private TextView textView_jiangeshijian;
    private TextView textView_hongwailingmindu;
    private TextView textView_shijianchuo;
    private ImageButton back_btn;
    private Button yulan_btn;
    private Button shezhi_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        moshi = findViewById(R.id.moshi_spinner);
        tuxiang = findViewById(R.id.tuxiang_spinner);
        luxiang = findViewById(R.id.luxiang_spinner);
        paizhaozhangshu = findViewById(R.id.zhangshu_spinner);
        luxiangshijian = findViewById(R.id.shijian_spinner);
        jiangeshijian = findViewById(R.id.jiange_spinner);
        hongwailingmindu = findViewById(R.id.hongwai_spinner);
        shijianchuo = findViewById(R.id.shijianchuo_spinner);

        textView_moshi = findViewById(R.id.moshi_text);
        textView_tuxiang = findViewById(R.id.tuxiang_text);
        textView_luxiang = findViewById(R.id.luxiang_text);
        textView_paizhaozhangshu = findViewById(R.id.zhangshu_text);
        textView_luxiangshijian = findViewById(R.id.shijian_text);
        textView_jiangeshijian = findViewById(R.id.jiange_text);
        textView_hongwailingmindu = findViewById(R.id.hongwai_text);
        textView_shijianchuo = findViewById(R.id.shijianchuo_text);

        back_btn = findViewById(R.id.back);
        yulan_btn = findViewById(R.id.yulan);
        shezhi_btn = findViewById(R.id.shezhi);



        moshi.setSelection(0, false);  // must
        moshi.setSelection(0,true);  //must
        tuxiang.setSelection(0, false);  // must
        tuxiang.setSelection(0,true);  //must
        luxiang.setSelection(0, false);  // must
        luxiang.setSelection(0,true);  //must
        paizhaozhangshu.setSelection(0, false);  // must
        paizhaozhangshu.setSelection(0,true);  //must
        luxiangshijian.setSelection(0, false);  // must
        luxiangshijian.setSelection(0,true);  //must
        jiangeshijian.setSelection(0, false);  // must
        jiangeshijian.setSelection(0,true);  //must
        hongwailingmindu.setSelection(0, false);  // must
        hongwailingmindu.setSelection(0,true);  //must
        shijianchuo.setSelection(0, false);  // must
        shijianchuo.setSelection(0,true);  //must


        moshi.setOnItemSelectedListener(l);
        tuxiang.setOnItemSelectedListener(l);
        luxiang.setOnItemSelectedListener(l);
        paizhaozhangshu.setOnItemSelectedListener(l);
        luxiangshijian.setOnItemSelectedListener(l);
        jiangeshijian.setOnItemSelectedListener(l);
        hongwailingmindu.setOnItemSelectedListener(l);
        shijianchuo.setOnItemSelectedListener(l);
        back_btn.setOnClickListener(l_back);


    }
    //模式下拉菜单选择监听
    AdapterView.OnItemSelectedListener l = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String content = adapterView.getItemAtPosition(i).toString();

            Intent intent = new Intent("com.rfstar.action.NORMAL_BROADCAST");
            Bundle bundle = new Bundle();
            bundle.putString("thread", "MainActivity2");

            String value;

            switch (adapterView.getId()){
                case R.id.moshi_spinner:
                    //textView_moshi.setText(content);
                    if (content.equals("拍照+录像"))
                    {
                        value = "1";
                    }
                    else if (content.equals("拍照"))
                    {
                        value = "2";
                    }
                    else if (content.equals("录像"))
                    {
                        value = "3";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "me" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);

                    Toast.makeText(MainActivity2.this, "模式参数已设置:"+content, Toast.LENGTH_LONG).show();
                    break;
                case R.id.tuxiang_spinner:
                    /*
                    if (content.equals("1MP"))
                    {
                        value = "1";
                    }
                    else if (content.equals("2MP"))
                    {
                        value = "2";
                    }
                    else if (content.equals("3MP"))
                    {
                        value = "3";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "ps" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                     */
                    Toast.makeText(MainActivity2.this, "图像尺寸已设置:"+content, Toast.LENGTH_LONG).show();

                    break;
                case R.id.luxiang_spinner:
                    /*
                    if (content.equals("1MP"))
                    {
                        value = "1";
                    }
                    else if (content.equals("2MP"))
                    {
                        value = "2";
                    }
                    else if (content.equals("3MP"))
                    {
                        value = "3";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "ps" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);

                     */
                    Toast.makeText(MainActivity2.this, "录像尺寸已设置:"+content, Toast.LENGTH_LONG).show();

                    break;
                case R.id.zhangshu_spinner:
                    if (content.equals("1张"))
                    {
                        value = "1";
                    }
                    else if (content.equals("2张"))
                    {
                        value = "2";
                    }
                    else if (content.equals("3张"))
                    {
                        value = "3";
                    }
                    else if (content.equals("4张"))
                    {
                        value = "4";
                    }
                    else if (content.equals("5张"))
                    {
                        value = "5";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "ps" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity2.this, "拍摄张数已设置:"+content, Toast.LENGTH_LONG).show();
                   // textView_paizhaozhangshu.setText(content);
                    break;
                case R.id.shijian_spinner:
                    if (content.equals("1MP"))
                    {
                        value = "1";
                    }
                    else if (content.equals("2MP"))
                    {
                        value = "2";
                    }
                    else if (content.equals("3MP"))
                    {
                        value = "3";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "vs" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity2.this, "录像时间已设置:"+content, Toast.LENGTH_LONG).show();
                   // textView_luxiangshijian.setText(content);
                    break;
                case R.id.jiange_spinner:
                    /*
                    if (content.equals("1MP"))
                    {
                        value = "1";
                    }
                    else if (content.equals("2MP"))
                    {
                        value = "2";
                    }
                    else if (content.equals("3MP"))
                    {
                        value = "3";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "ps" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);

                     */
                    Toast.makeText(MainActivity2.this, "间隔时间已设置:"+content, Toast.LENGTH_LONG).show();
                   // textView_jiangeshijian.setText(content);
                    break;
                case R.id.hongwai_spinner:
                    if (content.equals("低"))
                    {
                        value = "1";
                    }
                    else if (content.equals("高"))
                    {
                        value = "2";
                    }
                    else
                    {
                        return;
                    }

                    bundle.putString("name", "sy" + value);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity2.this, "灵敏度已设置:"+content, Toast.LENGTH_LONG).show();
                   // textView_hongwailingmindu.setText(content);
                    break;
                case R.id.shijianchuo_spinner:
                   // textView_shijianchuo.setText(content);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    View.OnClickListener l_back = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(MainActivity2.this,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
            Intent intent = new Intent(MainActivity2.this,MainActivity.class);
            //把数据捆设置改意图
            intent.putExtra("path", "");
            intent.putExtra("flag", "guanbishezhi");
            //setResult(1,intent);//requestCode=1
            //关闭当前activity
            finish();
        }
    };






}