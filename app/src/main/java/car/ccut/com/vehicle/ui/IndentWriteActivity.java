package car.ccut.com.vehicle.ui;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.Refuel.OrderRefuel;
import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.view.RayMenu;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
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
 * Created by WangXin on 2016/3/9 0009.
 */
public class IndentWriteActivity extends BaseActivity implements ActionSheet.ActionSheetListener{
    @Bind(R.id.current_server_car_icon) CircleImageView current_car_icon;
    @Bind(R.id.car_type) TextView carType;
    @Bind(R.id.refuel_type) TextView refuelType;
    @Bind(R.id.refuel_time) TextView refuelTime;
    @Bind(R.id.refuel_price) TextView refuelPrice;
    @Bind(R.id.money) EditText moeny;
    @Bind(R.id.fuel_count) EditText fuelCount;
    @Bind(R.id.sum_money) TextView sumMoney;

    private RefuelStationInfo stationInfo;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private String [] refuelItems;
    private float price;
    private static final int UNFINISHED = 0;
    private static final int ALL = 1;

    DecimalFormat decimalFormat=new DecimalFormat(".00");

    @Override
    protected int getLayoutId() {
        return R.layout.activity_indent_write;
    }

    @Override
    public void initView() {
        setTitle("预约加油");
        carType.setText(MyApplication.getCurrentServerCar().getCarBrand()+MyApplication.getCurrentServerCar().getCarType());
        refuelTime.setText(currentYear+"-"+currentMonth+"-"+currentDay);
        ImageLoader.getInstance().displayImage(ConstantValue.CAR_PHOTO_URL+MyApplication.getCurrentServerCar().getCarPhoto(),current_car_icon);
        refuelType.setText(refuelItems[0]);
        price = stationInfo.getPrice().get(refuelItems[0]);
        refuelPrice.setText(price+"元/升");
        moeny.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    if (!TextUtils.isEmpty(moeny.getText())){
                        sumMoney.setText(moeny.getText().toString());
                        float countRefuel = Float.parseFloat(sumMoney.getText().toString())/price;
                        fuelCount.setText(decimalFormat.format(countRefuel));
                    }
                }
            }
        });
        fuelCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    if (!TextUtils.isEmpty(fuelCount.getText())){
                        float countMony = Float.parseFloat(fuelCount.getText().toString())*price;
                        moeny.setText(decimalFormat.format(countMony));
                        sumMoney.setText(decimalFormat.format(countMony));
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        stationInfo = (RefuelStationInfo) getIntent().getSerializableExtra("stationInfo");
        ArrayList<String> keys = getIntent().getStringArrayListExtra("key");
        if (keys!=null){
            int size = keys.size();
            refuelItems = new String[size];
            for (int i=0;i<size;i++){
                refuelItems[i]=keys.get(i);
            }
        }
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR) ;
        currentMonth = c.get(Calendar.MONTH)+1;
        currentDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.make_order,R.id.select_time,R.id.select_fuel_type})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.select_time:
                showDate();
                break;
            case R.id.select_fuel_type:
                ActionSheet.createBuilder(this,getSupportFragmentManager())
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles(refuelItems)
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.make_order:
                intent();
                break;
        }
    }

    private void initRayMenu(RayMenu rayMenu,int[] itemDrawables){
        final int itemCount = itemDrawables.length;
        viewHolder viewHolder = null;
        for (int i = 0;i<itemCount;i++){
            viewHolder = new viewHolder();
            View view = LayoutInflater.from(this).inflate(R.layout.ray_menu_item,null);
            viewHolder.itemIcon = (ImageView) view.findViewById(R.id.image_item);
            viewHolder.itemIcon.setImageResource(itemDrawables[i]);
            final int position = i;
            rayMenu.addItem(view, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(IndentWriteActivity.this,OrderManageActivity.class);
                    switch (position){
                        case 0:
                            intent.putExtra("orderType",UNFINISHED);
                            break;
                        case 1:
                            intent.putExtra("orderType",ALL);
                            break;
                    }
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }


    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        refuelType.setText(refuelItems[index]);
        price = stationInfo.getPrice().get(refuelItems[index]);
        refuelPrice.setText(price+"元/升");
    }

    static class viewHolder{
        public ImageView itemIcon;
    }

    public void showDate(){
        final AlertDialog dialog = new AlertDialog.Builder(IndentWriteActivity.this).create();
        dialog.show();
        DatePicker picker = new DatePicker(IndentWriteActivity.this);
        picker.setDate(currentYear,currentMonth);
        picker.setMode(DPMode.SINGLE);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                refuelTime.setText(date);
                dialog.dismiss();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(picker, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void intent(){
       if (check()){
           OrderRefuel orderInfo = new OrderRefuel();
           orderInfo.setOrderNum(createOrderNum());
           orderInfo.setCarName(MyApplication.getCurrentServerCar().getCarBrand());
           orderInfo.setCarNum(MyApplication.getCurrentServerCar().getCarNumber());
           orderInfo.setRefuelType(refuelType.getText().toString());
           orderInfo.setAddress(stationInfo.getAddress());
           orderInfo.setFuelName(stationInfo.getName());
           orderInfo.setFuelCount(Float.parseFloat(fuelCount.getText().toString()));
           orderInfo.setMoney(Float.parseFloat(moeny.getText().toString()));
           orderInfo.setOrderDate(refuelTime.getText().toString());
           orderInfo.setLantitude(Double.valueOf(stationInfo.getLat()));
           orderInfo.setLontitude(Double.valueOf(stationInfo.getLon()));
           Intent it = new Intent(this,QuickMarkActivity.class);
           Bundle bundle = new Bundle();
           bundle.putSerializable("orderInfo",orderInfo);
           it.putExtras(bundle);
           startActivity(it);
           finish();
       }
    }

    private boolean check(){
        if (TextUtils.isEmpty(moeny.getText())&&TextUtils.isEmpty(fuelCount.getText())){
            Toast.makeText(this,"请填写加油金额或加油量",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String createOrderNum(){
        //格式化时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = dateFormat.format(new Date());
        //处理6位随机数
        Random random = new Random();
        int number = random.nextInt(99999)+1;//0-99999
        DecimalFormat decimalFormat = new DecimalFormat("000000");
        String numFormat = decimalFormat.format(number);
        return time+numFormat;
    }
}
