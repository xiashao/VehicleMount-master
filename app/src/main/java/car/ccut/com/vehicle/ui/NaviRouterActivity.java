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
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baoyz.actionsheet.ActionSheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.PoiInfos;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.ui.BaiDuNativ.BNDemoMainActivity;

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
 * Created by WangXin on 2016/5/7 0007.
 */
public class NaviRouterActivity extends BaseActivity implements ActionSheet.ActionSheetListener {
    @Bind(R.id.origin)
    AutoCompleteTextView origin;
    @Bind(R.id.finish)
    AutoCompleteTextView finish;
    @Bind(R.id.through_point1)
    AutoCompleteTextView throughPoint1;
    @Bind(R.id.through_point2)
    AutoCompleteTextView throughPoint2;
    @Bind(R.id.through_point3)
    AutoCompleteTextView throughPoint3;
    private List<BNRoutePlanNode> nodes = new ArrayList<BNRoutePlanNode>();

    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private PoiInfos originNode;
    private PoiInfos finishNode;
    private PoiInfos throughNode1;
    private PoiInfos throughNode2;
    private PoiInfos throughNode3;

    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    //每页容量
    private static final int PAGE_CAPACITY = 50;
    //第一页
    private static final int PAGE_NUM = 0;
    //搜索半径10km
    private static final int RADIUS = 10000;
    private ArrayAdapter<String> searchAdapter;
    private String[] searchResult;
    private int changeFlag=2;
    private static final int ORIGIN = 0;
    private static final int FINISH = 1;
    private static final int THROUGH_POINT1 = 2;
    private static final int THROUGH_POINT2 =3;
    private static final int THROUGH_POINT3=4;

    private List<PoiInfo> mPoiResult = new ArrayList<PoiInfo>();
    private double mLantitude;
    private double mLontitude;
    private boolean isFristIn;
    private int RoutePlanPreference;
    private String[] routePlanPreferenceItem = {"推荐","躲避拥堵","少收费","高速优先","少走高速"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_navi_router;
    }

