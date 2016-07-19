package car.ccut.com.vehicle.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import car.ccut.com.vehicle.bean.Mp3;


public class MusicService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private Context context;
	/* MediaPlayer对象 */
	private MediaPlayer  mMediaPlayer = null;
	private int currentTime = 0;//歌曲播放进度
	private int currentListItme = -1;//当前播放第几首歌
	private List<Mp3> songs;//要播放的歌曲集合

	@Override
	public void onCreate() {
		super.onCreate();
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
	}

	/**
	 *得到当前播放进度
	 */
	public int getCurrent() {
		if (mMediaPlayer.isPlaying()) {
			return mMediaPlayer.getCurrentPosition();
		} else {
			return currentTime;
		}
	}

	/**
	 *	跳到输入的进度
	 */
	public void movePlay(int progress) {
		mMediaPlayer.seekTo(progress);
		currentTime = progress;
	}

	/**
	 *	根据歌曲存储路径播放歌曲
	 */
	public void playMusic(String path) {
		try {
			/* 重置MediaPlayer */
			mMediaPlayer.reset();
			/* 设置要播放的文件的路径 */

			mMediaPlayer.setDataSource(path);
			// mMediaPlayer = MediaPlayer.create(this,
			// R.drawable.bbb);播放资源文件中的歌曲
			/* 准备播放 */
			mMediaPlayer.prepare();
			/* 开始播放 */
			mMediaPlayer.start();

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer arg0) {
					// 播放完成一首之后进行下一首
					nextMusic();
				}
			});
		} catch (IOException e) {
		}
	}

	/* 下一首 */
	public void nextMusic() {
		if (++currentListItme >= songs.size()) {
			currentListItme = 0;
		}
		playMusic(songs.get(currentListItme).getUrl());
	}

	/* 上一首 */
	public void frontMusic() {
		Log.v("itme", currentListItme + "hree");
		if (--currentListItme < 0) {
			currentListItme = songs.size() - 1;
		}
		playMusic(songs.get(currentListItme).getUrl());
	}

	/**
	 *	歌曲是否真在播放
	 */
	public boolean isPlay() {
		return mMediaPlayer.isPlaying();
	}

	/**
	 *	暂停或开始播放歌曲
	 */
	public void pausePlay() {
		if (mMediaPlayer.isPlaying()) {
			currentTime = mMediaPlayer.getCurrentPosition();
			mMediaPlayer.pause();
		} else {
			mMediaPlayer.start();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public String getSongName(){
		return songs.get(currentListItme).getName();
	}

	public String getSingerName(){
		return songs.get(currentListItme).getSingerName();
	}

	/**
	 *	自定义绑定Service类，通过这里的getService得到Service，之后就可调用Service这里的方法了
	 */
	public class LocalBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public MediaPlayer getmMediaPlayer() {
		return mMediaPlayer;
	}

	public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
		this.mMediaPlayer = mMediaPlayer;
	}

	public int getCurrentListItme() {
		return currentListItme;
	}

	public void setCurrentListItme(int currentListItme) {
		this.currentListItme = currentListItme;
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public List<Mp3> getSongs() {
		return songs;
	}

	public void setSongs(List<Mp3> songs) {
		this.songs = songs;
	}

	// 兼容2.0以前版本
	@Override
	public void onStart(Intent intent, int startId) {
	}

	// 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("Service", "Received start id " + startId + ": " + intent);
		 // 如果服务进程在它启动后(从onStartCommand()返回后)被kill掉, 那么让他呆在启动状态但不取传给它的intent.
		// 随后系统会重写创建service，因为在启动时，会在创建新的service时保证运行onStartCommand
		// 如果没有任何开始指令发送给service，那将得到null的intent，因此必须检查它.
		// 该方式可用在开始和在运行中任意时刻停止的情况，例如一个service执行音乐后台的重放

		return START_STICKY;
	}

	@Override
	public void onDestroy() {

	}
}
