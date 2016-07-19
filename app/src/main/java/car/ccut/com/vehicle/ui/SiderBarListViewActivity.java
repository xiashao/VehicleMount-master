package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.bean.car.Car;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.CharacterParser;
import car.ccut.com.vehicle.util.PinyinComparator;
import car.ccut.com.vehicle.view.SideBar;

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
 * Created by WangXin on 2016/4/3 0003.
 */
public class SiderBarListViewActivity extends AppCompatActivity{

    @Bind(R.id.list_view)
    ListView listView;
    @Bind(R.id.sidrbar)
    SideBar sideBar;

    private List<Car> carData;
    private QuickAdapter<Car> adapter;
    private String carTypeId;
    public DelayedProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.side_bar_listview);
        ButterKnife.bind(this);
        dialog = DelayedProgressDialog.make(this,null,"数据加载中,请稍后..",true,true);
        initData();
        initView();
    }
    public void showData(){
        Collections.sort(toUpperCase(carData), new PinyinComparator());
        listView.setAdapter(adapter = new QuickAdapter<Car>(SiderBarListViewActivity.this, R.layout.side_bar_listview_item, carData) {
            @Override
            protected void convert(BaseAdapterHelper helper, Car item) {
                int section = getSectionForPosition(helper.getPosition());
                if (helper.getPosition() == getPositionForSection(section)) {
                    helper.setVisible(R.id.catalog, true);
                    helper.setText(R.id.catalog, item.getFirstLetter());
                } else {
                    helper.setVisible(R.id.catalog, false);
                }
                if (item.getName()!=null){
                    helper.setText(R.id.item, item.getName());
                }else if (item.getCarTypeName()!=null){
                    helper.setText(R.id.item, item.getCarTypeName());
                }

            }
        });
    }
    public void initView() {
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("car",carData.get(i));
                it.putExtras(bundle);
                setResult(RESULT_OK, it);
                finish();
            }
        });
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return carData.get(position).getFirstLetter().charAt(0);
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < carData.size(); i++) {
            String sortStr = carData.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    public void initData() {
        carTypeId = getIntent().getStringExtra("flag");
        dialog.show();
        if (carTypeId==null){
            requestData(null);
        }else {
            requestData(carTypeId);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        MyApplication.getHttpQueues().cancelAll("getCarInfo");
    }


    private List<Car> toUpperCase(List<Car> cars){
        for (int i =0;i<cars.size();i++){
            String pinyin = null;
            String sortString = null;
            if (cars.get(i).getName()!=null){
                pinyin = CharacterParser.getInstance().getSelling(cars.get(i).getName());
            }else if (cars.get(i).getCarTypeName()!=null){
                pinyin = CharacterParser.getInstance().getSelling(cars.get(i).getCarTypeName());
            }
            if (pinyin!=null){
                sortString = pinyin.substring(0, 1).toUpperCase();
            }
            if(sortString.matches("[A-Z]")){
                cars.get(i).setFirstLetter(sortString.toUpperCase());
            }else{
                cars.get(i).setFirstLetter("#");
            }
        }
        return cars;
    }

    private void requestData(final String carTypeId){
        String url=null;
        Map params = new HashMap();
        if (carTypeId!=null){
            params.put("carName",carTypeId);
            url=ConstantValue.REQUEST_CAR_TYPE+"/"+carTypeId;
        }else {
            url = ConstantValue.REQUEST_CAR_NAME;
        }
        JsonRequestWithAuth<AjaxResponse> getCarInfo = new JsonRequestWithAuth<AjaxResponse>(url, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        Gson gson = new Gson();
                        dialog.dismiss();
                        if (response.isOk()){
                            if (carTypeId==null){
                                carData = gson.fromJson(gson.toJson(response.getResponseData().get("brandList")), new TypeToken<List<Car>>(){}.getType());
                            }else {
                                carData = gson.fromJson(gson.toJson(response.getResponseData().get("carTypeList")), new TypeToken<List<Car>>(){}.getType());
                            }
                            showData();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MyApplication.getHttpQueues().add(getCarInfo);
    }
}
