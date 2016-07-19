package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.PoiInfos;

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
 * Created by WangXin on 2016/3/11 0011.
 */
public class NearbyFacilitesActivity extends BaseActivity {

    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    private LocationClient mLocationClient;
    //每页容量
    private static final int PAGE_CAPACITY=50;
    //第一页
    private static final int PAGE_NUM = 0;
    //搜索半径10km
    private static final int RADIUS = 10000;
    private double mLantitude;
    private double mLontitude;
    private boolean searchText;
    private MyLocationListener myLocationListener;
    private ArrayAdapter<String> searchAdapter;
    private  String [] searchResult;
    private PoiResult mPoiResult;
    List<PoiInfos> poiInfos = new ArrayList<PoiInfos>();
    @Bind(R.id.search)
    AutoCompleteTextView search;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_neraby_facilites;
    }

    @Override
    public void initView() {
        setTitle("附近检索");
        mPoiSearch = PoiSearch.newInstance();
        poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(final PoiResult poiResult) {
                hideWaitDialog();
                if (poiResult==null||poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD){
                    Toast.makeText(NearbyFacilitesActivity.this,"在附近未找到相关信息",Toast.LENGTH_SHORT).show();
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
                       searchAdapter = new ArrayAdapter<String>(NearbyFacilitesActivity.this,android.R.layout.simple_list_item_1,searchResult){
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
                       Intent intent = new Intent(NearbyFacilitesActivity.this,SearchInfosActivity.class);
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
                    Intent intent = new Intent(NearbyFacilitesActivity.this,SearchInfosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("poiInfos", (Serializable) poiInfos);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    public void initData() {
        searchText=false;
        //定位相关
        mLocationClient = new LocationClient(getApplicationContext());
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
        showWaitDialog("定位中,请稍后...");
    }



    @Override
    @OnClick({R.id.iv_title_back,R.id.food,R.id.hotel,R.id.bus_station,R.id.bank,R.id.movie,R.id.train,R.id.ktv,R.id.market})
    public void onClick(View view) {
        int id = view.getId();
        if(id!=R.id.iv_title_back){
            searchText=false;
            showWaitDialog("搜索中,请稍后...");
        }
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.food:
                searchInfo("美食");
                break;
            case R.id.hotel:
                searchInfo("酒店");
                break;
            case R.id.bank:
                searchInfo("银行");
                break;
            case R.id.bus_station:
                searchInfo("公交站");
                break;
            case R.id.movie:
                searchInfo("电影");
                break;
            case R.id.train:
                searchInfo("火车站");
                break;
            case R.id.ktv:
                searchInfo("KTV");
                break;
            case R.id.market:
                searchInfo("商场");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPoiSearch!=null){
            mPoiSearch.destroy();
        }
    }

    private void  searchInfo(String searchInfo){
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(searchInfo)
                                    .location(new LatLng(mLantitude,mLontitude))
                                    .pageCapacity(PAGE_CAPACITY)
                                    .pageNum(PAGE_NUM)
                                    .radius(RADIUS));
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLantitude = bdLocation.getLatitude();
            mLontitude = bdLocation.getLongitude();
            hideWaitDialog();
        }
    }
}
