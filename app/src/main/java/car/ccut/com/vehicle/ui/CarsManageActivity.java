package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.car.CarInfo;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.zxing.activity.CaptureActivity;
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
 * Created by WangXin on 2016/3/12 0012.
 */
public class CarsManageActivity extends BaseActivity implements ActionSheet.ActionSheetListener{

    @Bind(R.id.list_view)
    ListView listView;
    @Bind(R.id.tv_add)
    TextView tvAdd;

    private List<CarInfo> carInfos = new ArrayList<CarInfo>();
    private QuickAdapter<CarInfo> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cars_manage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("requestCarInfo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initView() {
        setTitle("车辆管理");
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setBackgroundResource(R.drawable.add_bg);
    }

    public void showList(final List<CarInfo> carInfoList){
        if (carInfoList!=null&&carInfoList.size()>0) {
            mAdapter = new QuickAdapter<CarInfo>(this, R.layout.cars_manage_item, carInfoList) {
                @Override
                protected void convert(BaseAdapterHelper helper, CarInfo item) {
                    CircleImageView carPhoto = (CircleImageView) helper.getView().findViewById(R.id.car_photo);
                    ImageLoader.getInstance().displayImage(ConstantValue.CAR_PHOTO_URL + item.getCarPhoto(), carPhoto);
                    helper.setText(R.id.car_num, item.getCarNumber());
                }
            };
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent it = new Intent(CarsManageActivity.this, CarInfoDetailsActivity.class);
                    it.putExtra("carId", carInfoList.get(i).getId());
                    startActivity(it);
                }
            });
        }
    }

    public void addCar(){
        //  添加车辆
        ActionSheet.createBuilder(CarsManageActivity.this,getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setCancelableOnTouchOutside(true)
                .setOtherButtonTitles("扫码添加","手动添加")
                .setListener(CarsManageActivity.this)
                .show();
    }

    @Override
    public void initData() {
        showWaitDialog();
        if (carInfos!=null){
            carInfos.clear();
        }
        requestCarInfo(MyApplication.currentUser.getId());
    }

    private void requestCarInfo(String userId){
        Map params = new HashMap();
        params.put("userId",userId);
        JsonRequestWithAuth<AjaxResponse> requestCarInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.REQUEST_ALL_CAR_BASE_INFO, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        hideWaitDialog();
                        if (response.isOk()){
                            Gson gson = new Gson();
                            carInfos = gson.fromJson(gson.toJson(response.getResponseData().get("carInfoList")),new TypeToken<List<CarInfo>>(){}.getType());
                            if (carInfos!=null&&carInfos.size()>0){
                                showList(carInfos);
                            }else {
                                Intent intent = new Intent(CarsManageActivity.this,AddCarActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().finishAllActivity();
                                finish();
                            }
                        }else {
                            Toast.makeText(CarsManageActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(requestCarInfo);
        MyApplication.getHttpQueues().start();
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
                addCar();
                break;
        }
    }
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        Intent it;
        switch (index){
            case 0:
                it = new Intent(this,CaptureActivity.class);
                break;
            case 1:
                it = new Intent(this,AddCarActivity.class);
                it.putExtra("request","request");
                break;
            default:
                return;
        }
        it.putExtra("request","manageAdd");
        startActivity(it);
    }
}
