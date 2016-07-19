package car.ccut.com.vehicle.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.car.CarInfo;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

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
 * Created by WangXin on 2016/4/17 0017.
 */
public class CarInfoDetailsActivity extends BaseActivity {
    @Bind(R.id.car_photo)
    CircleImageView carPhoto;
    @Bind(R.id.car_brand)
    TextView carBrand;
    @Bind(R.id.car_type)
    TextView carType;
    @Bind(R.id.car_num)
    TextView carNum;
    @Bind(R.id.car_machine_num)
    TextView machineNume;
    @Bind(R.id.body_level)
    TextView bodyLevel;
    @Bind(R.id.fuel_type)
    TextView fuelType;
    @Bind(R.id.expend_num)
    TextView expandNum;
    @Bind(R.id.fuel_amount)
    TextView fuelAmount;
    @Bind(R.id.engine_performance)
    TextView enginePerformance;
    @Bind(R.id.transmission_performance)
    TextView transmissionPerformance;
    @Bind(R.id.light_status)
    TextView lightStatus;

    private CarInfo carInfo;
    private String carId;
    private ActionSheet.ActionSheetListener select;
    private static int UPDATE_FLAG = -1;
    private static final int UPDATE_ENGINE=1;
    private static final int UPDATE_TRANSMISSION=2;
    private static final int UPDATE_LIGHT=3;
    private static final int UPDATE_AMUONT=4;
    private static final int UPDATE_EXPEND=5;
    private String[] items = new String[]{"正常","异常"};

    @Override
    protected int getLayoutId() {
        return R.layout.actvivty_car_info_details;
    }

    @Override
    public void initView() {
        setTitle("车辆信息");
        setBackIcon();
        select = new ActionSheet.ActionSheetListener() {
            @Override
            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

            }

            @Override
            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                updateCarInfo(index);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("detailCarInfo");
        MyApplication.getHttpQueues().cancelAll("update");
    }

    public void showData(CarInfo carInfo){
        ImageLoader.getInstance().displayImage(ConstantValue.CAR_PHOTO_URL+carInfo.getCarPhoto(),carPhoto);
        carBrand.setText(carInfo.getCarBrand());
        carType.setText(carInfo.getCarType());
        carNum.setText(carInfo.getCarNumber());
        machineNume.setText(carInfo.getCarMachineNo());
        bodyLevel.setText(carInfo.getBodyLevel());
        String refuelType="";
        if (carInfo.getRefuelType()!=null&&carInfo.getRefuelType().size()>0){
            for (int i=0;i<carInfo.getRefuelType().size();i++){
                refuelType+=carInfo.getRefuelType().get(i).getRefuelType()+";";
            }
            fuelType.setText(refuelType.substring(0,refuelType.length()-1));
        }
        expandNum.setText(carInfo.getExpendNumber());
        fuelAmount.setText(carInfo.getFuelAmount());
        enginePerformance.setText(Utils.getPerformance(carInfo.getEnginePerformance()));
        transmissionPerformance.setText(Utils.getPerformance(carInfo.getTransmissionPerformance()));
        lightStatus.setText(Utils.getPerformance(carInfo.getLampStatus()));
    }