    @Override
    public void initView() {
        setTitle("导航路线");
        setBackIcon();
        mPoiSearch = PoiSearch.newInstance();
        poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                hideWaitDialog();
                if (poiResult==null||poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD){
                   return;
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR){
                    System.out.println(changeFlag);
                    mPoiResult.clear();
                    mPoiResult.addAll(poiResult.getAllPoi());
                    switch (changeFlag){
                        case ORIGIN:
                            AutoCompleteData(poiResult,origin);
                            break;
                        case FINISH:
                            AutoCompleteData(poiResult,finish);
                            break;
                        case THROUGH_POINT1:
                            AutoCompleteData(poiResult,throughPoint1);
                            break;
                        case THROUGH_POINT2:
                            AutoCompleteData(poiResult,throughPoint2);
                            break;
                        case THROUGH_POINT3:
                            AutoCompleteData(poiResult,throughPoint3);
                            break;
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        origin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(origin.getText().toString(),ORIGIN);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter!=null){
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        origin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (originNode==null){
                    originNode = new PoiInfos();
                }
                originNode.setName(mPoiResult.get(i).name);
                originNode.setLontitude(mPoiResult.get(i).location.longitude);
                originNode.setLatitude(mPoiResult.get(i).location.latitude);
            }
        });
        finish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(finish.getText().toString(),FINISH);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter!=null){
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        finish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(finishNode==null){
                    finishNode = new PoiInfos();
                }
                finishNode.setName(mPoiResult.get(i).name);
                finishNode.setLatitude(mPoiResult.get(i).location.latitude);
                finishNode.setLontitude(mPoiResult.get(i).location.longitude);
            }
        });
        throughPoint1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(throughPoint1.getText().toString(),THROUGH_POINT1);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter!=null){
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        throughPoint1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (throughNode1==null){
                    throughNode1 = new PoiInfos();
                }
                throughNode1.setName(mPoiResult.get(i).name);
                throughNode1.setLatitude(mPoiResult.get(i).location.latitude);
                throughNode1.setLontitude(mPoiResult.get(i).location.longitude);
            }
        });
        throughPoint2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(throughPoint2.getText().toString(),THROUGH_POINT2);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter!=null){
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        throughPoint2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (throughNode2==null){
                    throughNode2 = new PoiInfos();
                }
                throughNode2.setName(mPoiResult.get(i).name);
                throughNode2.setLatitude(mPoiResult.get(i).location.latitude);
                throughNode2.setLontitude(mPoiResult.get(i).location.longitude);
            }
        });
        throughPoint3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(throughPoint3.getText().toString(),THROUGH_POINT3);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter!=null){
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        throughPoint3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (throughNode3==null){
                    throughNode3 = new PoiInfos();
                }
                throughNode3.setName(mPoiResult.get(i).name);
                throughNode3.setLatitude(mPoiResult.get(i).location.latitude);
                throughNode3.setLontitude(mPoiResult.get(i).location.longitude);
            }
        });

    }

    @Override
    public void initData() {
        finishNode = (PoiInfos) getIntent().getSerializableExtra("finishNode");
        if (finishNode != null) {
            finish.setText(finishNode.getName());
        }
        isFristIn=true;
        position();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    @OnClick({R.id.iv_title_back, R.id.exchange, R.id.navi_btn, R.id.position_icon})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.exchange:
                exchange();
                break;
            case R.id.navi_btn:
                selectRoutePlanPreference();
                break;
            case R.id.position_icon:
                position();
                break;
        }
    }

    private void position() {
        mLocationClient = new LocationClient(getApplicationContext());
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        if (!mLocationClient.isStarted()){
            mLocationClient.start();
        }
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
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index){
            case 0:
                RoutePlanPreference = ConstantValue.ROUTE_PLAN_MOD_RECOMMEND;       //推荐
                break;
            case 1:
                RoutePlanPreference = ConstantValue.ROUTE_PLAN_MOD_AVOID_TAFFICJAM; //躲避拥堵
                break;
            case 2:
                RoutePlanPreference = ConstantValue.ROUTE_PLAN_MOD_MIN_TOLL;        //少收费
                break;
            case 3:
                RoutePlanPreference = ConstantValue.ROUTE_PLAN_MOD_MIN_TIME;        //高速优先
                break;
            case 4:
                RoutePlanPreference = ConstantValue.ROUTE_PLAN_MOD_MIN_DIST;        //少走高速
                break;
        }
        planRoute();
    }

    private void selectRoutePlanPreference(){
        ActionSheet.createBuilder(this,getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(routePlanPreferenceItem)
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            hideWaitDialog();
            mLantitude = bdLocation.getLatitude();
            mLontitude = bdLocation.getLongitude();
            System.out.println(isFristIn);
            if (!isFristIn){
                origin.setText(bdLocation.getAddrStr());
            }
            isFristIn=false;
            if (originNode==null){
                originNode = new PoiInfos();
            }
            try {
                originNode.setName(bdLocation.getAddrStr());
                originNode.setLatitude(bdLocation.getLatitude());
                originNode.setLontitude(bdLocation.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void AutoCompleteData(PoiResult poiResult, AutoCompleteTextView view) {
        if (poiResult != null) {
            searchResult = new String[poiResult.getAllPoi().size()];
        }
        for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
            searchResult[i] = poiResult.getAllPoi().get(i).name;
        }
        searchAdapter = new ArrayAdapter<String>(NaviRouterActivity.this, android.R.layout.simple_list_item_1, searchResult) {
            private Filter f;
            public Filter getF() {
                if (f == null) {
                    f = new Filter() {
                        @Override
                        protected FilterResults performFiltering(CharSequence charSequence) {
                            ArrayList<Object> suggestions = new ArrayList<Object>();
                            for (String adr : searchResult) {
                                suggestions.add(adr);
                            }
                            FilterResults filterResults = new FilterResults();
                            filterResults.values = suggestions;
                            filterResults.count = suggestions.size();
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
        view.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
        searchAdapter.notifyDataSetInvalidated();
    }

    private void searchInfo(String searchInfo,int flag) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(searchInfo)
                .location(new LatLng(mLantitude, mLontitude))
                .pageCapacity(PAGE_CAPACITY)
                .pageNum(PAGE_NUM)
                .radius(RADIUS));
        changeFlag = flag;
    }

    private void exchange(){
        if (originNode==null&&finishNode==null){
            return;
        }
        PoiInfos tempNode;
        tempNode = finishNode;
        finishNode = originNode;
        originNode = tempNode;
        origin.setText(originNode.getName());
        finish.setText(finishNode.getName());
    }

    private void planRoute(){
        if (originNode==null){
            Toast.makeText(this,"请输入导航起点",Toast.LENGTH_SHORT).show();
            return;
        }
        if (finishNode==null){
            Toast.makeText(this,"请输入导航终点",Toast.LENGTH_SHORT).show();
            return;
        }
        BNRoutePlanNode startNode = new BNRoutePlanNode(originNode.getLontitude(),originNode.getLatitude(),originNode.getName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
        nodes.add(startNode);
        if (throughNode1!=null){
            BNRoutePlanNode throughPlanNode1 = new BNRoutePlanNode(throughNode1.getLontitude(),throughNode1.getLatitude(),throughNode1.getName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
            nodes.add(throughPlanNode1);
        }
        if (throughNode2!=null){
            BNRoutePlanNode throughPlanNode2 = new BNRoutePlanNode(throughNode2.getLontitude(),throughNode2.getLatitude(),throughNode2.getName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
            nodes.add(throughPlanNode2);
        }
        if (throughNode3!=null){
            BNRoutePlanNode throughPlanNode3 = new BNRoutePlanNode(throughNode3.getLontitude(),throughNode3.getLatitude(),throughNode3.getName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
            nodes.add(throughPlanNode3);
        }
        BNRoutePlanNode finishPlanNode = new BNRoutePlanNode(finishNode.getLontitude(),finishNode.getLatitude(),finishNode.getName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
        nodes.add(finishPlanNode);
        Intent it = new Intent(this, BNDemoMainActivity.class);
        it.putExtra("RoutePlanPreference",RoutePlanPreference);
        Bundle bundle = new Bundle();
        bundle.putSerializable("routPlanNodes", (Serializable) nodes);
        it.putExtras(bundle);
        startActivity(it);
    }
}
