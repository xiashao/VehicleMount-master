package car.ccut.com.vehicle;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import car.ccut.com.vehicle.bean.Mp3;
import car.ccut.com.vehicle.bean.User;
import car.ccut.com.vehicle.bean.car.CarInfo;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.service.MusicService;
import car.ccut.com.vehicle.util.MusicUtils;

/**
 * *
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 * Created by WangXin on 2016/2/18 0018.
 */
public class MyApplication extends Application {
    public static RequestQueue queues;
    public static CarInfo currentServerCar;
    public static User currentUser;
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.image_default)
            .showImageOnFail(R.mipmap.image_default)
            .showImageForEmptyUri(R.mipmap.image_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();
    @Override
    public void onCreate() {
        super.onCreate();
        if (getCurProcessName(getApplicationContext()).equals("car.ccut.com.vehicle")){
            queues = Volley.newRequestQueue(getApplicationContext());
            File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(),"vehicle/Cache");
            DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build(); // 创建配置过得DisplayImageOption对象

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    // default = device screen dimensions 内存缓存文件的最大长宽
                    .memoryCacheExtraOptions(320, 480)
                    //线程池内加载的数量
                    .threadPoolSize(5)
                    // default 设置当前线程的优先级
                    //降低线程的优先级保证主UI线程不受太大影响
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    //可以通过自己的内存缓存实现
                    //建议内存设在5-10M,可以有比较好的表现
                    .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                    // 内存缓存的最大值
                    .memoryCacheSize(5 * 1024 * 1024)
                    //100 Mb sd卡(本地)缓存的最大值
                    .diskCacheSize(200 * 1024 * 1024)
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .defaultDisplayImageOptions(options)
                    .writeDebugLogs() //正常发版后这句话删除，作用是打出日志
                    .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                    .build();
            ImageLoader.getInstance().init(configuration);
            StringBuffer param = new StringBuffer();
            param.append(ConstantValue.VOICE_RECOGNITION);
            param.append(",");
            // 设置使用v5+
            param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
            SpeechUtility.createUtility(getApplicationContext(), param.toString());
            SDKInitializer.initialize(getApplicationContext());
            currentServerCar = new CarInfo();
            currentUser = new User();
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    MusicService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MusicService.LocalBinder) service).getService();//用绑定方法启动service，就是从这里绑定并得到service，然后就可以操作service了
            mService.setContext(getApplicationContext());
            List<Mp3> songs = MusicUtils.getAllSongs(getApplicationContext());
            if (songs!=null){
                mService.setSongs(songs);
                mService.playMusic(songs.get(0).getUrl());
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    public MusicService getmService() {
        return mService;
    }

    public void setmService(MusicService mService) {
        this.mService = mService;
    }

    public static RequestQueue getHttpQueues(){
        return queues;
    }

    public static DisplayImageOptions getOptions() {
        return options;
    }

    public static CarInfo getCurrentServerCar(){
        return currentServerCar;
    }


    public static void setCurrentServerCar(CarInfo info){
        currentServerCar = info;
    }

    public static User getCurrentUser() {
        return currentUser;
    }


    public static void setCurrentUser(User currentUser) {
        MyApplication.currentUser = currentUser;
    }


   public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
