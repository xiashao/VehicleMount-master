package car.ccut.com.vehicle.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.ListViewAdapter;
import car.ccut.com.vehicle.bean.Mp3;
import car.ccut.com.vehicle.service.MusicService;
import car.ccut.com.vehicle.util.MusicUtils;

@SuppressLint("NewApi")
public class MusicMainActivity extends Activity {

    private ListView listView;
    private List<Mp3> songs;// 得到全部歌曲
    private ListViewAdapter listViewAdapter;
    private List<Map<String, Object>> listItems;// 传进适配器的数据
    private ArrayList<String> songIds;// 全部歌曲的id
    private final int SETADAPTER = 111;
    private Timer timer;
    private TimerTask myTimerTask;
    private MusicService mService;
    private MyApplication application;
    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case SETADAPTER:
                    setAdapter();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addsong2playlist);
        setTitle("音乐列表");
        application = (MyApplication) getApplication();
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initView();
        initListener();
    }

    private void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final int positionInt = position;
                mService = application.getmService();
                mService.setCurrentListItme(position);
                mService.playMusic(songs.get(position).getUrl());
                Intent it = new Intent(MusicMainActivity.this, MusicPlayActivity.class);
                startActivity(it);
            }
        });
    }
    public void setTitle (String title) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 以下是定时器0.1秒后再跳到handler加载适配器
        timer = new Timer();
        myTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = SETADAPTER;
                handler.sendMessage(message);
            }
        };
        timer.schedule(myTimerTask, 100);
    }
    public void initView() {
        listView = (ListView) findViewById(R.id.listView);
    }
    public void setAdapter() {

        listItems = getListItems();
        listViewAdapter = new ListViewAdapter(this, listItems, R.layout.pl_songs_add); // 创建适配器
        listViewAdapter.setSongIds(songIds);
        listView.setAdapter(listViewAdapter);
    }

    /**
     * 返回适配器所要加载的数据集合
     */
    private List<Map<String, Object>> getListItems() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        songs = MusicUtils.getAllSongs(this);
        songIds = new ArrayList<String>();
        for (int i = 0; i < songs.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            songIds.add(songs.get(i).getSqlId() + "");
            map.put("songName", songs.get(i).getName()); // 歌曲名
            if (songs.get(i).getSingerName().equals("<unknown>")) {
                map.put("singerName", "----");
            } else {
                map.put("singerName", songs.get(i).getSingerName()); // 歌手名
            }
            listItems.add(map);
        }
        return listItems;
    }

}
