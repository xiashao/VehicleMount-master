package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.base.MultipartRequest;
import car.ccut.com.vehicle.bean.CheckRefuelType;
import car.ccut.com.vehicle.bean.car.Car;
import car.ccut.com.vehicle.bean.car.CarBodyLevel;
import car.ccut.com.vehicle.bean.car.CarInfo;
import car.ccut.com.vehicle.bean.car.MarkCar;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.photopick.PhotoPickerActivity;
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
 * Created by WangXin on 2016/4/2 0002.
 */
public class AddCarActivity extends BaseActivity{
    @Bind(R.id.car_name)TextView carBrand;
    @Bind(R.id.car_type)TextView carType;
    @Bind(R.id.car_num)
    EditText carNum;
    @Bind(R.id.engine_num) EditText engineNum;
    @Bind(R.id.body_level) TextView bodyLevel;
    @Bind(R.id.mileage) EditText mileage;
    @Bind(R.id.refuel_type) TextView refuelType;
    @Bind(R.id.car_photo)
    CircleImageView carPhoto;

    private CarInfo addCar = new CarInfo();
    private static final int REQUEST_PHOTO = 9002;
    //请求汽车品牌
    public static final int REQUEST_CODE_CARNAME = 3;
    //请求汽车品牌
    public static final int REQUEST_CODE_CARTYPE = 4;
    //请求燃油类型
    public static final int REQUEST_CODE_REFUELTYPE = 5;

