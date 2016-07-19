package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.base.MultipartRequest;
import car.ccut.com.vehicle.bean.Refuel.OrderRefuel;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.pay.ShoppingCardActivity;
import car.ccut.com.vehicle.ui.BaiDuNativ.BNDemoMainActivity;
import car.ccut.com.vehicle.util.Utils;
import car.ccut.com.vehicle.zxing.encoding.EncodingHandler;

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
 * Created by WangXin on 2016/4/15 0015.
 */
public class QuickMarkActivity extends BaseActivity implements BDLocationListener,ActionSheet.ActionSheetListener {

    @Bind(R.id.refuel_name)
    TextView refuelName;
    @Bind(R.id.fuel_type)
    TextView fuelType;
    @Bind(R.id.money)
    TextView money;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.fuel_count)
    TextView fuelCount;
    @Bind(R.id.mark_code)
    ImageView markCode;
    @Bind(R.id.save)
    Button save;
    @Bind(R.id.navi)
    LinearLayout navigation;
    String markCodeInfo;

    private OrderRefuel orderRefuelInfo;
    private LocationClient locationClient;
    private List<BNRoutePlanNode> routePlanNodes = new ArrayList<BNRoutePlanNode>();
    private int RoutePlanPreference;
    private String[] routePlanPreferenceItem = {"推荐","躲避拥堵","少收费","高速优先","少走高速"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_quick_mark;
    }

    @Override
    public void initView() {
        setTitle("订单信息");
        setBackIcon();
        if (orderRefuelInfo!=null){
            try {
                refuelName.setText(orderRefuelInfo.getFuelName());
                fuelType.setText(orderRefuelInfo.getRefuelType());
                money.setText(orderRefuelInfo.getMoney()+"元");
                date.setText(orderRefuelInfo.getOrderDate());
                fuelCount.setText(orderRefuelInfo.getFuelCount()+"升");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            markCodeInfo = new String(new Gson().toJson(orderRefuelInfo).getBytes("UTF-8"),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (markCodeInfo!=null&&!markCodeInfo.equals("")){
            try {
                Bitmap qrcodeBitmap = EncodingHandler.createQRCode(markCodeInfo, 600);
                markCode.setImageBitmap(qrcodeBitmap);
                String markPath = Utils.saveMyBitmap(this,qrcodeBitmap);
                orderRefuelInfo.setMarkPath(markPath);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        navigation.setEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!locationClient.isStarted()){
            locationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.stop();
    }

    @Override
    public void initData() {
        orderRefuelInfo = (OrderRefuel) getIntent().getSerializableExtra("orderInfo");
        initLocation();
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.pay,R.id.save,R.id.navi})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.pay:
                Intent it = new Intent(this, ShoppingCardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",orderRefuelInfo);
                it.putExtras(bundle);
                startActivity(it);
                break;
            case R.id.save:
                addOrder();
                break;
            case R.id.navi:
                selectRoutePlanPreference();
                break;
        }
    }

    private void addOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", MyApplication.getCurrentUser().getUsername());
        params.put("orderNum",orderRefuelInfo.getOrderNum());
        params.put("address", orderRefuelInfo.getAddress());
        params.put("carId", orderRefuelInfo.getCarNum());
        params.put("latx", orderRefuelInfo.getLantitude()+"");
        params.put("laty", orderRefuelInfo.getLontitude() + "");
        params.put("fuelName", orderRefuelInfo.getFuelName());
        params.put("gasQuatity", orderRefuelInfo.getFuelCount() + "");
        params.put("amount", orderRefuelInfo.getMoney() + "");
        params.put("gasType", orderRefuelInfo.getRefuelType() + "");
        params.put("orderTime", orderRefuelInfo.getOrderDate());
        params.put("status",ConstantValue.ORDER_UNFINISHED+"");
        MultipartRequest request = new MultipartRequest(Request.Method.POST, ConstantValue.REQUEST_SAVE_ORDER, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                if (response.isOk()){
                    Intent it = new Intent(QuickMarkActivity.this,OrderManageActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        }, "file", new File(orderRefuelInfo.getMarkPath()), params);

        MyApplication.getHttpQueues().add(request);
        MyApplication.getHttpQueues().start();
    }

    private void initLocation(){
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        locationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //起点
        BNRoutePlanNode startNode = new BNRoutePlanNode(bdLocation.getLongitude(),bdLocation.getLatitude(),bdLocation.getAddrStr(),null, BNRoutePlanNode.CoordinateType.BD09LL);
        routePlanNodes.add(startNode);
        //终点
        BNRoutePlanNode endNode = new BNRoutePlanNode(orderRefuelInfo.getLontitude(),orderRefuelInfo.getLantitude(),orderRefuelInfo.getFuelName(),null, BNRoutePlanNode.CoordinateType.BD09LL);
        routePlanNodes.add(endNode);
        navigation.setEnabled(true);
    }

    private void planRoute(List<BNRoutePlanNode> routePlanNodes){
        Intent it = new Intent(this, BNDemoMainActivity.class);
        it.putExtra("RoutePlanPreference",RoutePlanPreference);
        Bundle bundle = new Bundle();
        bundle.putSerializable("routPlanNodes",(Serializable)routePlanNodes);
        it.putExtras(bundle);
        startActivity(it);
    }

    private void selectRoutePlanPreference(){
        ActionSheet.createBuilder(this,getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(routePlanPreferenceItem)
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
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
        planRoute(routePlanNodes);
    }
}
