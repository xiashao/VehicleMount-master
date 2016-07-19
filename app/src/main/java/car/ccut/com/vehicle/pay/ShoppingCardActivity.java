package car.ccut.com.vehicle.pay;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.Refuel.OrderRefuel;
import car.ccut.com.vehicle.bean.payType;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.util.PayResult;
import car.ccut.com.vehicle.util.SignUtils;

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
 * Created by WangXin on 2016/5/8 0008.
 */
public class ShoppingCardActivity extends BaseActivity{
    @Bind(R.id.recipient)
    TextView recipient;
    @Bind(R.id.money)
    TextView money;
    @Bind(R.id.commodity_name)
    TextView commodityName;
    @Bind(R.id.commodity_num)
    TextView commodityNum;
    @Bind(R.id.listView)
    ListView listView;
    private List<payType> payTypes;
    private OrderRefuel order;
    private static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getApplicationContext(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shopping_card;
    }

    @Override
    public void initView() {
        setTitle("付款");
        setBackIcon();
        if (order!=null){
            recipient.setText(order.getFuelName());
            money.setText(order.getMoney()+"");
            commodityName.setText("加油单");
            commodityNum.setText(order.getOrderNum());
        }
        listView.setAdapter(new QuickAdapter<payType>(this,R.layout.pay_list_item,payTypes) {
            @Override
            protected void convert(BaseAdapterHelper helper, payType item) {
                helper.setText(R.id.payName,item.getPayName());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String payType = payTypes.get(i).getPayName();
                switch (payType){
                    case "微信支付":
                        break;
                    case "支付宝支付":
                        alipay();
                        break;
                    case "银联在线":
                        break;
                    case "百度钱包":
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        payTypes = new ArrayList<payType>();
        for (int i=0;i< ConstantValue.payNames.length;i++){
            payType payType = new payType();
            payType.setPayName(ConstantValue.payNames[i]);
            payTypes.add(payType);
        }
        order = (OrderRefuel) getIntent().getSerializableExtra("order");
    }

    @Override
    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                AppManager.getAppManager().finishActivity();
                break;
        }
    }

    private void alipay(){
        String orderInfo = getOrderInfo(order);
        String sign = sign(orderInfo);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(ShoppingCardActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    private String getOrderInfo(OrderRefuel orderRefuel){
        String orderInfo = "partner=" + "\"" + ConstantValue.PARTNER1 + "\"";
        orderInfo += "&seller_id=" + "\"" + ConstantValue.SELLER1+ "\"";
        orderInfo += "&out_trade_no=" + "\"" +orderRefuel.getOrderNum() + "\"";
        orderInfo += "&subject=" + "\"" + orderRefuel.getRefuelType() + "\"";
        orderInfo += "&body=" + "\"" + orderRefuel.getFuelName()+orderRefuel.getRefuelType()+orderRefuel.getFuelCount() + "\"";
        orderInfo += "&total_fee=" + "\"" + orderRefuel.getMoney() + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }
    private String sign(String content) {
        return SignUtils.sign(content, ConstantValue.RSA_PRIVATE1);
    }
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
