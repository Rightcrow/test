package com.example.bianhaifang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telecom.Connection;
import android.text.TextUtils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class shipin_M_act extends DefaultBaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private Map<String, List<Video>> AllList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RelativeLayout actionbar;
    private ImageView img_album_arrow;
    private ImageView title_back;
    private TextView select_video;
    private List<MyFile> myFileList = new ArrayList<>();

    private  final String DEBUG_TAG = "Maintian Param";

    @Override
    protected void initialize() {
        Log.e(DEBUG_TAG, "initialize");
        setContentView(R.layout.activity_shipin_mact);
    }

    @Override
    protected void initView() {
        Log.e(DEBUG_TAG, "initView");
        actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.Gray, R.color.Gray, R.color.Gray, R.color.Gray);
        startRefreshing(swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        title_back = (ImageView) findViewById(R.id.title_back);
        img_album_arrow = (ImageView) findViewById(R.id.img_album_arrow);
        select_video = (TextView) findViewById(R.id.select_video);

        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.select_video).setOnClickListener(this);
       // initMyFile(Environment.getExternalStorageDirectory());
    }

    private void initMyFile(File externalStorageDirectory) {
        myFileList.clear();
        List<File> files = new ArrayList<>();
        Collections.addAll(files, externalStorageDirectory.listFiles());
        for (File file : files) {
            String fileName = file.getName();
            MyFile myFile = new MyFile(fileName);
            myFileList.add(myFile);
        }
       // adapter.notifyDataSetChanged();//刷新列表
    }

    @Override
    protected void initData() {
        Log.e(DEBUG_TAG, "initData");
        super.initData();
        new initVideosThread().start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                Intent intent = new Intent(shipin_M_act.this,MainActivity.class);
                //把数据捆设置改意图
                intent.putExtra("path", "");
                intent.putExtra("flag", "guanbishipin");
                setResult(1,intent);//requestCode=1
                finish();
                break;
            case R.id.select_video:
                if (bottomListDialog != null) {
                    Log.e(DEBUG_TAG, "bottomListDialog != null");
                    bottomListDialog.show();
                    img_album_arrow.setSelected(true);
                }else{
                    Log.e(DEBUG_TAG, "bottomListDialog = null");
                }
                break;

        }
    }
    private BottomListDialog bottomListDialog;
    private Adapter adapter;
    private OnItemClickListener clickListener;
    private GestureDetectorCompat gestureDetector;
    String path = "";


     Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstant.WHAT.SUCCESS:
                    Log.e(DEBUG_TAG, "SUCCESS");
                    stopRefreshing(swipeRefreshLayout);
                    adapter = new Adapter(R.layout.adapter_select_video_item, (List<Video>) msg.obj);
                    mRecyclerView.setAdapter(adapter);
                    List<Video> list_path = (List<Video>) msg.obj;
                    mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(shipin_M_act.this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            List<String> templist = new ArrayList<>();
                            for (Video video : list_path) {

                                String video_path = video.getPath();
                                templist.add(video_path);

                               // Log.e(DEBUG_TAG, "video_path--"+video_path);
                            }
                            Log.e(DEBUG_TAG, "RecyclerItemClickListener---"+view.getId()+"/"+position);
                            Log.e(DEBUG_TAG, "RecyclerItemClickListener---video_path--"+templist.get(position));
                            path = templist.get(position).toString();

                            Intent intent = new Intent(shipin_M_act.this,MainActivity.class);
                            //把数据捆设置改意图
                            intent.putExtra("flag", "bofang");
                            intent.putExtra("path", path);
                            setResult(1,intent);//requestCode=1
                            //关闭当前activity
                            finish();
                        }

                        @Override
                        public void onLongClick(View view, int posotion) {

                        }
                    }));


                    final SelectVideoActivity_BottomListDialogAdapter bottomListDialogAdapter = new SelectVideoActivity_BottomListDialogAdapter(activity, AllList);

                    bottomListDialog = new BottomListDialog.Builder(activity
                            , bottomListDialogAdapter,
                            MyApplication.getInstance().getScreenHeight() - actionbar.getHeight() - StatusBarHeightUtil.getStatusBarHeight(context)
                    ).setOnItemClickListener(new BottomListDialog.OnItemClickListener(){
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            Log.e(DEBUG_TAG, "setOnItemClickListener");
                            dialog.dismiss();
                            String album = (String) bottomListDialogAdapter.getAllList().keySet().toArray()[which];
                            adapter.setNewInstance(bottomListDialogAdapter.getAllList().get(album));
                            select_video.setText(album);
                            img_album_arrow.setSelected(false);
                        }
                    }).create();

                    break;