    @Override
    public void initData() {
        showWaitDialog();
        carId = getIntent().getStringExtra("carId");
        if (carId!=null){
            detailCarInfo(carId);
        }
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.switch_btn,R.id.delete_btn,R.id.item1,R.id.item2,R.id.item3,R.id.item4,R.id.item5})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.switch_btn:
                if (carInfo!=null){
                    MyApplication.setCurrentServerCar(carInfo);
                }
                onBackPressed();
                break;
            case R.id.delete_btn:
                if (carId!=null){
                    deleteCar(carId);
                }
                break;
            case R.id.item1:
                UPDATE_FLAG = UPDATE_EXPEND;
                updateEditInfo();
                break;
            case R.id.item2:
                UPDATE_FLAG=UPDATE_AMUONT;
                updateEditInfo();
                break;
            case R.id.item3:
                UPDATE_FLAG=UPDATE_ENGINE;
                sheetBottom();
                break;
            case R.id.item4:
                UPDATE_FLAG=UPDATE_TRANSMISSION;
                sheetBottom();
                break;
            case R.id.item5:
                UPDATE_FLAG=UPDATE_LIGHT;
                sheetBottom();
                break;

        }
    }

    private void updateEditInfo(){
        LayoutInflater layoutInflaterPhone = LayoutInflater.from(this);
        final View view = layoutInflaterPhone.inflate(R.layout.dialog_change_phone, null);
        TextView lable = (TextView) view.findViewById(R.id.lable);
        final EditText editText = (EditText) view.findViewById(R.id.change_phone_password);
        TextView until = (TextView) view.findViewById(R.id.unitl);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("");
        String title="";
        if (UPDATE_FLAG == UPDATE_AMUONT){
            title="修改燃油量";
            lable.setText("燃油量:");
            until.setVisibility(View.VISIBLE);
        }else if (UPDATE_FLAG == UPDATE_EXPEND){
            title="修改里程数";
            lable.setText("里程数:");
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String info = editText.getText().toString().trim();
                        if (!TextUtils.isEmpty(info)){
                            updateCarInfo(Integer.valueOf(info));
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void sheetBottom(){
        ActionSheet.createBuilder(this,getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(items)
                .setCancelableOnTouchOutside(true)
                .setListener(select).show();
    }

    public void deleteCar(String carId){
        Map params = new HashMap();
        params.put("id",carId);
        JsonRequestWithAuth<AjaxResponse> deleteCar = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.DELETE_CAR, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        if (response.isOk()){
                            onBackPressed();
                        }else {
                            Toast.makeText(CarInfoDetailsActivity.this,"请求失败,请重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(deleteCar);
        MyApplication.getHttpQueues().start();
    }

    public void detailCarInfo(String carId){
        Map params = new HashMap();
        params.put("carId",carId);
        JsonRequestWithAuth<AjaxResponse> detailCarInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.DETAILS_CAR_INFO, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        hideWaitDialog();
                        if (response.isOk()){
                            Gson gson = new Gson();
                            carInfo = gson.fromJson(gson.toJson(response.getResponseData().get("detailCarInfo")),new TypeToken<CarInfo>(){}.getType());
                            if (carInfo!=null){
                                showData(carInfo);
                            }
                        }else {
                            Toast.makeText(CarInfoDetailsActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(detailCarInfo);
        MyApplication.getHttpQueues().start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void updateCarInfo(final int info){
        showWaitDialog();
        String url ="";
        Map params = new HashMap();
        params.put("id",MyApplication.getCurrentServerCar().getId());
        switch (UPDATE_FLAG){
            case UPDATE_ENGINE:
                params.put("enginePerformance",info+"");
                url = ConstantValue.UPDATE_ENGINE_PERFORMANCE;
                break;
            case UPDATE_TRANSMISSION:
                url = ConstantValue.UPDATE_TRANSMISSION_PERFORMANCE;
                params.put("transmissionPerformance",info+"");
                break;
            case UPDATE_LIGHT:
                url = ConstantValue.UPDATE_LAMP_STATUS;
                params.put("lampStatus",info+"");
                break;
            case UPDATE_AMUONT:
                url = ConstantValue.UPDATE_FUEL_AMOUNT;
                params.put("fuelAmount",info+"");
                break;
            case UPDATE_EXPEND:
                url = ConstantValue.UPDATE_EXPEND_NUMBER;
                params.put("expendNumber",info+"");
                break;
        }
        if ("".equals(url)){
            return;
        }
        JsonRequestWithAuth<AjaxResponse> update = new JsonRequestWithAuth<AjaxResponse>(url, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                hideWaitDialog();
                if (response.isOk()){
                    switch (UPDATE_FLAG){
                        case UPDATE_ENGINE:
                            if (info==ConstantValue.ABNORMAL){
                                enginePerformance.setText(items[info]);
                                notification("引擎出现故障,请您及时查看");
                            }
                            break;
                        case UPDATE_TRANSMISSION:
                            transmissionPerformance.setText(items[info]);
                            if (info==ConstantValue.ABNORMAL){
                                notification("变速箱出现故障,请及时查看");
                            }
                            break;
                        case UPDATE_LIGHT:
                            lightStatus.setText(items[info]);
                            if (info == ConstantValue.ABNORMAL){
                                notification("大灯出现故障,请您及时查看");
                            }
                            break;
                        case UPDATE_AMUONT:
                            fuelAmount.setText(info+"");
                            if (info<20){
                                notification("燃油即将耗尽,请及时补充");
                            }
                            break;
                        case UPDATE_EXPEND:
                            expandNum.setText(info+"");
                            if (info>15000){
                                notification("里程数已超过正常范围,请及时检修");
                            }
                            break;
                    }
                }else {
                    Toast.makeText(CarInfoDetailsActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                }
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(update);
        MyApplication.getHttpQueues().start();
    }

    private void notification(String content){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("警告")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.logo);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
}
