package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.Refuel.RefuelPrice;
import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;

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
 * Created by WangXin on 2016/4/9 0009.
 */
public class RefuelStationDetailsActivity extends BaseActivity {
    @Bind(R.id.station_name)
    TextView stationName;
    @Bind(R.id.distance)
    TextView distance;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.listView)
    ListView listView;
    private RefuelStationInfo stationInfo;
    private List<RefuelPrice> prices = new ArrayList<RefuelPrice>();
    private ArrayList<String> keys;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_station_details;
    }

    @Override
    public void initView() {
        setTitle("加油站详情");
        stationName.setText(stationInfo.getName());
        distance.setText(stationInfo.getDistance()+"m");
        address.setText(stationInfo.getAddress());
        listView.setAdapter(new QuickAdapter<RefuelPrice>(RefuelStationDetailsActivity.this,R.layout.refuel_station_details_item,prices) {
            @Override
            protected void convert(BaseAdapterHelper helper, RefuelPrice item) {
                helper.setText(R.id.refuel_type,item.getRefuelType());
                helper.setText(R.id.local_price,item.getLocalPrice());
                helper.setText(R.id.gastprice,item.getGastprice());
            }
        });
    }

    @Override
    public void initData() {
        stationInfo = (RefuelStationInfo) getIntent().getSerializableExtra("stationInfo");
        keys = getIntent().getStringArrayListExtra("key");
        if (stationInfo.getGastprice()!=null&&stationInfo.getPrice()!=null){
            Iterator it = null;
            boolean flag = maxSize();
            if (flag){
                it = stationInfo.getGastprice().entrySet().iterator();
            }else {
                it = stationInfo.getPrice().entrySet().iterator();
            }
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                RefuelPrice refuelPrice = new RefuelPrice();
                refuelPrice.setRefuelType(entry.getKey().toString());
                if (flag){
                    refuelPrice.setGastprice(entry.getValue().toString()+"元/升");
                }else {
                    refuelPrice.setLocalPrice(entry.getValue().toString()+"元/升");
                }
                prices.add(refuelPrice);
            }
            for (int i=0;i<prices.size();i++){
                String key = prices.get(i).getRefuelType();
                if (flag){
                    if (stationInfo.getPrice().get(key)!=null){
                        prices.get(i).setLocalPrice(stationInfo.getPrice().get(key)+"元/升");
                    }
                }else {
                    if (stationInfo.getGastprice().get(key)!=null){
                        prices.get(i).setGastprice(stationInfo.getGastprice().get(key)+"元/升");
                    }
                }
            }
        }
    }
    @Override
    @OnClick({R.id.iv_title_back,R.id.order})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.order:
                Intent it = new Intent(this,IndentWriteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("stationInfo",stationInfo);
                if (keys!=null&&keys.size()>0){
                    it.putStringArrayListExtra("key",keys);
                }
                it.putExtras(bundle);
                startActivity(it);
                break;
        }
    }

    private boolean maxSize(){
        return stationInfo.getGastprice().size()>=stationInfo.getPrice().size()?true:false;
    }
}