//
                case AppConstant.WHAT.FAILURE:
                    Log.e(DEBUG_TAG, "FAILURE");
                    stopRefreshing(swipeRefreshLayout);
                    break;
            }
        }
    };



    class initVideosThread extends Thread {
        @Override
        public void run() {
            super.run();
            AbstructProvider provider = new VedioProvider(activity);

            List<Video> list = (List<Video>) provider.getList();
           // Log.e(DEBUG_TAG, "list---"+list);
            Log.e(DEBUG_TAG, "initVideosThread");
            List<Video> templist = new ArrayList<>();
            AllList = new HashMap<>();

            //我需要可以查看所有视频 所以加了这样一个文件夹名称
           // AllList.put(" " + getResources().getString(R.string.all_video), list);
            AllList.put(" " + getResources().getString(R.string.all_video), list);

            //主要是读取文件夹的名称 做分文件夹的展示

            for (Video video : list) {
                String album = video.getAlbum();
                if (TextUtils.isEmpty(album)) {
                    album = "Camera";
                }

                if (AllList.containsKey(album)) {
                    AllList.get(album).add(video);
                } else {
                    templist = new ArrayList<>();
                    templist.add(video);
                    AllList.put(album, templist);
                }
               // Log.e(DEBUG_TAG, "album--"+album);

            }
           // Log.e(DEBUG_TAG, "AllList--"+AllList);
            //在子线程读取好数据后使用handler 更新
            if (list == null || list.size() == 0) {
                Message message = new Message();
                message.what = AppConstant.WHAT.FAILURE;
                mHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.what = AppConstant.WHAT.SUCCESS;
                message.obj = list;
                mHandler.sendMessage(message);
            }
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    protected void startRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }
    protected void stopRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public class Adapter extends BaseQuickAdapter<Video,BaseViewHolder> {

        public Adapter(int layoutResId, List<Video> data) {
            super(layoutResId, data);
            Log.e(DEBUG_TAG, "Adapter");
        }


        @Override
        protected void convert(BaseViewHolder helper, Video item) {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(item.getDuration()),
                    TimeUnit.MILLISECONDS.toMinutes(item.getDuration()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(item.getDuration())),
                    TimeUnit.MILLISECONDS.toSeconds(item.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.getDuration())));

            helper.setText(R.id.text_duration, hms);
            ImageView simpleDraweeView1 = null;
            //simpleDraweeView1 = AdapterUtils.getAdapterView(helper.getView(R.id.simpleDraweeView), R.id.simpleDraweeView);
            simpleDraweeView1 = helper.getView(R.id.simpleDraweeView);
            Log.e(DEBUG_TAG, "simpleDraweeView1"+simpleDraweeView1);
            int width = (MyApplication.getInstance().getScreenWidth() - 4) / 4;
            Log.e(DEBUG_TAG, "fffffffffffokkkkkkxxxxxxxxxxxxxxxxx"+width);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
            simpleDraweeView1.setLayoutParams(layoutParams);

            Glide
                    .with(context)
                    .load(Uri.fromFile(new File(item.getPath())))
                    .asBitmap()
                    .into(simpleDraweeView1);
        }

    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        GestureDetector mGestureDetector;
        private View childView;
        private RecyclerView touchView;

        public RecyclerItemClickListener(Context context, final RecyclerItemClickListener.OnItemClickListener mListener) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onItemClick(childView, touchView.getChildAdapterPosition(childView));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onLongClick(childView, touchView.getChildAdapterPosition(childView));
                    }
                }
            });
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onLongClick(View view, int posotion);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            childView = rv.findChildViewUnder(e.getX(), e.getY());
            touchView = rv;
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}