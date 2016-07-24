package car.ccut.com.vehicle.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.PoiInfos;
import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;
import car.ccut.com.vehicle.listener.MyOrientationListener;
import car.ccut.com.vehicle.service.MusicService;
import car.ccut.com.vehicle.service.VoiceRecognition;

/**
 * Created by panzhuowen on 2016/3/12.
 */
public class MapActivity extends BaseActivity{

    @Bind(R.id.bmapView)
    MapView mapView;
    @Bind(R.id.search)
    AutoCompleteTextView search;
    @Bind(R.id.mac)
    ImageView macButton;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private boolean isFristIn =true;
    private List<RefuelStationInfo> stationInfos;
    private BitmapDescriptor mMarker;
    private LinearLayout navi;
    private LinearLayout carManage;
    private LinearLayout orderRefuel;
    private LinearLayout more;
    private Intent it;
    private boolean macControl=true;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    private PoiResult mPoiResult;
    private boolean searchText;
    private  String [] searchResult;
    private ArrayAdapter<String> searchAdapter;
    private String type;
    private MusicService musicService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            musicService = ((MusicService.LocalBinder) service).getService();//用绑定方法启动service，就是从这里绑定并得到service，然后就可以操作service了
            musicService.setContext(getApplicationContext());
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    List<PoiInfos> poiInfos = new ArrayList<PoiInfos>();
    //每页容量
    private static final int PAGE_CAPACITY=50;
    //第一页
    private static final int PAGE_NUM = 0;
    //搜索半径10km
    private static final int RADIUS = 10000;
    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**
     * 当前的精度
     */
    private float mCurrentAccracy;
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    public void initData(){
        stationInfos = (List<RefuelStationInfo>) getIntent().getSerializableExtra("stationInfos");
        type = getIntent().getStringExtra("type");
    }
    @Override
    public void initView() {
        setTitle("当前位置");
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setTrafficEnabled(true);
        //设置地图放缩比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        macControl=true;
        // 初始化传感器
        initOritationListener();
        mPoiSearch = PoiSearch.newInstance();
        poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(final PoiResult poiResult) {
                hideWaitDialog();
                if (poiResult==null||poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD){
                    Toast.makeText(MapActivity.this,"在附近未找到相关信息",Toast.LENGTH_SHORT).show();
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR){
                    mPoiResult=poiResult;
                    if (searchText){
                        if (searchResult==null){
                            searchResult = new String[poiResult.getAllPoi().size()];
                        }
                        for (int i = 0;i<poiResult.getAllPoi().size();i++){
                            searchResult[i]=poiResult.getAllPoi().get(i).name;
                        }
                        searchAdapter = new ArrayAdapter<String>(MapActivity.this,android.R.layout.simple_list_item_1,searchResult){
                            private Filter f;
                            public Filter getF() {
                                if(f ==null){
                                    f = new Filter() {
                                        @Override
                                        protected FilterResults performFiltering(CharSequence charSequence) {
                                            ArrayList<Object> suggestions = new ArrayList<Object>();
                                            for(String adr:searchResult){
                                                suggestions.add(adr);
                                            }
                                            FilterResults filterResults = new FilterResults();
                                            filterResults.values = suggestions;
                                            filterResults.count=suggestions.size();
                                            return filterResults;
                                        }

                                        @Override
                                        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                                            if (filterResults.count > 0) {
                                                searchAdapter.notifyDataSetChanged();
                                            } else {
                                                searchAdapter.notifyDataSetInvalidated();
                                            }
                                        }
                                    };
                                }
                                return f;
                            }
                        };
                        search.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        searchAdapter.notifyDataSetInvalidated();
                    }else {
                        poiInfos.clear();
                        for (int i=0;i<poiResult.getAllPoi().size();i++){
                            PoiInfos poiInfo= new PoiInfos();
                            poiInfo.setName(poiResult.getAllPoi().get(i).name);
                            poiInfo.setAddress(poiResult.getAllPoi().get(i).address);
                            poiInfo.setPhoneNum(poiResult.getAllPoi().get(i).phoneNum);
                            poiInfo.setCity(poiResult.getAllPoi().get(i).city);
                            poiInfo.setPostCode(poiResult.getAllPoi().get(i).postCode);
                            poiInfo.setLatitude(poiResult.getAllPoi().get(i).location.latitude);
                            poiInfo.setLontitude(poiResult.getAllPoi().get(i).location.longitude);
                            poiInfos.add(poiInfo);
                        }
                        Intent intent = new Intent(MapActivity.this,SearchInfosActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("poiInfos", (Serializable) poiInfos);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(search.getText().toString());
                searchText=true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                poiInfos.clear();
                try {
                    PoiInfos poiInfo= new PoiInfos();
                    poiInfo.setName(mPoiResult.getAllPoi().get(i).name);
                    poiInfo.setAddress(mPoiResult.getAllPoi().get(i).address);
                    poiInfo.setPhoneNum(mPoiResult.getAllPoi().get(i).phoneNum);
                    poiInfo.setCity(mPoiResult.getAllPoi().get(i).city);
                    poiInfo.setPostCode(mPoiResult.getAllPoi().get(i).postCode);
                    poiInfo.setLatitude(mPoiResult.getAllPoi().get(i).location.latitude);
                    poiInfo.setLontitude(mPoiResult.getAllPoi().get(i).location.longitude);
                    poiInfos.add(poiInfo);
                    Intent intent = new Intent(MapActivity.this,SearchInfosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("poiInfos", (Serializable) poiInfos);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (type!=null&&type.equals("type")){
            showDialog();
        }
    }

    private void  searchInfo(String searchInfo){
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(searchInfo)
                .location(new LatLng(mCurrentLantitude,mCurrentLongitude))
                .pageCapacity(PAGE_CAPACITY)
                .pageNum(PAGE_NUM)
                .radius(RADIUS));
    }
    /**
     * 初始化方向传感器
     */
    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
                {
                    @Override
                    public void onOrientationChanged(float x)
                    {
                        mXDirection = (int) x;

                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        // 设置自定义图标
                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.mipmap.navi_map_gps_locked);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                MyLocationConfiguration.LocationMode.COMPASS, true, mCurrentMarker);
                        mBaiduMap.setMyLocationConfigeration(config);
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(mapView!=null){
            mapView.onDestroy();
        }
        if (locationClient.isStarted()){
            locationClient.stop();
            locationClient=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()){
            locationClient.start();
        }
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        myOrientationListener.stop();
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.mac})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                Intent it = new Intent(this,HomeActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.mac:
                mac();
                break;
        }
    }

    public void mac(){
        Intent intent = new Intent(this,VoiceRecognition.class);
        musicService.pausePlay();
        if (macControl){
            startService(intent);
            System.out.println("service已启动");
            macButton.setImageResource(R.mipmap.mac_shut_icon);
        }else {
            macButton.setImageResource(R.mipmap.mac_start_icon);
            stopService(intent);
        }
        macControl=!macControl;
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation==null||mapView==null){
                return;
            }
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .direction(mXDirection).latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude()).//
                    build();
            mBaiduMap.setMyLocationData(data);
            mCurrentLantitude = bdLocation.getLatitude();
            mCurrentLongitude = bdLocation.getLongitude();
            mCurrentAccracy = bdLocation.getRadius();
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.mipmap.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.COMPASS, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            if (isFristIn){
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFristIn = false;
            }
            if (stationInfos!=null){
                addOverlays(stationInfos);
                markerOnclick();
            }else {
//                showDialog();
            }
        }
    }

    //添加覆盖物
    private void addOverlays(List<RefuelStationInfo> stationInfo){
        mBaiduMap.clear();
        OverlayOptions options;
        for (RefuelStationInfo refuelStationInfo : stationInfo){
            LatLng latLng = new LatLng(Double.valueOf(refuelStationInfo.getLat()),Double.valueOf(refuelStationInfo.getLon()));
            mMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),R.mipmap.gas_station_icon));
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            Marker marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stationInfo",refuelStationInfo);
            marker.setExtraInfo(bundle);
        }
    }

    //覆盖物点击事件
    private void markerOnclick(){
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final RefuelStationInfo stationInfo = (RefuelStationInfo) marker.getExtraInfo().getSerializable("stationInfo");
                Toast.makeText(MapActivity.this,stationInfo.getAddress(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void showDialog(){
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MapActivity.this).create();
        dialog.show();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.home_simple_menu,null);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = 1000;
        navi = (LinearLayout) view.findViewById(R.id.navi);
        navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dialog.dismiss();
                    macButton.setVisibility(View.VISIBLE);
                it = new Intent(MapActivity.this,DrivingActivity.class);
                startActivity(it);
            }
        });
        carManage = (LinearLayout) view.findViewById(R.id.car_manage);
        carManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(MapActivity.this,HomeActivity.class);
                startActivity(it);
            }
        });
        orderRefuel = (LinearLayout) view.findViewById(R.id.order_refuel);
        orderRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        more = (LinearLayout) view.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                it = new Intent(MapActivity.this,HomeActivity.class);
//                startActivity(it);
//                AppManager.getAppManager().finishActivity(MapActivity.this);

            }
        });
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setContentView(view,params);
    }
}
