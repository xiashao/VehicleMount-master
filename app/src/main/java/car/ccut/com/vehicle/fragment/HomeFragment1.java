package car.ccut.com.vehicle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.thinkpage.lib.api.TPCity;
import com.thinkpage.lib.api.TPListeners;
import com.thinkpage.lib.api.TPWeatherManager;
import com.thinkpage.lib.api.TPWeatherNow;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.ui.CarsManageActivity;
import car.ccut.com.vehicle.ui.MusicMainActivity;
import car.ccut.com.vehicle.ui.NearbyFacilitesActivity;
import car.ccut.com.vehicle.ui.NearbyRefuelStationActivity;
import car.ccut.com.vehicle.ui.OtherCarQueryActivity;
import car.ccut.com.vehicle.util.CharacterParser;

//import car.ccut.com.vehicle.ui.MusicManageActivity;

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
 * Created by WangXin on 2016/5/11 0011.
 */
public class HomeFragment1 extends Fragment implements View.OnClickListener {
    private Intent it;
    private TPWeatherManager weatherManager;
    private MyLocationListener mLocationListener;
    private LocationClient mLocationClient;
    private Handler timeHandler;

    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.week)
    TextView week;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.place)
    TextView place;
    @Bind(R.id.weather_icon)
    ImageView weatherIcon;
    @Bind(R.id.temperature)
    TextView temperature;
    @Bind(R.id.weather_text)
    TextView weatherText;

    @Override
    public void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }


    private void initView() {
        new timeThread().start();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        date.setText(String.valueOf(c.get(Calendar.MONTH) + 1)+"月"+String.valueOf(c.get(Calendar.DAY_OF_MONTH))+"日");
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        week.setText("星期"+mWay);
    }

    private void initData() {
        mLocationClient = new LocationClient(getActivity());
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        long systemTime = System.currentTimeMillis();
                        CharSequence sysTime = DateFormat.format("hh:mm", systemTime);
                        try {
                            time.setText(sysTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment1,container,false);
        ButterKnife.bind(this, view);;
        initView();
        return view;
    }

    @Override
    @OnClick({R.id.near,R.id.order_refuel,R.id.car_manage,R.id.music_manage,R.id.violation})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.near:
                it = new Intent(getActivity(), NearbyFacilitesActivity.class);
                break;
            case R.id.order_refuel:
                it = new Intent(getActivity(), NearbyRefuelStationActivity.class);
                break;
            case R.id.car_manage:
                it = new Intent(getActivity(), CarsManageActivity.class);
                break;
            case R.id.music_manage:
                it = new Intent(getActivity(), MusicMainActivity.class);
                break;
            case R.id.violation:
                it = new Intent(getActivity(),OtherCarQueryActivity.class);
                break;
        }
        if (it !=null){
            startActivity(it);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String city = bdLocation.getCity();
            try {
                place.setText(city);
                String cityPin = city.substring(0,city.indexOf("市"));
                weatherInfo(CharacterParser.getInstance().getSelling(cityPin));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void weatherInfo(String cityId){
        weatherManager = TPWeatherManager.sharedWeatherManager();
        weatherManager.initWithKeyAndUserId(ConstantValue.WEATHER_KEY,ConstantValue.WEATHER_ID);
        weatherManager.getWeatherNow(new TPCity(cityId)
                , TPWeatherManager.TPWeatherReportLanguage.kSimplifiedChinese
                , TPWeatherManager.TPTemperatureUnit.kCelsius
                , new TPListeners.TPWeatherNowListener() {
                    @Override
                    public void onTPWeatherNowAvailable(TPWeatherNow tpWeatherNow, String errorInfo) {
                        if (tpWeatherNow!=null){
                            temperature.setText(tpWeatherNow.temperature+"℃ ");
                            String weather = tpWeatherNow.text;
                            weatherText.setText(weather);
                            if (weather.equals("晴")){
                                weatherIcon.setImageResource(R.mipmap.weather1);
                            }else if (weather.equals("多云")){
                                weatherIcon.setImageResource(R.mipmap.weather2);
                            }else if (weather.equals("阴")){
                                weatherIcon.setImageResource(R.mipmap.weather3);
                            }else if (weather.equals("阵雨")){
                                weatherIcon.setImageResource(R.mipmap.weather4);
                            }else if (weather.equals("雷阵雨")){
                                weatherIcon.setImageResource(R.mipmap.weather5);
                            }else if (weather.equals("小雨")){
                                weatherIcon.setImageResource(R.mipmap.weather6);
                            }else if (weather.equals("中雨")){
                                weatherIcon.setImageResource(R.mipmap.weather7);
                            }else if (weather.equals("大雨")){
                                weatherIcon.setImageResource(R.mipmap.weather8);
                            }else if (weather.equals("小雪")){
                                weatherIcon.setImageResource(R.mipmap.weather9);
                            }else if (weather.equals("中雪")){
                                weatherIcon.setImageResource(R.mipmap.weather10);
                            }else if (weather.equals("大雪")){
                                weatherIcon.setImageResource(R.mipmap.weather11);
                            }else if (weather.equals("霾")){
                                weatherIcon.setImageResource(R.mipmap.weather12);
                            }
                        }else {
                            System.out.println(errorInfo);
                        }
                    }
                });
    }

    class timeThread extends Thread{
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    timeHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
}