    private String carTypeId;
    private String flag;
    private ActionSheet.ActionSheetListener selectBodyLevel;
    private String[] bodyLevelItems;
    private List<CheckRefuelType> refuelTypes;
    private List<CarBodyLevel> bodyLevels;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_car;
    }

    @Override
    public void initView() {
        setTitle("添加车辆");
        selectBodyLevel = new ActionSheet.ActionSheetListener() {
            @Override
            public void onDismiss(ActionSheet actionSheet, boolean b) {

            }

            @Override
            public void onOtherButtonClick(ActionSheet actionSheet, int i) {
                bodyLevel.setText(bodyLevelItems[i]);
                addCar.setBodyLevel(bodyLevels.get(i).getId());
            }
        };
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("insertRequest");
        MyApplication.getHttpQueues().cancelAll("requestBodyLevel");
    }

    @Override
    public void initData() {
        flag = getIntent().getStringExtra("request");
        String type = getIntent().getStringExtra("type");
        if (type!=null&&type.equals("mark")){
            String result = getIntent().getStringExtra("result");
            if (result!=null){
                System.out.println("++++++"+result);
                try {
                    MarkCar carInfo = new Gson().fromJson(result,new TypeToken<MarkCar>(){}.getType());
                    if (carInfo!=null){
                        addCar.setUserId(MyApplication.currentUser.getId());
                        addCar.setCarBrand(carInfo.getCarBrand().getId());
                        carBrand.setText(carInfo.getCarBrand().getName());
                        addCar.setCarType(carInfo.getCarBrand().getCarTypeId());
                        carType.setText(carInfo.getCarBrand().getCarTypeName());
                        addCar.setCarNumber(carInfo.getCarNumber());
                        carNum.setText(carInfo.getCarNumber());
                        addCar.setCarMachineNo(carInfo.getCarMachineNo());
                        engineNum.setText(carInfo.getCarMachineNo());
                        addCar.setBodyLevel(carInfo.getCarBodyLevel().getId());
                        bodyLevel.setText(carInfo.getCarBodyLevel().getLevel());
                        addCar.setExpendNumber(carInfo.getExpendNumber());
                        mileage.setText(carInfo.getExpendNumber());
                        addCar.getRefuelType().clear();
                        addCar.getRefuelType().addAll(carInfo.getCheckRefuelTypes());
                        String showText = "";
                        for (CheckRefuelType refuelType:carInfo.getCheckRefuelTypes()){
                            showText+=refuelType.getName()+";";
                        }
                        if (!TextUtils.isEmpty(showText)){
                            refuelType.setText(showText.substring(0,showText.length()-1));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.select_car_name,R.id.select_car_type,R.id.select_body_level,R.id.select_refuel_type,R.id.car_photo,R.id.add})
    public void onClick(View view) {
        int id = view.getId();
        Intent it;
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.select_car_name:
                it = new Intent(AddCarActivity.this,SiderBarListViewActivity.class);
                startActivityForResult(it,REQUEST_CODE_CARNAME);
                break;
            case R.id.select_car_type:
                if (TextUtils.isEmpty(carTypeId)){
                    Toast.makeText(this,"请选择车辆品牌",Toast.LENGTH_SHORT).show();
                }else {
                    it = new Intent(AddCarActivity.this,SiderBarListViewActivity.class);
                    it.putExtra("flag",carTypeId);
                    startActivityForResult(it,REQUEST_CODE_CARTYPE);
                }
                break;
            case R.id.select_body_level:
                getCarBodyLevel();
                break;
            case R.id.select_refuel_type:
                it = new Intent(AddCarActivity.this,SelectRefuelTypeActivity.class);
                startActivityForResult(it,REQUEST_CODE_REFUELTYPE);
                break;
            case R.id.car_photo:
                selectPhoto();
                break;
            case R.id.add:
                submit();
                break;
        }
    }

    public void getCarBodyLevel(){
        showWaitDialog();
        Map params = new HashMap();
        JsonRequestWithAuth<AjaxResponse> requestBodyLevel = new JsonRequestWithAuth<>(ConstantValue.REQUEST_ALL_CAR_BODY_LEVEL, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        if (response.isOk()){
                            hideWaitDialog();
                            Gson gson = new Gson();
                            bodyLevels = gson.fromJson(gson.toJson(response.getResponseData().get("allCarBodyLevel")),new TypeToken<List<CarBodyLevel>>(){}.getType());
                            if (bodyLevels!=null){
                                int size = bodyLevels.size();
                                bodyLevelItems = new String[size];
                                for (int i = 0;i<size;i++){
                                    bodyLevelItems[i] = bodyLevels.get(i).getLevel();
                                }
                                sheetBottom(bodyLevelItems,selectBodyLevel);
                            }else {
                                Toast.makeText(AddCarActivity.this,"数据加载失败,请重试",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AddCarActivity.this,"数据加载失败,请重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(requestBodyLevel);
        MyApplication.getHttpQueues().start();
    }

    private void selectPhoto(){
        Intent intent = new Intent(AddCarActivity.this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
        startActivityForResult(intent, REQUEST_PHOTO);
    }

    private boolean check(){
        if (TextUtils.isEmpty(addCar.getCarBrand())){
            Toast.makeText(this,"请选择汽车品牌",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(addCar.getCarType())){
            Toast.makeText(this,"请选择汽车类型",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(carNum.getText())){
            Toast.makeText(this,"请输入车牌号码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(engineNum.getText())){
            Toast.makeText(this,"请输入发动机号码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(addCar.getBodyLevel())){
            Toast.makeText(this,"请选择车身级别",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mileage.getText())){
            Toast.makeText(this,"请输入行驶里程",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(refuelType.getText())){
            Toast.makeText(this,"请选燃油类型",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submit(){
        if (check()){
            addCar.setCarNumber(carNum.getText().toString());
            addCar.setCarMachineNo(engineNum.getText().toString());
            addCar.setExpendNumber(mileage.getText().toString());
            addCar.setUserId(MyApplication.getCurrentUser().getId());
            insertRequest(addCar);
        }
    }

    private void sheetBottom(String[] items,ActionSheet.ActionSheetListener listener){
        ActionSheet.createBuilder(this,getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(items)
                .setCancelableOnTouchOutside(true)
                .setListener(listener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){

            switch (requestCode){
                case REQUEST_PHOTO:
                    if (data!=null){
                        List<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                        addCar.setCarPhoto(photos.get(0));
                        ImageLoader.getInstance().displayImage("file://" + addCar.getCarPhoto(),carPhoto);
                    }
                    break;
                case REQUEST_CODE_CARNAME:
                    Car carBrandResult = (Car) data.getSerializableExtra("car");
                    if (carBrandResult!=null){
                        addCar.setCarBrand(carBrandResult.getId());
                        carTypeId = carBrandResult.getCarTypeId();
                        carBrand.setText(carBrandResult.getName());
                    }
                    break;
                case REQUEST_CODE_CARTYPE:
                    Car carTypeResult = (Car) data.getSerializableExtra("car");
                    if (carTypeResult!=null){
                        addCar.setCarType(carTypeResult.getId());
                        carType.setText(carTypeResult.getCarTypeName());
                    }
                    break;
                case REQUEST_CODE_REFUELTYPE:
                    refuelTypes = (List<CheckRefuelType>) data.getSerializableExtra("fuelTypeList");
                    String showText="";
                    if (refuelTypes!=null){
                        addCar.getRefuelType().clear();
                        addCar.getRefuelType().addAll(refuelTypes);
                        for (CheckRefuelType refuelType:refuelTypes){
                            showText+=refuelType.getName()+";";
                        }
                        if (!TextUtils.isEmpty(showText)){
                            refuelType.setText(showText.substring(0,showText.length()-1));
                        }
                    }
                    refuelTypes.clear();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void insertRequest(final CarInfo carInfo){
        Map<String, String> params = new HashMap<>();
        params.put("userId", carInfo.getUserId());
        params.put("carNumber", carInfo.getCarNumber());
        params.put("carBrand", carInfo.getCarBrand());
        params.put("carType", carInfo.getCarType());
        params.put("carMachineNo", carInfo.getCarMachineNo());
        params.put("bodyLevel", carInfo.getBodyLevel());
        params.put("stringRefuelType", listToString(carInfo.getRefuelType()));
        params.put("expendNumber", carInfo.getExpendNumber());
        params.put("lampStatus",ConstantValue.NORMAL+"");
        params.put("enginePerformance",ConstantValue.NORMAL+"");
        params.put("transmissionPerformance",ConstantValue.NORMAL+"");
        params.put("fuelAmount","100%");
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, ConstantValue.REQUEST_INSRT_CAR, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.networkResponse);
            }
        }, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                if (response.isOk()) {
                    if (MyApplication.getCurrentServerCar().getCarNumber()==null||"".equals(MyApplication.getCurrentServerCar().getCarNumber())){
                        MyApplication.setCurrentServerCar(addCar);
                    }
                    Intent it = null;
                    if (flag==null){
                        it = new Intent(AddCarActivity.this,MapActivity.class);
                        it.putExtra("type","type");
                    }else {
                        onBackPressed();
                    }
                    if (it!=null){
                        startActivity(it);
                        finish();
                    }
                }else {
                    Toast.makeText(AddCarActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                }
                hideWaitDialog();
            }
        }, "file", new File(carInfo.getCarPhoto()),params);
        MyApplication.getHttpQueues().add(multipartRequest);
        MyApplication.getHttpQueues().start();
    }

    public String listToString(List<CheckRefuelType> list) {
        String result="";
        if (list!=null){
            for (int i = 0;i<list.size();i++){
                result+=list.get(i).getId()+",";
            }
        }
        return result.substring(0,result.length()-1);
    }
}
