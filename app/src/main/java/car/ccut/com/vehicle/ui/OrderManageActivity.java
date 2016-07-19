package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
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
import car.ccut.com.vehicle.bean.Refuel.Order;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;

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
 * Created by WangXin on 2016/5/6 0006.
 */
public class OrderManageActivity extends BaseActivity {
    @Bind(R.id.list_view)
    ListView listView;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.empty)
    TextView empty;
    private int orderStatus;
    private QuickAdapter<Order> orderListAdapter;
    private List<Order> orderList;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_manage;
    }

    @Override
    public void initView() {
        setBackIcon();
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setBackgroundResource(R.drawable.text_click_bg);
        switchStatus();
    }



    @Override
    public void initData() {
        orderStatus = getIntent().getIntExtra("orderType",0);
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.tv_add})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.tv_add:
                if (orderStatus==ConstantValue.ORDER_UNFINISHED){
                    orderStatus=ConstantValue.ORDER_FINISH;
                }else {
                    orderStatus = ConstantValue.ORDER_UNFINISHED;
                }
                switchStatus();
                break;
        }
    }

    public void switchStatus(){
        if (orderStatus== ConstantValue.ORDER_UNFINISHED){
            tvAdd.setText("全部");
            setTitle("待加油订单");
        }else {
            tvAdd.setText("待加油");
            setTitle("全部订单");
        }
        requestOrder(MyApplication.getCurrentUser().getId(),orderStatus+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("updateOrderStatus");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void requestOrder(String userId,String status){
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("status",status);
        params.put("pageNo","1");
        JsonRequestWithAuth<AjaxResponse> orderRequest = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.REQUEST_ORDER, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        Gson gson = new Gson();
                        if (response.isOk()){
                            orderList = gson.fromJson(gson.toJson(response.getResponseData().get("orderListPage")),new TypeToken<ArrayList<Order>>(){}.getType());
                            if (orderListAdapter==null){
                                setDataList();
                            }else {
                                orderListAdapter.replaceAll(orderList);
                                orderListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(orderRequest);
        MyApplication.getHttpQueues().start();
    }

    public void setDataList(){
        if (orderList!=null&&orderList.size()>0){
            empty.setVisibility(View.GONE);
            orderListAdapter = new QuickAdapter<Order>(this,R.layout.order_item,orderList) {
                @Override
                protected void convert(final BaseAdapterHelper helper, final Order item) {
                    if (item.getStatus()==ConstantValue.ORDER_UNFINISHED){
                        helper.setText(R.id.order_status,"待加油");
                        helper.setText(R.id.button1,"完成");
                    }else {
                        helper.setText(R.id.order_status,"完成");
                        helper.setText(R.id.button1,"再次预约");
                    }
                    helper.setOnClickListener(R.id.button1, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(item.getStatus()==ConstantValue.ORDER_UNFINISHED){
                                // TODO 完成处理
                                updateOrderStatus(item.getId(),ConstantValue.ORDER_FINISH,helper.getPosition());
                            }else{
                                // TODO 再次预约
                            }
                        }
                    });
                    helper.setText(R.id.order_money,item.getAmount()+"元");
                    helper.setText(R.id.name,item.getFuelName());
                    helper.setText(R.id.order_fuel_type,item.getGasType());
                    helper.setText(R.id.order_time,item.getOrderTime());
                    helper.setText(R.id.order_amount,item.getGasQuatity()+"L");
                    helper.setOnClickListener(R.id.details, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(OrderManageActivity.this,OrderDetailsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("order", (Serializable) item);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
            };
            listView.setAdapter(orderListAdapter);
        }else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    public void updateOrderStatus(String orderId, int status, final int position){
        showWaitDialog();
        Map params = new HashMap();
        params.put("id",orderId);
        params.put("status",status+"");
        JsonRequestWithAuth<AjaxResponse> updateOrderStatus  = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.UPDATE_ORDER_STATUS, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        hideWaitDialog();
                        if (response.isOk()){
                            orderListAdapter.remove(position);
                        }else {
                            Toast.makeText(OrderManageActivity.this,"请求失败,请重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
            }
        });
        MyApplication.getHttpQueues().add(updateOrderStatus);
        MyApplication.getHttpQueues().start();
    }
}
