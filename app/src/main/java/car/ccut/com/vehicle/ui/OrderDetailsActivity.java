package car.ccut.com.vehicle.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.Refuel.Order;
import car.ccut.com.vehicle.interf.ConstantValue;

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
 * Created by WangXin on 2016/6/11 0011.
 */
public class OrderDetailsActivity extends BaseActivity {

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

    private Order order;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_details;
    }

    @Override
    public void initView() {
        setTitle("订单详情");
        if (order!=null){
            try {
                refuelName.setText(order.getFuelName());
                fuelType.setText(order.getGasType());
                money.setText(order.getAmount()+"元");
                date.setText(order.getOrderTime());
                fuelCount.setText(order.getGasQuatity()+"升");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ImageLoader.getInstance().displayImage(ConstantValue.ORDER_MARK_URL+order.getMarkPath(),markCode);
    }

    @Override
    public void initData() {
        order = (Order) getIntent().getSerializableExtra("order");
    }

    @Override
    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
        }
    }
}
