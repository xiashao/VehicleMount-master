package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.Refuel.NearbyRefStaInfo;
import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.MyComparator;
import car.ccut.com.vehicle.util.Utils;

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
 * Created by WangXin on 2016/3/29 0029.
 */
public class NearbyRefuelStationActivity extends BaseActivity {
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.discount_sort)
    LinearLayout discountSort;
    @Bind(R.id.distance_sort)
    LinearLayout distanceSort;
    @Bind(R.id.price_sort)
    LinearLayout priceSort;

    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    protected QuickAdapter<RefuelStationInfo>adapter;
    private final int DICOUNT = 0;
    private final int DISTANCE = 1;
    private final int PRICE =2;
    private List<RefuelStationInfo> stationInfos = new ArrayList<RefuelStationInfo>();
    private Intent it;
    int refuelTypeSize;
    private ArrayList<String> keys = new ArrayList<String>();

    @Override
    public void initView() {
        setTitle("附近加油站");
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setBackgroundResource(R.drawable.location_button_bg);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.actvity_nearby_refuel_station;
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void initData() {
        refuelTypeSize = MyApplication.getCurrentServerCar().getRefuelType().size();
        initLocation();
        showWaitDialog("正在加载,请稍后...");
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.discount_sort,R.id.distance_sort,R.id.price_sort,R.id.tv_add})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.discount_sort:
                if (stationInfos!=null&&stationInfos.size()>2){
                    sort(DICOUNT,null);
                    discountSort.setBackgroundResource(R.color.app_theme_bg);
                    distanceSort.setBackgroundResource(R.color.sort_normal);
                    priceSort.setBackgroundResource(R.color.sort_normal);
                }
                break;
            case R.id.distance_sort:
                if (stationInfos!=null&&stationInfos.size()>2){
                    sort(DISTANCE,null);
                    discountSort.setBackgroundResource(R.color.sort_normal);
                    distanceSort.setBackgroundResource(R.color.app_theme_bg);
                    priceSort.setBackgroundResource(R.color.sort_normal);
                }
                break;
            case R.id.price_sort:
                if (stationInfos!=null&&stationInfos.size()>2){
                    sort(PRICE,MyApplication.getCurrentServerCar().getRefuelType().get(0).getRefuelType());
                    discountSort.setBackgroundResource(R.color.sort_normal);
                    distanceSort.setBackgroundResource(R.color.sort_normal);
                    priceSort.setBackgroundResource(R.color.app_theme_bg);
                }
                break;
            case R.id.tv_add:
                Intent intent= new Intent(this,MapActivity.class);
                intent.putExtra("stationInfos",(Serializable) stationInfos);
                startActivity(intent);
                break;
        }
    }

    private void sort(int flag,String refuelType){
        if (refuelType==null||refuelType.equals("")){
            Collections.sort(stationInfos,new MyComparator(flag));
        }else {
            Collections.sort(stationInfos,new MyComparator(flag,refuelType));
        }
        adapter.replaceAll(stationInfos);
        listView.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("getNearRefInfRequest");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            requestData(bdLocation.getLatitude(),bdLocation.getLongitude());
        }
    }

    private void requestData(double lantitude,double longitude){
        Map params = new HashMap();
        params.put("lon",longitude+"");
        params.put("lat",lantitude+"");
        params.put("r",5000+"");
        params.put("key", ConstantValue.REFUEL_KEY);
        JsonRequestWithAuth<NearbyRefStaInfo> getNearRefInfRequest = new JsonRequestWithAuth<NearbyRefStaInfo>(ConstantValue.REDUEL_STATION_URL,
                NearbyRefStaInfo.class,new Response.Listener<NearbyRefStaInfo>() {
                    @Override
                    public void onResponse(NearbyRefStaInfo response) {
                        stationInfos.clear();
                        if (response.getResult()!=null){
                            System.out.println(response.getResult().getData().size());
                            for (int i=0;i<response.getResult().getData().size();i++){
                               for (int j=0;j<refuelTypeSize;j++){
                                   String key = MyApplication.getCurrentServerCar().getRefuelType().get(j).getRefuelType();
                                   if (response.getResult().getData().get(i).getPrice().get(key)!=null){
                                       stationInfos.add(response.getResult().getData().get(i));
                                       break;
                                   }
                               }
                            }
                            if (stationInfos.size()>0){
                                setListViewData(stationInfos);
                            }
                        }
                    }
                },params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        if (Utils.hasNetwork(NearbyRefuelStationActivity.this)){
            MyApplication.getHttpQueues().add(getNearRefInfRequest);
            MyApplication.getHttpQueues().start();
        }else {
            Toast.makeText(this,"网络故障,请检查网络",Toast.LENGTH_SHORT).show();
        }
    }

    public void setListViewData(final List<RefuelStationInfo> infos){
        if (adapter==null){
            listView.setAdapter(adapter = new QuickAdapter<RefuelStationInfo>(NearbyRefuelStationActivity.this,R.layout.nearby_refuel_item,
                    infos) {
                @Override
                protected void convert(BaseAdapterHelper helper, final RefuelStationInfo item) {
                    keys.clear();
                    helper.setText(R.id.station_name,item.getName());
                    helper.setText(R.id.distance,item.getDistance()+"m");
                    if (refuelTypeSize==1){
                        String key1 = MyApplication.getCurrentServerCar().getRefuelType().get(0).getRefuelType();
                        if (item.getPrice().get(key1)!=null){
                            helper.setVisible(R.id.item,true);
                            helper.setText(R.id.refuel_type,key1+":");
                            helper.setText(R.id.price,item.getPrice().get(key1)+"元/升");
                            keys.add(key1);
                        }
                    }else if (refuelTypeSize==2){
                        String key2 = MyApplication.getCurrentServerCar().getRefuelType().get(1).getRefuelType();
                        if (item.getPrice().get(key2)!=null){
                            helper.setVisible(R.id.item1,true);
                            helper.setText(R.id.refuel_type1,key2+":");
                            helper.setText(R.id.price1,item.getPrice().get(key2)+"元/升");
                            keys.add(key2);
                        }
                    }else {
                        String key3 = MyApplication.getCurrentServerCar().getRefuelType().get(2).getRefuelType();
                        if (item.getPrice().get(key3)!=null){
                            helper.setVisible(R.id.item2,true);
                            helper.setText(R.id.refuel_type2,key3+":");
                            helper.setText(R.id.price2,item.getPrice().get(key3)+"元/升");
                            keys.add(key3);
                        }
                    }
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable("stationInfo", item);
                    helper.setOnClickListener(R.id.details, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            it = new Intent(NearbyRefuelStationActivity.this, RefuelStationDetailsActivity.class);
                            if (keys!=null&&keys.size()>0){
                                it.putStringArrayListExtra("key",keys);
                            }
                            it.putExtras(bundle);
                            startActivity(it);
                        }
                    });
                    helper.setOnClickListener(R.id.edit_order, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            it = new Intent(NearbyRefuelStationActivity.this, IndentWriteActivity.class);
                            if (keys!=null&&keys.size()>0){
                                it.putStringArrayListExtra("key",keys);
                            }
                            it.putExtras(bundle);
                            startActivity(it);
                        }
                    });
                }
            });
        }
    }
}
